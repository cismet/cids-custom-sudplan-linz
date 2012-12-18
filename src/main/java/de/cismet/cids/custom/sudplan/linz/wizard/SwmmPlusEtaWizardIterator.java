/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.sudplan.linz.wizard;

import org.openide.WizardDescriptor;

import java.awt.Component;

import java.util.NoSuchElementException;

import javax.swing.JComponent;
import javax.swing.event.ChangeListener;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public final class SwmmPlusEtaWizardIterator implements WizardDescriptor.Iterator {

    //~ Instance fields --------------------------------------------------------

    private int index;
    private WizardDescriptor.Panel[] allPanels;
    private WizardDescriptor wizardDescriptor;
    private WizardDescriptor.Panel[] swmmPanels;
    private WizardDescriptor.Panel[] swmmPlusEtaPanels;
    private WizardDescriptor.Panel[] currentPanels;
    private String[] beginningContentData;
    private String[] healthyText;
    private String[] diseasedText;

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  wizardDescriptor  DOCUMENT ME!
     */
    public void initialize(final WizardDescriptor wizardDescriptor) {
        this.wizardDescriptor = wizardDescriptor;
    }

    /**
     * Initialize panels representing individual wizard's steps and sets various properties for them influencing wizard
     * appearance.
     */
    private void initializePanels() {
        if (allPanels == null) {
            allPanels = new WizardDescriptor.Panel[] {
                    new SwmmWizardPanelProject(),
                    new SwmmWizardPanelStations(),
                    new SwmmWizardPanelTimeseries(),
                    new EtaWizardPanelEtaConfiguration(),
                    new WizardPanelMetadata()
                };
            final String[] steps = new String[allPanels.length];
            for (int i = 0; i < allPanels.length; i++) {
                final Component c = allPanels[i].getComponent();
                // Default step name to component name of panel.
                steps[i] = c.getName();
                if (c instanceof JComponent) { // assume Swing components
                    final JComponent jc = (JComponent)c;
                    // Sets step number of a component
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, Integer.valueOf(i));
                    // Sets steps names for a panel
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, steps);
                    // Turn on subtitle creation on each step
                    jc.putClientProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, Boolean.TRUE);
                    // Show steps on the left side with the image on the
                    // background
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, Boolean.TRUE);
                    // Turn on numbering of all steps
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, Boolean.TRUE);
                }
            }

            swmmPanels = new WizardDescriptor.Panel[] { allPanels[0], allPanels[1], allPanels[2], allPanels[4] };
            swmmPlusEtaPanels = new WizardDescriptor.Panel[] {
                    allPanels[0],
                    allPanels[1],
                    allPanels[2],
                    allPanels[3],
                    allPanels[4]
                };
            healthyText = new String[] { steps[0], steps[1], steps[2], steps[4] };
            diseasedText = new String[] { steps[0], steps[1], steps[2], steps[3], steps[4] };
            currentPanels = swmmPanels;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  etaCalculationEnabled  DOCUMENT ME!
     */
    private void setEtaCalculationEnabled(final boolean etaCalculationEnabled) {
        String[] contentData;
        if (!etaCalculationEnabled) {
            currentPanels = swmmPanels;
            contentData = healthyText;
        } else {
            currentPanels = swmmPlusEtaPanels;
            contentData = diseasedText;
        }

        wizardDescriptor.putProperty(WizardDescriptor.PROP_CONTENT_DATA, contentData);
    }

    @Override
    public WizardDescriptor.Panel current() {
        // Make sure panels have been initialized
        initializePanels();
        return currentPanels[index];
    }

    @Override
    public String name() {
        if (index == 0) {
            return index + 1 + " of ...";
        }
        return index + 1 + " of " + currentPanels.length;
    }

    @Override
    public boolean hasNext() {
        initializePanels();
        return index < (currentPanels.length - 1);
    }

    @Override
    public boolean hasPrevious() {
        return index > 0;
    }

    @Override
    public void nextPanel() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        if (index == 0) {
            if (((Boolean)this.wizardDescriptor.getProperty(SwmmPlusEtaWizardAction.PROP_ETA_CALCULATION_ENABLED))) {
                setEtaCalculationEnabled(true);
            } else {
                setEtaCalculationEnabled(false);
            }
        }
        index++;
        // The index of the step (or "Content Data") to be highlighted needs to be set separately.
        wizardDescriptor.putProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, index);
    }

    @Override
    public void previousPanel() {
        if (!hasPrevious()) {
            throw new NoSuchElementException();
        }
        index--;
        if (index == 0) {
            wizardDescriptor.putProperty(WizardDescriptor.PROP_CONTENT_DATA, beginningContentData);
        }
        // The index of the step (or "Content Data") to be highlighted needs to be set separately.
        wizardDescriptor.putProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, index);
    }

    // If nothing unusual changes in the middle of the wizard, simply:
    @Override
    public void addChangeListener(final ChangeListener l) {
    }

    @Override
    public void removeChangeListener(final ChangeListener l) {
    }
    // If something changes dynamically (besides moving between panels), e.g.
    // the number of panels changes in response to user input, then uncomment
    // the following and call when needed: fireChangeEvent();
    /*
     * private Set<ChangeListener> listeners = new HashSet<ChangeListener>(1); // or can use ChangeSupport in NB 6.0
     * public final void addChangeListener(ChangeListener l) { synchronized (listeners) { listeners.add(l); } } public
     * final void removeChangeListener(ChangeListener l) { synchronized (listeners) { listeners.remove(l); } } protected
     * final void fireChangeEvent() { Iterator<ChangeListener> it; synchronized (listeners) { it = new
     * HashSet<ChangeListener>(listeners).iterator(); } ChangeEvent ev = new ChangeEvent(this); while (it.hasNext()) {
     * it.next().stateChanged(ev); } }
     */
}
