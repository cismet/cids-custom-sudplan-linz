/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.linz.wizard;

import org.openide.WizardDescriptor;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

import java.awt.Component;

import javax.swing.event.ChangeListener;

import de.cismet.cids.custom.sudplan.linz.EtaInput;
import de.cismet.cids.custom.sudplan.linz.SwmmInput;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   pascal.dihe@cismet.de
 * @version  $Revision$, $Date$
 */
public final class WizardPanelMetadata implements WizardDescriptor.Panel {

    //~ Instance fields --------------------------------------------------------

    private final transient ChangeSupport changeSupport;

    private transient WizardDescriptor wizard;
    private transient WizardPanelMetadataUI component;

    private transient String name;
    private transient String description;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new RainfallDownscalingWizardPanelScenarios object.
     */
    public WizardPanelMetadata() {
        changeSupport = new ChangeSupport(this);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Component getComponent() {
        if (component == null) {
            component = new WizardPanelMetadataUI(this);
        }

        return component;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String getName() {
        return name;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String getDescription() {
        return description;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getDefaultDescription() {
        final StringBuilder sb = new StringBuilder();
        boolean isSwmmRun = false;
        if (wizard.getProperty(SwmmPlusEtaWizardAction.PROP_SWMM_PROJECT_BEAN) != null) {
            sb.append("SWMM Project: ");
            sb.append(((CidsBean)wizard.getProperty(SwmmPlusEtaWizardAction.PROP_SWMM_PROJECT_BEAN)).getProperty(
                    "title"));
            sb.append(", ");
        }

        if (wizard.getProperty(SwmmPlusEtaWizardAction.PROP_SWMM_INPUT) != null) {
            isSwmmRun = true;
            final SwmmInput swmmInput = (SwmmInput)wizard.getProperty(SwmmPlusEtaWizardAction.PROP_SWMM_INPUT);

            sb.append("SWMM INP File: ").append(swmmInput.getInpFile()).append(", ");
            sb.append("SWMM Start Date: ")
                    .append(SwmmInput.UTC_DATE_FORMAT.format(swmmInput.getStartDate()))
                    .append(" UTC, ");
            sb.append("SWMM End Date: ")
                    .append(SwmmInput.UTC_DATE_FORMAT.format(swmmInput.getEndDate()))
                    .append(" UTC, ");
            int i = 0;
            for (final String timeseries : swmmInput.getTimeseriesURLs()) {
                i++;
                sb.append("Time Series #").append(i).append(": ").append(timeseries).append(", ");
            }
        }

        if (((Boolean)this.wizard.getProperty(
                            SwmmPlusEtaWizardAction.PROP_ETA_CALCULATION_ENABLED))) {
            final EtaInput etaInput = (EtaInput)wizard.getProperty(SwmmPlusEtaWizardAction.PROP_ETA_INPUT);
            if (!isSwmmRun) {
                sb.append("SWMM Scenario: ").append(etaInput.getSwmmRunName()).append(", ");
            } else {
                sb.append("ETA Calcualtion: yes").append(", ");
            }
            sb.append("# ETA of Configurations: ").append(etaInput.getEtaConfigurations().size());
        } else if (isSwmmRun) {
            sb.append("ETA Calculation: no");
        }

        return sb.toString();
    }

    @Override
    public HelpCtx getHelp() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    public void readSettings(final Object settings) {
        wizard = (WizardDescriptor)settings;
        name = (String)wizard.getProperty(SwmmPlusEtaWizardAction.PROP_NAME);
        description = (String)wizard.getProperty(SwmmPlusEtaWizardAction.PROP_DESCRIPTION);
        component.init();
    }

    @Override
    public void storeSettings(final Object settings) {
        wizard = (WizardDescriptor)settings;
        wizard.putProperty(SwmmPlusEtaWizardAction.PROP_NAME, component.getSelectedName());
        wizard.putProperty(SwmmPlusEtaWizardAction.PROP_DESCRIPTION, component.getSelectedDescription());
    }

    @Override
    public boolean isValid() {
        final String currentName = component.getSelectedName();
        boolean valid;

        if ((currentName == null) || currentName.isEmpty()) {
            wizard.putProperty(
                WizardDescriptor.PROP_WARNING_MESSAGE,
                NbBundle.getMessage(
                    WizardPanelMetadata.class,
                    "WizardPanelMetadata.isValid().emptyName")); // NOI18N
            valid = false;
        } else {
            wizard.putProperty(WizardDescriptor.PROP_WARNING_MESSAGE, null);

            final String currentDesc = component.getSelectedDescription();
            if ((currentDesc == null) || currentDesc.isEmpty()) {
                wizard.putProperty(
                    WizardDescriptor.PROP_INFO_MESSAGE,
                    NbBundle.getMessage(
                        WizardPanelMetadata.class,
                        "WizardPanelMetadata.isValid().emptyDescription")); // NOI18N
            } else {
                wizard.putProperty(WizardDescriptor.PROP_INFO_MESSAGE, null);
            }

            valid = true;
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
     */
    protected void fireChangeEvent() {
        changeSupport.fireChange();
    }
}
