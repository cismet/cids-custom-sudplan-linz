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

import org.apache.log4j.Logger;

import org.codehaus.jackson.map.ObjectMapper;

import org.openide.util.NbBundle;

import java.io.IOException;
import java.io.StringWriter;

import java.util.Collection;

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
public class EtaModelManager extends AbstractAsyncModelManager {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(EtaModelManager.class);
    public static final String TABLENAME_LINZ_ETA_RESULT = "linz_eta_result";

    //~ Methods ----------------------------------------------------------------

    @Override
    protected CidsBean createOutputBean() throws IOException {
        if (!isFinished()) {
            throw new IllegalStateException(cidsBean + " cannot create outputbean when not finished yet ("
                        + cidsBean.getProperty("started") + ") [" + this + "]"); // NOI18N
        }

        if (!(getWatchable() instanceof EtaWatchable)) {
            throw new IllegalStateException(cidsBean + " cannot create output if there is no valid watchable ("
                        + getWatchable().getClass() + ")"); // NOI18N
        }

        try {
            final EtaWatchable etaWatchable = (EtaWatchable)this.getWatchable();
            final EtaOutput etaOutput = etaWatchable.getEtaOutput();

            etaOutput.setEtaRun((Integer)this.cidsBean.getProperty("id"));
            etaOutput.setEtaRunName((String)this.cidsBean.getProperty("name"));

            final CidsBean etaModelOutput = SMSUtils.createModelOutput("ETA Results " + etaOutput.getEtaRunName(), // NOI18N
                    etaOutput,
                    SMSUtils.Model.LINZ_ETA);

            final String domain = SessionManager.getSession().getUser().getDomain();
            final MetaClass etaResultClass = ClassCacheMultiple.getMetaClass(domain, TABLENAME_LINZ_ETA_RESULT);

            final CidsBean etaResultBean;
            etaResultBean = etaResultClass.getEmptyInstance().getBean();
            etaResultBean.setProperty("name", etaOutput.getEtaRunName());
            etaResultBean.setProperty("eta_scenario_id", etaOutput.getEtaRun());
            etaResultBean.setProperty("swmm_scenario_id", etaOutput.getSwmmRun());
            etaResultBean.setProperty("eta_sed_required", etaOutput.getEtaSedRequired());
            etaResultBean.setProperty("eta_sed_actual", etaOutput.getEtaSedActual());
            etaResultBean.setProperty("eta_hyd_required", etaOutput.getEtaHydRequired());
            etaResultBean.setProperty("eta_hyd_actual", etaOutput.getEtaHydActual());
            etaResultBean.setProperty("r720", etaOutput.getR720());
            etaResultBean.setProperty("total_overflow_volume", etaOutput.getTotalOverflowVolume());
            etaResultBean.persist();

            final EtaInput etaInput = (EtaInput)this.getUR();
            this.updateSwmmResults(etaInput, etaOutput);

            return etaModelOutput.persist();
        } catch (final Exception e) {
            final String message = "cannot get results for ETA Run '" + this.cidsBean + "': " + e.getMessage();
            LOG.error(message, e);
            this.fireBroken(message);
            throw new IOException(message, e);
        }
    }

    /**
     * updates the model results (swmm) attached to the CSO objects (in the meta data base) and sets the CSO ids of the
     * CSO JSon objects.
     *
     * @param   etaInput   swmmOutput DOCUMENT ME!
     * @param   etaOutput  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    private void updateSwmmResults(final EtaInput etaInput, final EtaOutput etaOutput) throws IOException {
        LOG.info("updating " + etaInput.getCsoOverflows().size() + " CSOs with model results for ETA Run '"
                    + etaOutput.getEtaRunName() + "' {" + etaOutput.getEtaRun() + "} of SWMM Run '"
                    + etaInput.getSwmmRunName() + "' {" + etaInput.getSwmmRun() + "}");
        final String domain = SessionManager.getSession().getUser().getDomain();
        final MetaClass swmmResultClass = ClassCacheMultiple.getMetaClass(
                domain,
                SwmmModelManager.TABLENAME_LINZ_SWMM_RESULT);
        final MetaClass etaResultClass = ClassCacheMultiple.getMetaClass(
                domain,
                EtaModelManager.TABLENAME_LINZ_ETA_RESULT);
        final int swmmRunId = etaInput.getSwmmRun();
        if (swmmRunId == -1) {
            throw new IOException("swmmRunId of ETA Run '"
                        + etaOutput.getEtaRunName() + "' {" + etaOutput.getEtaRun() + "} is -1!");
        }

        if (swmmResultClass == null) {
            throw new IOException("cannot fetch SWMM result metaclass"); // NOI18N
        } else if (etaResultClass == null) {
            throw new IOException("cannot fetch ETA result metaclass");  // NOI18N
        }

        final StringBuilder sb = new StringBuilder();
        sb.append("SELECT ").append(swmmResultClass.getID()).append(',').append(swmmResultClass.getPrimaryKey()); // NOI18N
        sb.append(" FROM ").append(swmmResultClass.getTableName());                                               // NOI18N
        sb.append(" WHERE swmm_scenario_id = ").append(swmmRunId);

        final MetaObject[] swmmScenarioMetaObjects;

        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug("executing SQL statement: \n" + sb);
            }
            swmmScenarioMetaObjects = SessionManager.getProxy().getMetaObjectByQuery(sb.toString(), 0);
        } catch (final ConnectionException ex) {
            final String message = "cannot get SWMM Scenario  meta objects from database"; // NOI18N
            LOG.error(message, ex);
            throw new IOException(message, ex);
        }

        if ((swmmScenarioMetaObjects == null) || (swmmScenarioMetaObjects.length == 0)) {
            throw new IOException("no SWMM results found for SWMM Run #" + swmmRunId);
        }

        CidsBean etaResultBean = etaResultClass.getEmptyInstance().getBean();

        try {
            etaResultBean.setProperty("name", etaOutput.getEtaRunName());
            etaResultBean.setProperty("swmm_scenario_id", etaOutput.getSwmmRun());
            etaResultBean.setProperty("eta_scenario_id", etaOutput.getEtaRun());
            etaResultBean.setProperty("total_overflow_volume", etaOutput.getTotalOverflowVolume());
            etaResultBean.setProperty("r720", etaOutput.getR720());
            etaResultBean.setProperty("eta_hyd_required", etaOutput.getEtaHydRequired());
            etaResultBean.setProperty("eta_sed_required", etaOutput.getEtaSedRequired());
            etaResultBean.setProperty("eta_hyd_actual", etaOutput.getEtaHydActual());
            etaResultBean.setProperty("eta_sed_actual", etaOutput.getEtaSedActual());
            etaResultBean = etaResultBean.persist();
        } catch (Exception ex) {
            final String message = "could not update ETA Result for ETA Run '"
                        + etaOutput.getEtaRunName() + "' {" + etaOutput.getEtaRun() + "} of SWMM Run '"
                        + etaInput.getSwmmRunName() + "' {" + etaInput.getSwmmRun() + "}";
            LOG.error(message, ex);
            throw new IOException(message, ex);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("updating " + swmmScenarioMetaObjects.length + " SWMM Results with ETA Results");
        }
        for (final MetaObject swmmScenarioMetaObject : swmmScenarioMetaObjects) {
            try {
                final CidsBean swmmScenarioBean = swmmScenarioMetaObject.getBean();
                final Collection<CidsBean> etaResults = (Collection)swmmScenarioBean.getProperty("eta_results"); // NOI18N
                etaResults.add(etaResultBean);
                swmmScenarioBean.persist();
            } catch (Exception ex) {
                final String message = "could not update  SWMM Result '" + swmmScenarioMetaObject.getName() + "': "
                            + ex.getMessage();
                LOG.error(message, ex);
                throw new IOException(message, ex);
            }
        }
    }

    @Override
    protected String getReloadId() {
        try {
            final EtaInput etaInput = (EtaInput)getUR();
            final String reloadId = "local.linz." + etaInput.getSwmmProject()
                        + ".eta.scenario." + this.cidsBean.getProperty("id"); // NOI18N
            if (LOG.isDebugEnabled()) {
                LOG.debug("ETA Reload ID: " + reloadId);
            }
            return reloadId;
        } catch (final Exception e) {
            LOG.warn("cannot fetch reload id", e);                            // NOI18N
            return null;
        }
    }

    @Override
    public AbstractModelRunWatchable createWatchable() throws IOException {
        if (cidsBean == null) {
            throw new IllegalStateException("cidsBean not set"); // NOI18N
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("creating watchable for ETA Run '" + cidsBean + "' (" + cidsBean.getProperty("started")
                        + ") [" + this + "]");                   // NOI18N
        }

        final EtaRunInfo runInfo = this.getRunInfo();
        if (runInfo == null) {
            throw new IllegalStateException("run info not set"); // NOI18N
        }

        if (runInfo.isCanceled() || runInfo.isBroken()) {
            final String message = "run '" + cidsBean + "' is canceled  or broken, ignoring run";
            LOG.warn(message);
            throw new IllegalStateException(message); // NOI18N
        }

        return new EtaWatchable(this);
    }

    @Override
    protected boolean needsDownload() {
        return false;
    }

    @Override
    protected void prepareExecution() throws IOException {
        if (LOG.isDebugEnabled()) {
            LOG.debug(cidsBean + " executing ETA Calculation (" + cidsBean.getProperty("started")
                        + ") [" + this + "]"); // NOI18N
        }

        // now set to indeterminate
        fireProgressed(
            -1,
            -1,
            NbBundle.getMessage(
                EtaModelManager.class,
                "EtaModelManager.prepareExecution().progress.running"));

        final EtaInput etaInput = (EtaInput)this.getUR();

        if (etaInput.getSwmmRun() == -1) {
            final String message = "ETA run '" + this.cidsBean + "' without SWMM run";
            LOG.error(message);
            this.fireBroken(message);
            throw new IOException(message);
        }

        final EtaRunInfo etaRunInfo = new EtaRunInfo();
        etaRunInfo.setSwmmRunId(etaInput.getSwmmRun());

        LOG.info("preparing the execution of '" + this.cidsBean + "' for SWMM Run #" + etaRunInfo.getSwmmRunId()
                    + " (" + this + ")");
        if ((etaInput.getEtaConfigurations() == null) || etaInput.getEtaConfigurations().isEmpty()) {
            final String message = "ETA Run '" + this.cidsBean + "' without proper eta configurations!";
            LOG.error(message);
            this.fireBroken(message);
            throw new IOException(message);
        }

        try {
            final ObjectMapper mapper = new ObjectMapper();
            final StringWriter writer = new StringWriter();

            mapper.writeValue(writer, etaRunInfo);
            cidsBean.setProperty("runinfo", writer.toString());
            if (LOG.isDebugEnabled()) {
                LOG.debug("Saving ETA RunInfo for ETA Run '" + cidsBean + "' saved (" + cidsBean.getProperty("started")
                            + ")");
            }
            cidsBean = cidsBean.persist();
            if (LOG.isDebugEnabled()) {
                LOG.debug("ETA RunInfo for ETA Run '" + cidsBean + "' saved [" + this + "]");
            }
        } catch (final Exception ex) {
            final String message = "Cannot store runinfo of  ETA Run '" + cidsBean + "'"; // NOI18N
            LOG.error(message, ex);
            this.fireBroken(message);
            throw new IOException(message, ex);
        }
    }

    @Override
    public EtaRunInfo getRunInfo() {
        return SMSUtils.<EtaRunInfo>getRunInfo(cidsBean, EtaRunInfo.class);
    }
}
