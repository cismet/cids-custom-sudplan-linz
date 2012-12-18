/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.linz.wizard;

import org.apache.log4j.Logger;

import org.openide.WizardDescriptor;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

import java.awt.Component;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;

import javax.swing.event.ChangeListener;

import de.cismet.cids.custom.sudplan.WizardInitialisationException;
import de.cismet.cids.custom.sudplan.linz.EtaInput;
import de.cismet.cids.custom.sudplan.linz.SwmmInput;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class SwmmWizardPanelProject implements WizardDescriptor.Panel {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(SwmmWizardPanelProject.class);
    public static final String PROP_ETACALCULATIONENABLED = "etaCalculationEnabled";

    //~ Instance fields --------------------------------------------------------

    private final transient SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    private final transient ChangeSupport changeSupport;
    private transient WizardDescriptor wizard;
    /** local swmm project variable. */
    private transient CidsBean swmmProject;
    /** local swmm input variable. */
    private transient SwmmInput swmmInput;
    private transient volatile SwmmWizardPanelProjectUI component;
    private boolean etaCalculationEnabled;
    private final transient PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new RunGeoCPMWizardPanelInput object.
     */
    public SwmmWizardPanelProject() {
        changeSupport = new ChangeSupport(this);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Component getComponent() {
        if (component == null) {
            synchronized (this) {
                if (component == null) {
                    try {
                        component = new SwmmWizardPanelProjectUI(this);
                    } catch (final WizardInitialisationException ex) {
                        LOG.error("cannot create wizard panel component", ex); // NOI18N
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
        if (LOG.isDebugEnabled()) {
            LOG.debug("read settings");
        }
        wizard = (WizardDescriptor)settings;
        assert wizard.getProperty(SwmmPlusEtaWizardAction.PROP_SWMM_PROJECT_BEAN) != null : "swmm project bean is null";
        this.swmmProject = (CidsBean)wizard.getProperty(SwmmPlusEtaWizardAction.PROP_SWMM_PROJECT_BEAN);

        assert wizard.getProperty(SwmmPlusEtaWizardAction.PROP_SWMM_INPUT) != null : "swmm input is null";
        this.swmmInput = (SwmmInput)wizard.getProperty(SwmmPlusEtaWizardAction.PROP_SWMM_INPUT);

        this.etaCalculationEnabled = (Boolean)wizard.getProperty(SwmmPlusEtaWizardAction.PROP_ETA_CALCULATION_ENABLED);

        component.init();
    }

    @Override
    public void storeSettings(final Object settings) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("store settings");
        }

        // remove time ..................
        try {
            final Date startDate = this.swmmInput.getStartDate();
            final Date endDate = this.swmmInput.getEndDate();
            swmmInput.setStartDate(SwmmInput.UTC_DATE_FORMAT.parse(dateFormat.format(startDate)));
            swmmInput.setEndDate(SwmmInput.UTC_DATE_FORMAT.parse(dateFormat.format(endDate)));
            if (LOG.isDebugEnabled()) {
                LOG.debug("start and end date " + startDate + "<=>" + endDate + " changed to"
                            + swmmInput.getStartDate() + "<=>" + swmmInput.getEndDate());
            }
        } catch (ParseException ex) {
            LOG.error("coud not sanitize start and end dates ("
                        + swmmInput.getStartDate() + ", "
                        + swmmInput.getEndDate() + "): " + ex.getLocalizedMessage(),
                ex);
        }

        wizard = (WizardDescriptor)settings;
        wizard.putProperty(SwmmPlusEtaWizardAction.PROP_SWMM_PROJECT_BEAN, this.getSwmmProject());
        wizard.putProperty(SwmmPlusEtaWizardAction.PROP_SWMM_INPUT, this.swmmInput);
        wizard.putProperty(SwmmPlusEtaWizardAction.PROP_ETA_CALCULATION_ENABLED, this.etaCalculationEnabled);

        if (this.etaCalculationEnabled) {
            ((EtaInput)wizard.getProperty(SwmmPlusEtaWizardAction.PROP_ETA_INPUT)).setSwmmProject(this.swmmInput
                        .getSwmmProject());
        }

        this.swmmInput.getSwmmProject();
    }

    @Override
    public boolean isValid() {
        boolean valid = true;

        if (this.swmmInput.getSwmmProject() == -1) {
            wizard.putProperty(
                WizardDescriptor.PROP_WARNING_MESSAGE,
                NbBundle.getMessage(SwmmWizardPanelProject.class, "SwmmWizardPanelProject.error.noproject"));
            valid = false;
        } else if ((this.swmmInput.getInpFile() == null) || this.swmmInput.getInpFile().isEmpty()) {
            Object inpFile = this.getSwmmProject().getProperty("inp_file_name");
            if (inpFile != null) {
                swmmInput.setInpFile(inpFile.toString());
                LOG.warn("SWMM INP file not set, setting to " + swmmInput.getInpFile());
            } else {
                inpFile = this.getSwmmProject().getProperty("title");
                LOG.warn("INP File not set in swmm model configuration, setting automatically to '"
                            + inpFile + "'");
            }

            // dieser beansbinding und property change mist funktioniert einfach nicht
            // warum sonst wird jetzt das textfield im UI nicht aktualisiert???!!!!
            this.swmmInput.setInpFile(inpFile.toString());

            wizard.putProperty(
                WizardDescriptor.PROP_INFO_MESSAGE,
                NbBundle.getMessage(SwmmWizardPanelProject.class, "SwmmWizardPanelProject.error.noinp", inpFile));
        } else if ((this.swmmInput.getStartDate() == null)) {
            wizard.putProperty(
                WizardDescriptor.PROP_WARNING_MESSAGE,
                NbBundle.getMessage(SwmmWizardPanelProject.class, "SwmmWizardPanelProject.error.noStartDate"));
            valid = false;
        } else if ((this.swmmInput.getEndDate() == null)) {
            wizard.putProperty(
                WizardDescriptor.PROP_WARNING_MESSAGE,
                NbBundle.getMessage(SwmmWizardPanelProject.class, "SwmmWizardPanelProject.error.noEndDate"));
            valid = false;
        } else if (this.swmmInput.getStartDate().getTime() >= this.swmmInput.getEndDate().getTime()) {
            wizard.putProperty(
                WizardDescriptor.PROP_WARNING_MESSAGE,
                NbBundle.getMessage(
                    SwmmWizardPanelProject.class,
                    "SwmmWizardPanelProject.error.endDateBeforeStartDate"));
            valid = false;
        } else if ((this.swmmInput.getEndDate().getTime()
                        - this.swmmInput.getStartDate().getTime()) < 200) {
            wizard.putProperty(
                WizardDescriptor.PROP_INFO_MESSAGE,
                NbBundle.getMessage(
                    SwmmWizardPanelProject.class,
                    "SwmmWizardPanelProject.error.simulationDateTooShort"));
            valid = false;
        } else {
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
    public SwmmInput getSwmmInput() {
        // nicht schön aber notwendig, damit die Validierung funktioniert
        this.changeSupport.fireChange();
        return swmmInput;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean getSwmmProject() {
        return swmmProject;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  swmmProject  DOCUMENT ME!
     */
    public void setSwmmProject(final CidsBean swmmProject) {
        this.swmmProject = swmmProject;
    }

    /**
     * Get the value of etaCalculationEnabled.
     *
     * @return  the value of etaCalculationEnabled
     */
    public boolean isEtaCalculationEnabled() {
        return etaCalculationEnabled;
    }

    /**
     * Set the value of etaCalculationEnabled.
     *
     * @param  etaCalculationEnabled  new value of etaCalculationEnabled
     */
    public void setEtaCalculationEnabled(final boolean etaCalculationEnabled) {
        final boolean oldEtaCalculationEnabled = this.etaCalculationEnabled;
        this.etaCalculationEnabled = etaCalculationEnabled;
        propertyChangeSupport.firePropertyChange(
            PROP_ETACALCULATIONENABLED,
            oldEtaCalculationEnabled,
            etaCalculationEnabled);
    }

    /**
     * Add PropertyChangeListener.
     *
     * @param  listener  DOCUMENT ME!
     */
    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove PropertyChangeListener.
     *
     * @param  listener  DOCUMENT ME!
     */
    public void removePropertyChangeListener(final PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public WizardDescriptor getWizard() {
        return wizard;
    }
}
