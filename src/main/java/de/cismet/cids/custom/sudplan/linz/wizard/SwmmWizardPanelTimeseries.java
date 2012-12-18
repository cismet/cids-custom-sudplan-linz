/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.linz.wizard;

import at.ac.ait.enviro.tsapi.timeseries.TimeInterval;
import at.ac.ait.enviro.tsapi.timeseries.TimeStamp;

import org.apache.log4j.Logger;

import org.openide.WizardDescriptor;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

import java.awt.Component;

import java.net.MalformedURLException;

import java.util.List;

import javax.swing.event.ChangeListener;

import de.cismet.cids.custom.sudplan.TimeseriesRetrieverConfig;
import de.cismet.cids.custom.sudplan.WizardInitialisationException;
import de.cismet.cids.custom.sudplan.linz.SwmmInput;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class SwmmWizardPanelTimeseries implements WizardDescriptor.Panel {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(SwmmWizardPanelTimeseries.class);

    //~ Instance fields --------------------------------------------------------

    protected SwmmInput swmmInput;
    private final transient ChangeSupport changeSupport;
    private transient WizardDescriptor wizard;
    /** local swmm project variable. */
    private transient List<Integer> stationIds;
    private transient volatile SwmmWizardPanelTimeseriesUI component;
    private transient boolean validTimeIntervall = false;
    private transient TimeStamp startDate;
    private transient TimeStamp endDate;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new RunGeoCPMWizardPanelInput object.
     */
    public SwmmWizardPanelTimeseries() {
        changeSupport = new ChangeSupport(this);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Component getComponent() {
        if (component == null) {
            synchronized (this) {
                if (component == null) {
                    try {
                        component = new SwmmWizardPanelTimeseriesUI(this);
                    } catch (final WizardInitialisationException ex) {
                        LOG.error("cannot create Timeseries wizard panel component", ex); // NOI18N
                    }
                }
            }
        }

        return component;
    }

    @Override
    public HelpCtx getHelp() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    public void readSettings(final Object settings) {
        wizard = (WizardDescriptor)settings;
        assert wizard.getProperty(SwmmPlusEtaWizardAction.PROP_STATION_IDS) != null : "station ids list is null";
        this.stationIds = (List<Integer>)wizard.getProperty(
                SwmmPlusEtaWizardAction.PROP_STATION_IDS);

        assert wizard.getProperty(SwmmPlusEtaWizardAction.PROP_SWMM_INPUT) != null : "swmm input is null";
        this.swmmInput = (SwmmInput)wizard.getProperty(SwmmPlusEtaWizardAction.PROP_SWMM_INPUT);

        try {
            this.startDate = this.swmmInput.getStartDateTimestamp();
            this.endDate = this.swmmInput.getEndDateTimestamp();
        } catch (Exception e) {
            LOG.error("could not set start and end date timestamps: " + e.getLocalizedMessage(), e);
            this.startDate = null;
            this.endDate = null;
        }

        try {
            if ((this.swmmInput.getTimeseriesURLs() != null)
                        && !this.swmmInput.getTimeseriesURLs().isEmpty()) {
                this.validTimeIntervall = true;
                if (LOG.isDebugEnabled()) {
                    LOG.debug("checking selected time series dates for validity");
                }
                for (final String timeseries : this.swmmInput.getTimeseriesURLs()) {
                    final TimeseriesRetrieverConfig config = TimeseriesRetrieverConfig.fromUrl(timeseries);
                    final TimeInterval timeInterval = config.getInterval();

                    if (timeInterval != null) {
                        if (!timeInterval.containsTimeStamp(startDate)
                                    || !timeInterval.containsTimeStamp(endDate)) {
                            LOG.warn("time intervall '" + timeInterval
                                        + "' of timeseries \n<" + timeseries
                                        + ">\n does not cover selected model timespan '" + startDate + "'<-> '"
                                        + endDate + "'");
                            if (LOG.isDebugEnabled()) {
                                LOG.debug(timeInterval.getStart() + " compared to " + startDate + " = "
                                            + timeInterval.getStart().compareTo(startDate));
                            }
                            if (LOG.isDebugEnabled()) {
                                LOG.debug(timeInterval.getEnd() + " compared to "
                                            + endDate + " = " + timeInterval.getEnd().compareTo(endDate));
                            }
                            this.validTimeIntervall = false;
                            break;
                        }
                    } else {
                        LOG.warn("cannot check interval: timeInterval of timeseries '" + timeseries + "' is null!");
                    }
                }
            } else {
                this.validTimeIntervall = false;
            }
        } catch (MalformedURLException ex) {
            LOG.error("could not check time intervall of timseries", ex);
            this.validTimeIntervall = false;
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("read settings: startDate=" + this.startDate + ", endDate=" + this.endDate);
        }

        component.init();
    }

    @Override
    public void storeSettings(final Object settings) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("store settings");
        }
        wizard = (WizardDescriptor)settings;
        // wizard.putProperty(SwmmPlusEtaWizardAction.PROP_STATION_IDS, this.stationIds);
        wizard.putProperty(SwmmPlusEtaWizardAction.PROP_SWMM_INPUT, this.swmmInput);
    }

    @Override
    public boolean isValid() {
        boolean valid = true;
        if (LOG.isDebugEnabled()) {
            LOG.debug("isValid");
        }
        if (this.swmmInput.getTimeseries().isEmpty()) {
            // FIXME: i18n
            wizard.putProperty(
                WizardDescriptor.PROP_WARNING_MESSAGE,
                NbBundle.getMessage(SwmmWizardPanelTimeseries.class,
                    "SwmmWizardPanelTimeseries.error.notimeseries"));
            valid = false;
        } else if (!validTimeIntervall) {
            wizard.putProperty(
                WizardDescriptor.PROP_WARNING_MESSAGE,
                NbBundle.getMessage(
                    SwmmWizardPanelTimeseries.class,
                    "SwmmWizardPanelTimeseries.error.wrongtimecoverage"));
            valid = false;
        } else {
            // TODO: check time intervall!

            wizard.putProperty(
                WizardDescriptor.PROP_INFO_MESSAGE,
                null);
        }

        return valid;
    }

    @Override
    public void addChangeListener(final ChangeListener l) {
        changeSupport.addChangeListener(l);
    }

    @Override
    public void removeChangeListener(final ChangeListener l) {
        changeSupport.removeChangeListener(l);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public WizardDescriptor getWizard() {
        return wizard;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public List<Integer> getStationIds() {
        return stationIds;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public List<Integer> getTimeseriesIds() {
        return this.swmmInput.getTimeseries();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isForecast() {
        return this.swmmInput.isForecast();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  timeseriesIds   DOCUMENT ME!
     * @param  timeseriesURLs  DOCUMENT ME!
     */
    public void setTimeseries(final List<Integer> timeseriesIds, final List<String> timeseriesURLs) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("setting " + timeseriesIds.size() + " new timiseries ids and URIs");
        }
        this.swmmInput.setTimeseries(timeseriesIds);

        try {
            // reset flag
            this.validTimeIntervall = true;
            int i = 0;
            final TimeInterval modelIntervall = new TimeInterval(
                    TimeInterval.Openness.CLOSED,
                    this.startDate,
                    this.endDate,
                    TimeInterval.Openness.CLOSED);
            for (final String timeseries : timeseriesURLs) {
                final TimeseriesRetrieverConfig config = TimeseriesRetrieverConfig.fromUrl(timeseries);
                final TimeInterval timeInterval = config.getInterval();

                if (!timeInterval.containsTimeStamp(startDate)
                            || !timeInterval.containsTimeStamp(endDate)) {
                    LOG.warn("time intervall " + timeInterval + " of timeseries " + timeseriesIds.get(i)
                                + " does not cover selected model timespan " + startDate + "<->" + endDate);
                    this.validTimeIntervall = false;
                    break;
                } else {
                    config.setInterval(modelIntervall);
                    final String newURI = config.toUrl();
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("updated timeseries URI: " + newURI);
                    }
                    timeseriesURLs.set(i, newURI);
                }

                i++;
            }
        } catch (Exception e) {
            LOG.error("could not check time intervall of timseries: " + e.getLocalizedMessage(), e);
            this.validTimeIntervall = false;
        }

        this.swmmInput.setTimeseriesURLs(timeseriesURLs);
        this.changeSupport.fireChange();
    }
}
