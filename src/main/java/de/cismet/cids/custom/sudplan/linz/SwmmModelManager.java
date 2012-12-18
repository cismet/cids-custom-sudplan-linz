/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.linz;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;

import at.ac.ait.enviro.sudplan.clientutil.SudplanSOSHelper;
import at.ac.ait.enviro.sudplan.clientutil.SudplanSPSHelper;
import at.ac.ait.enviro.sudplan.util.PropertyNames;
import at.ac.ait.enviro.tsapi.timeseries.TimeSeries;
import at.ac.ait.enviro.util.text.ISO8601DateFormat;

import com.vividsolutions.jts.geom.Envelope;

import org.apache.log4j.Logger;

import org.codehaus.jackson.map.ObjectMapper;

import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import de.cismet.cids.custom.sudplan.*;
import de.cismet.cids.custom.sudplan.linz.wizard.SwmmPlusEtaWizardAction;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

/**
 * DOCUMENT ME!
 *
 * @author   pd
 * @version  $Revision$, $Date$
 */
public class SwmmModelManager extends AbstractAsyncModelManager {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(SwmmModelManager.class);
    public static final String TABLENAME_CSOS = SwmmPlusEtaWizardAction.TABLENAME_CSOS;
    public static final String TABLENAME_LINZ_SWMM_RESULT = "linz_swmm_result";
    public static final int MAX_STEPS = 4;

    //~ Instance fields --------------------------------------------------------

    private final String modelSosEndpoint = "http://sudplan.ait.ac.at:8081/";
    private SudplanSPSHelper.Task spsTask;

    //~ Methods ----------------------------------------------------------------

    @Override
    protected CidsBean createOutputBean() throws IOException {
        if (!isFinished()) {
            throw new IllegalStateException("cannot create outputbean when not finished yet"); // NOI18N
        }

        if (!(getWatchable() instanceof SwmmWatchable)) {
            throw new IllegalStateException("cannot create output if there is no valid watchable ("
                        + getWatchable().getClass() + ")"); // NOI18N
        }

        final SwmmWatchable swmmWatchable = (SwmmWatchable)this.getWatchable();
        final String spsRunId = swmmWatchable.getSwmmRunInfo().getSpsTaskId();

        try {
            final SwmmInput swmmInput = (SwmmInput)this.getUR();
            final String swmmRunName = this.getCidsBean().getProperty("name").toString();
            final int swmmRun = (Integer)this.getCidsBean().getProperty("id");

            LOG.info("creating SWMMOutput Bean for SWMM RUN '" + cidsBean + "' ("
                        + swmmRun + ")'"); // NOI18N

            final SwmmOutput swmmOutput = swmmWatchable.getSwmmOutput();

            if (swmmOutput.getSwmmProject() == -1) {
                LOG.warn("SWMM Project id of '" + swmmOutput + "' is -1!");
            }

            // copy values from input (also needed for ETA calculation)
            swmmOutput.setSwmmProject(swmmInput.getSwmmProject());
            swmmOutput.setSwmmRun(swmmRun);
            swmmOutput.setSwmmRunName(swmmRunName);

            // update overflows project ids also
            if ((swmmOutput.getCsoOverflows() != null) && !swmmOutput.getCsoOverflows().isEmpty()) {
                for (final CsoOverflow csoOverflow : swmmOutput.getCsoOverflows().values()) {
                    csoOverflow.setSwmmProject(swmmOutput.getSwmmProject());
                }
            }

            final CidsBean swmmModelOutput = SMSUtils.createModelOutput("SWMM Results "
                            + swmmOutput.getSwmmRunName(), // NOI18N
                    swmmOutput,
                    SMSUtils.Model.SWMM);

            this.updateCSOs(swmmOutput);
            return swmmModelOutput.persist();
        } catch (final Exception e) {
            final String message = "cannot get results for SPS SWMM run: " + spsRunId; // NOI18N
            LOG.error(message, e);
            this.fireBroken(message);
            throw new IOException(message, e);
        }
    }

    @Override
    protected String getReloadId() {
        try {
            final SwmmInput swmmInput = (SwmmInput)getUR();
            final String reloadId = "local.linz." + swmmInput.getSwmmProject()
                        + ".swmm.scenario." + this.cidsBean.getProperty("id"); // NOI18N
            if (LOG.isDebugEnabled()) {
                LOG.debug("SWMM Reload ID: " + reloadId);
            }
            return reloadId;
        } catch (final Exception e) {
            LOG.warn("cannot fetch reload id", e);                             // NOI18N
            return null;
        }
    }

    @Override
    public SwmmRunInfo getRunInfo() {
        return SMSUtils.getRunInfo(cidsBean, SwmmRunInfo.class);
    }

    @Override
    protected void prepareExecution() throws IOException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("preparaing the Execution of SWMM Run #" + cidsBean.getProperty("id")); // NOI18N
        }

        fireProgressed(
            0,
            MAX_STEPS,
            NbBundle.getMessage(SwmmModelManager.class,
                "SwmmModelManager.prepareExecution().progress.prepare"));

        final SwmmInput swmmInput = (SwmmInput)this.getUR();
        final SwmmRunInfo swmmRunInfo = new SwmmRunInfo();

        LOG.info("executing SWMM Run for model " + swmmInput.getInpFile());

        assert !swmmInput.getTimeseriesURLs().isEmpty() : "improperly configured swmm run, no timeseries configured";
        final TimeseriesRetrieverConfig config = TimeseriesRetrieverConfig.fromUrl(swmmInput.getTimeseriesURLs(0));
        LOG.info("STEP 1: retrieving timeseries from " + swmmInput.getTimeseriesURLs(0));

        TimeSeries rainTS;
        if (config.getProtocol().equals(TimeseriesRetrieverConfig.PROTOCOL_TSTB)) {
            LOG.info("downloading timeseries from SOS: " + config);

            fireProgressed(
                1,
                MAX_STEPS,
                NbBundle.getMessage(
                    SwmmModelManager.class,
                    "SwmmModelManager.prepareExecution().progress.download.sos"));

            final SudplanSOSHelper sensorSOSHelper = new SudplanSOSHelper(config.getLocation().toString());
            if (LOG.isDebugEnabled()) {
                LOG.debug("Download timeseries data for offering '" + config.getOffering()
                            + "', time intervall '" + config.getInterval() + "'");
            }

            rainTS = sensorSOSHelper.getTimeseries(config.getOffering(), config.getInterval());
            if (LOG.isDebugEnabled()) {
                LOG.debug("Downloaded {} values" + rainTS.getTimeStamps().size());
            }
        } else if (config.getProtocol().equals(TimeseriesRetrieverConfig.PROTOCOL_DAV)) {
            LOG.info("downloading timeseries from WEBDAV: " + config);

            fireProgressed(
                1,
                MAX_STEPS,
                NbBundle.getMessage(
                    SwmmModelManager.class,
                    "SwmmModelManager.prepareExecution().progress.download.webdav"));

            try {
                final Future<TimeSeries> rainTsFuture = TimeseriesRetriever.getInstance().retrieve(config);
                rainTS = rainTsFuture.get();
                if (LOG.isDebugEnabled()) {
                    LOG.debug("finished downloading timeseries from WEBDAV");
                }
            } catch (Exception e) {
                final String message = "Could not download rain timeseries from '"
                            + config.getProtocol() + "'";
                LOG.error(message, e);
                this.fireBroken(message);
                throw new IOException(message, e);
            }
        } else {
            final String message = "Unsupported timeseries protocol: '" + config.getProtocol() + "'";
            LOG.error(message);
            this.fireBroken(message);
            throw new IOException(message);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Upload rain timeseries as model input to " + modelSosEndpoint);
        }

        fireProgressed(
            2,
            MAX_STEPS,
            NbBundle.getMessage(SwmmModelManager.class,
                "SwmmModelManager.prepareExecution().progress.upload"));

        final SudplanSOSHelper modelSOSHelper = new SudplanSOSHelper(modelSosEndpoint);
        // Creating a new timeseries datapoint on the SOS-T is a 2 step process:
        // 1. createDatapoint (needs SensorML)
        // 2. putTimeserise (timeseries with all needed properties - and, yes, values too)
        //
        // Get SensorML from somewhere..
        final Map<String, Object> dpProps = new HashMap<String, Object>();
        final BufferedReader r = new BufferedReader(new InputStreamReader(
                    SwmmModelManager.class.getResourceAsStream("smlSensor.xml")));
        final StringBuilder sb = new StringBuilder();
        String s;
        while ((s = r.readLine()) != null) {
            sb.append(s);
        }
        r.close();
        final String sensorml = sb.toString();
        rainTS.setTSProperty(TimeSeries.SENSORML, sensorml);

        // Check if all needed properties are available.. They should already be contained in the TimeSeries I got from
        // the SOSLinzSensorServer Additionaly to the normal properties (see ts-docu) a minimum set is for compatibility
        // with SOS and SPS needed: TimeSeries.GEOMETRY, "ts:coordinate_system",....
        if (!rainTS.getTSKeys().contains(TimeSeries.VALUE_OBSERVED_PROPERTY_URNS)) {
            rainTS.setTSProperty(
                TimeSeries.VALUE_OBSERVED_PROPERTY_URNS,
                new String[] {
                    config.getObsProp()
                    // "urn:ogc:def:property:OGC:1.0:precipitation"
                });
            LOG.warn("Inserting missing Timeseries property" + TimeSeries.VALUE_OBSERVED_PROPERTY_URNS);
        }
        if (!rainTS.getTSKeys().contains(PropertyNames.DESCRIPTION)) {
            rainTS.setTSProperty(PropertyNames.DESCRIPTION, "Rain as input to the Linz model");
            LOG.warn("Inserting missing Timeseries property '" + PropertyNames.DESCRIPTION);
        }
        if (!rainTS.getTSKeys().contains(PropertyNames.COORDINATE_SYSTEM)) {
            rainTS.setTSProperty(PropertyNames.COORDINATE_SYSTEM, "EPSG:3423");
            LOG.warn("Inserting missing Timeseries property '" + PropertyNames.COORDINATE_SYSTEM);
        }
        if (!rainTS.getTSKeys().contains(PropertyNames.SPATIAL_RESOLUTION)) {
            rainTS.setTSProperty(
                PropertyNames.SPATIAL_RESOLUTION,
                new Integer[] { 1 }); // Need to be 1 value!!
            LOG.warn("Inserting missing Timeseries property '" + PropertyNames.SPATIAL_RESOLUTION);
        }
        if (!rainTS.getTSKeys().contains(PropertyNames.TEMPORAL_RESOLUTION)) {
            rainTS.setTSProperty(PropertyNames.TEMPORAL_RESOLUTION, "NONE");
            LOG.warn("Inserting missing Timeseries property '" + PropertyNames.TEMPORAL_RESOLUTION + "' = " + "NONE");
        }
        if (!rainTS.getTSKeys().contains(TimeSeries.VALUE_JAVA_CLASS_NAMES)) {
            rainTS.setTSProperty(
                TimeSeries.VALUE_JAVA_CLASS_NAMES,
                new String[] { Float.class.getName() });
            LOG.warn("Inserting missing Timeseries property '" + TimeSeries.VALUE_JAVA_CLASS_NAMES + "' = "
                        + Float.class.getName());
        }
        if (!rainTS.getTSKeys().contains(TimeSeries.VALUE_TYPES)) {
            rainTS.setTSProperty(
                TimeSeries.VALUE_TYPES,
                new String[] { TimeSeries.VALUE_TYPE_NUMBER });
            LOG.warn("Inserting missing Timeseries property '" + TimeSeries.VALUE_TYPES + "'");
        }
        if (!rainTS.getTSKeys().contains(TimeSeries.VALUE_UNITS)) {
            rainTS.setTSProperty(
                TimeSeries.VALUE_UNITS,
                new String[] { "urn:ogc:def:uom:OGC:mm" });
            LOG.warn("Inserting missing Timeseries property '" + TimeSeries.VALUE_UNITS + "' = urn:ogc:def:uom:OGC:mm");
        }
        if (!rainTS.getTSKeys().contains(TimeSeries.GEOMETRY)) {
            rainTS.setTSProperty(TimeSeries.GEOMETRY, new Envelope(14.18, 14.38, 48.24, 48.34));
            LOG.warn("Inserting missing Timeseries property '" + TimeSeries.GEOMETRY
                        + "' = 14.18, 14.38, 48.24, 48.34");
        }
        if (!rainTS.getTSKeys().contains(TimeSeries.AVAILABLE_DATA_MIN)) {
            rainTS.setTSProperty(TimeSeries.AVAILABLE_DATA_MIN, rainTS.getTimeStamps().first().asDate());
            LOG.warn("Inserting missing Timeseries property '" + TimeSeries.AVAILABLE_DATA_MIN + "' = "
                        + rainTS.getTimeStamps().first().asDate());
        }
        if (!rainTS.getTSKeys().contains(TimeSeries.AVAILABLE_DATA_MAX)) {
            rainTS.setTSProperty(TimeSeries.AVAILABLE_DATA_MAX, rainTS.getTimeStamps().last().asDate());
            LOG.warn("Inserting missing Timeseries property '" + TimeSeries.AVAILABLE_DATA_MAX + "' = "
                        + rainTS.getTimeStamps().last().asDate());
        }

        rainTS.setTSProperty(PropertyNames.DESCRIPTION, "Data from " + config.getOffering());
        final String modelOffering = modelSOSHelper.putNewTimeseries(rainTS);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Uploaded model input");

            LOG.debug("connecting to model SPS " + swmmRunInfo.getSpsUrl() + " and executing model "
                        + swmmRunInfo.getModelName());
        }

        fireProgressed(
            3,
            MAX_STEPS,
            NbBundle.getMessage(SwmmModelManager.class,
                "SwmmModelManager.prepareExecution().progress.dispatch"));

        final SudplanSPSHelper modelSPSHelper = new SudplanSPSHelper(swmmRunInfo.getSpsUrl());
        final DateFormat isoDf = new ISO8601DateFormat();
        this.spsTask = modelSPSHelper.createTask(swmmRunInfo.getModelName());

        try {
            final String startDate = isoDf.format(swmmInput.getStartDate());
            if (LOG.isDebugEnabled()) {
                LOG.debug("start date: " + swmmInput.getStartDate() + " (" + startDate + ")");
            }
            spsTask.setParameter("start", startDate);

            final String endDate = isoDf.format(swmmInput.getEndDate());
            if (LOG.isDebugEnabled()) {
                LOG.debug("end date: " + swmmInput.getEndDate() + " (" + endDate + ")");
            }

            spsTask.setParameter("end", endDate);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            this.fireBroken(e.getMessage());
            throw new IOException(e.getMessage(), e);
        }

        spsTask.setParameter("dat", modelOffering);

        if (swmmInput.getInpFile().toLowerCase().endsWith(".inp")) {
            final String inpFile = swmmInput.getInpFile()
                        .substring(
                            0,
                            swmmInput.getInpFile().toLowerCase().lastIndexOf(".inp"));

            LOG.warn("SPS does not like the file extension.INP, removing (" + inpFile + ")");
            spsTask.setParameter("inp", inpFile);
        } else {
            spsTask.setParameter("inp", swmmInput.getInpFile());
        }
        // eta calculation is now performed on client side, this is just used for r720,1
        spsTask.setParameter("eta", "linz_v1");

        // and start the task
        spsTask.start();

        swmmRunInfo.setSpsTaskId(spsTask.getTaskID());
        if (LOG.isDebugEnabled()) {
            LOG.debug("SWMM Model run started with SPS Task id" + swmmRunInfo.getSpsTaskId());
        }

        fireProgressed(
            4,
            MAX_STEPS,
            NbBundle.getMessage(SwmmModelManager.class,
                "SwmmModelManager.prepareExecution().progress.save"));

        try {
            final ObjectMapper mapper = new ObjectMapper();
            final StringWriter writer = new StringWriter();

            mapper.writeValue(writer, swmmRunInfo);
            cidsBean.setProperty("runinfo", writer.toString()); // NOI18N
            cidsBean = cidsBean.persist();
            if (LOG.isDebugEnabled()) {
                LOG.debug("SWMM RunInfo for SPS Task '" + swmmRunInfo.getSpsTaskId() + "' of SWMM Run '"
                            + cidsBean.getMetaObject().getName() + "' saved");
            }
        } catch (final Exception ex) {
            final String message = "Cannot store SWMM RunInfo for SPS Task '" + swmmRunInfo.getSpsTaskId()
                        + "' of SWMM Run '"
                        + cidsBean.getMetaObject().getName() + "'";
            LOG.error(message, ex);
            this.fireBroken(message);
            throw new IOException(message, ex);
        }

        // now set to indeterminate
        fireProgressed(
            -1,
            -1,
            NbBundle.getMessage(
                SwmmModelManager.class,
                "SwmmModelManager.prepareExecution().progress.running",
                swmmRunInfo.getSpsTaskId()));
    }

    @Override
    public AbstractModelRunWatchable createWatchable() throws IOException {
        if (cidsBean == null) {
            throw new IllegalStateException("cidsBean not set"); // NOI18N
        }

        final SwmmRunInfo runInfo = this.getRunInfo();
        if ((runInfo == null) || (runInfo.getSpsTaskId() == null)) {
            throw new IllegalStateException("run info not set"); // NOI18N
        }

        if (runInfo.isCanceled() || runInfo.isBroken()) {
            final String message = "SWMM Run '" + cidsBean + "' with SPS Task ID '" + runInfo.getSpsTaskId()
                        + "' is canceled  or broken, ignoring run";
            LOG.warn(message);
            throw new IllegalStateException(message); // NOI18N
        }

        return new SwmmWatchable(this);
    }

    @Override
    protected boolean needsDownload() {
        return true;
    }

    /**
     * updates the model results (swmm) attached to the CSO objects (in the meta data base) and sets the CSO ids of the
     * CSO JSon objects.
     *
     * @param   swmmOutput  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    private void updateCSOs(final SwmmOutput swmmOutput) throws IOException {
        LOG.info("updating " + swmmOutput.getCsoOverflows().size() + " CSOs with model results for SWMM Run {"
                    + swmmOutput.getSwmmRun() + "}");
        final String domain = SessionManager.getSession().getUser().getDomain();
        final MetaClass swmmResultClass = ClassCacheMultiple.getMetaClass(domain, TABLENAME_LINZ_SWMM_RESULT);
        final int swmmProjectId = swmmOutput.getSwmmProject();
        if (swmmProjectId == -1) {
            LOG.warn("swmmProjectId of SWMM Output '" + swmmOutput + "' is -1!");
        }
        final MetaClass csoClass = ClassCacheMultiple.getMetaClass(domain, SwmmPlusEtaWizardAction.TABLENAME_CSOS);
        if (LOG.isDebugEnabled()) {
            LOG.debug("synchronizing CSO IDs with CSO objects in meta database for SWMM Project " + swmmProjectId);
        }

        if (csoClass == null) {
            throw new IOException("cannot fetch CSO metaclass"); // NOI18N
        }

        final StringBuilder sb = new StringBuilder();
        sb.append("SELECT ").append(csoClass.getID()).append(',').append(csoClass.getPrimaryKey()); // NOI18N
        sb.append(" FROM ").append(csoClass.getTableName());                                        // NOI18N

        assert swmmProjectId != -1 : "no suitable swmm project selected";
        sb.append(" WHERE swmm_project = ").append(swmmProjectId);

        final MetaObject[] csoMetaObjects;

        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug("executing SQL statement: \n" + sb);
            }
            csoMetaObjects = SessionManager.getProxy().getMetaObjectByQuery(sb.toString(), 0);
        } catch (final ConnectionException ex) {
            final String message = "cannot get CSO meta objects from database"; // NOI18N
            LOG.error(message, ex);
            throw new IOException(message, ex);
        }

        if (swmmOutput.getCsoOverflows().values().size() == csoMetaObjects.length) {
            for (final MetaObject csoMetaObject : csoMetaObjects) {
                final String name = csoMetaObject.getName();
                if (swmmOutput.getCsoOverflows().containsKey(name)) {
                    try {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("adding swmm results (" + swmmOutput.getSwmmRunName() + ") to CSO '"
                                        + name + "'");
                        }

                        final CsoOverflow csoOverflow = swmmOutput.getCsoOverflows().get(name);
                        final CidsBean csoBean = csoMetaObject.getBean();

                        // 1st update the CSO JSON Bean with the ID of the CSO Meta Object
                        csoOverflow.setCso(csoMetaObject.getId());

                        // 2nd update the CSO Meta Object with the results of the SWMM calculation
                        final CidsBean swmmResultBean = swmmResultClass.getEmptyInstance().getBean();
                        swmmResultBean.setProperty("name", swmmOutput.getSwmmRunName());
                        swmmResultBean.setProperty("swmm_scenario_id", swmmOutput.getSwmmRun());
                        swmmResultBean.setProperty("overflow_frequency", csoOverflow.getOverflowFrequency());
                        swmmResultBean.setProperty("overflow_duration", csoOverflow.getOverflowDuration());
                        swmmResultBean.setProperty("overflow_volume", csoOverflow.getOverflowVolume());

                        final Collection<CidsBean> swmmResults = (Collection)csoBean.getProperty("swmm_results"); // NOI18N
                        swmmResults.add(swmmResultBean);
                        csoBean.persist();
                    } catch (Exception ex) {
                        final String message = "could not update  CSO '" + name + "': " + ex.getMessage();
                        LOG.error(message, ex);
                        this.fireBroken(message);
                        throw new IOException(message, ex);
                    }
                } else {
                    LOG.error("CSO '" + name + "' with id " + csoMetaObject.getId()
                                + " not found");
                }
            }
        } else {
            LOG.warn("CSO map size missmatch: " + swmmOutput.getCsoOverflows().values().size()
                        + " vs. " + csoMetaObjects.length);
        }
    }
}
