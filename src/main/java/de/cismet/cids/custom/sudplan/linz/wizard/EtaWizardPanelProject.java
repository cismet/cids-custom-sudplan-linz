/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.linz.wizard;

import org.apache.log4j.Logger;

import org.codehaus.jackson.map.ObjectMapper;

import org.openide.WizardDescriptor;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

import java.awt.Component;

import javax.swing.event.ChangeListener;

import de.cismet.cids.custom.sudplan.linz.EtaInput;
import de.cismet.cids.custom.sudplan.linz.SwmmOutput;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   pascal.dihe@cismet.de
 * @version  $Revision$, $Date$
 */
public final class EtaWizardPanelProject implements WizardDescriptor.Panel {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(EtaWizardPanelProject.class);

    //~ Instance fields --------------------------------------------------------

    private final transient ChangeSupport changeSupport;
    private transient WizardDescriptor wizard;
    /** local swmm project variable. */
    private transient CidsBean selectedSwmmProject;
    private transient CidsBean selectedSwmmScenario;
    private transient volatile EtaWizardPanelProjectUI component;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new RunGeoCPMWizardPanelInput object.
     */
    public EtaWizardPanelProject() {
        changeSupport = new ChangeSupport(this);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Component getComponent() {
        synchronized (this) {
            if (component == null) {
                try {
                    component = new EtaWizardPanelProjectUI(this);
                } catch (final Exception ex) {
                    LOG.error("cannot create wizard panel component", ex); // NOI18N
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
//        if (LOG.isDebugEnabled()) {
//            LOG.debug("read settings");
//        }
        wizard = (WizardDescriptor)settings;
        assert wizard.getProperty(EtaWizardAction.PROP_SWMM_PROJECT_BEAN) != null : "swmm project bean is null";
        this.selectedSwmmProject = (CidsBean)wizard.getProperty(EtaWizardAction.PROP_SWMM_PROJECT_BEAN);
        if (wizard.getProperty(EtaWizardAction.PROP_SWMM_SCENARIO_BEAN) != null) {
            this.selectedSwmmScenario = (CidsBean)wizard.getProperty(EtaWizardAction.PROP_SWMM_SCENARIO_BEAN);
        }

        assert component != null : "GUI Component is NULL!";
        component.init();
    }

    @Override
    public void storeSettings(final Object settings) {
//        if (LOG.isDebugEnabled()) {
//            LOG.debug("store settings");
//        }
        wizard = (WizardDescriptor)settings;
        wizard.putProperty(EtaWizardAction.PROP_SWMM_PROJECT_BEAN, this.getSelectedSwmmProject());
        wizard.putProperty(EtaWizardAction.PROP_SWMM_SCENARIO_BEAN, this.getSelectedSwmmScenario());

        if ((wizard.getProperty(EtaWizardAction.PROP_ETA_INPUT) != null)
                    && (this.getSelectedSwmmScenario() == wizard.getProperty(
                            EtaWizardAction.PROP_SWMM_SCENARIO_BEAN))) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("selected SWMM Scenario '" + this.getSelectedSwmmScenario()
                            + "' didn't change, don't create new eta input");
            }
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("selected SWMM Scenario '" + this.getSelectedSwmmScenario()
                            + "' did change, create new eta input");
            }
            try {
                final CidsBean swmmOutputBean = (CidsBean)this.getSelectedSwmmScenario().getProperty("modeloutput");
                final String json = (String)swmmOutputBean.getProperty("ur"); // NOI18N
                final ObjectMapper mapper = new ObjectMapper();
                final SwmmOutput swmmOutput = mapper.readValue(json, SwmmOutput.class);
                final EtaInput etaInput = new EtaInput(swmmOutput);

                if (swmmOutput.getSwmmProject() == -1) {
                    final int swmmProjectId = (Integer)this.getSelectedSwmmProject().getProperty("id");
                    LOG.warn("no suitable SWMM project selected in SWMM Output: -1, setting to " + swmmProjectId);
                    etaInput.setSwmmProject(swmmProjectId);
                } else {
                    etaInput.setSwmmProject(swmmOutput.getSwmmProject());
                }

                wizard.putProperty(EtaWizardAction.PROP_ETA_INPUT, etaInput);
            } catch (Exception e) {
                LOG.error("invalid SWMM Model Output, could not create valid ETA Input: " + e.getMessage(), e);
            }
        }
    }

    @Override
    public boolean isValid() {
        boolean valid = true;

        if (this.getSelectedSwmmProject() == null) {
            wizard.putProperty(
                WizardDescriptor.PROP_WARNING_MESSAGE,
                NbBundle.getMessage(EtaWizardPanelProject.class, "EtaWizardPanelProject.error.noproject"));
            valid = false;
            LOG.warn("no SWMM project selected");
        } else if (this.getSelectedSwmmScenario() == null) {
            wizard.putProperty(
                WizardDescriptor.PROP_WARNING_MESSAGE,
                NbBundle.getMessage(EtaWizardPanelProject.class, "EtaWizardPanelProject.error.noscenario"));
            valid = false;
            LOG.warn("no SWMM Scenario selected");
        } else if (this.getSelectedSwmmScenario().getProperty("modeloutput") == null) {
            wizard.putProperty(
                WizardDescriptor.PROP_WARNING_MESSAGE,
                NbBundle.getMessage(EtaWizardPanelProject.class, "EtaWizardPanelProject.error.noresults"));
            valid = false;
            LOG.warn("SWMM Scenario has no results");
        } else {
            try {
                final CidsBean swmmOutputBean = (CidsBean)this.getSelectedSwmmScenario().getProperty("modeloutput");
                final String json = (String)swmmOutputBean.getProperty("ur"); // NOI18N
                final ObjectMapper mapper = new ObjectMapper();
                mapper.readValue(json, SwmmOutput.class);
            } catch (Exception e) {
                LOG.error("invalid SWMM Model Output: " + e.getMessage(), e);
                wizard.putProperty(
                    WizardDescriptor.PROP_ERROR_MESSAGE,
                    NbBundle.getMessage(EtaWizardPanelProject.class, "EtaWizardPanelProject.error.invalidResults"));
                valid = false;
            }
        }
//        if (LOG.isDebugEnabled()) {
//            LOG.debug("isValid: " + valid);
//        }

        if (valid) {
            wizard.putProperty(WizardDescriptor.PROP_INFO_MESSAGE, null);
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
    public CidsBean getSelectedSwmmProject() {
        return selectedSwmmProject;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  swmmProject  DOCUMENT ME!
     */
    public void setSelectedSwmmProject(final CidsBean swmmProject) {
        this.selectedSwmmProject = swmmProject;
        this.changeSupport.fireChange();
    }

    /**
     * Get the value of selectedSwmmScenario.
     *
     * @return  the value of selectedSwmmScenario
     */
    public CidsBean getSelectedSwmmScenario() {
        return selectedSwmmScenario;
    }

    /**
     * Set the value of selectedSwmmScenario.
     *
     * @param  selectedSwmmScenario  new value of selectedSwmmScenario
     */
    public void setSelectedSwmmScenario(final CidsBean selectedSwmmScenario) {
        this.selectedSwmmScenario = selectedSwmmScenario;
        this.changeSupport.fireChange();
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
