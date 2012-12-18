/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.linz.wizard;

import Sirius.server.middleware.types.MetaObject;

import org.apache.log4j.Logger;

import org.openide.WizardDescriptor;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

import java.awt.Component;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ChangeListener;

import de.cismet.cids.custom.sudplan.WizardInitialisationException;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   pascal.dihe@cismet.de
 * @version  $Revision$, $Date$
 */
public final class UploadWizardPanelCSOs implements WizardDescriptor.Panel {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(UploadWizardPanelCSOs.class);

    //~ Instance fields --------------------------------------------------------

    private final transient ChangeSupport changeSupport;
    private transient WizardDescriptor wizardDescriptor;
    private transient UploadWizardPanelCSOsUI component;
    private String inpFile = null;

    private boolean copyCSOsComplete = false;

    private boolean copyCSOsErroneous = false;

    private boolean copyCSOsInProgress = false;

    private transient int selectedSwmmProject = -1;

    private transient CidsBean newSwmmProjectBean;

    private transient List<MetaObject> copiedCSOs = new ArrayList<MetaObject>();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new RainfallDownscalingWizardPanelScenarios object.
     */
    public UploadWizardPanelCSOs() {
        changeSupport = new ChangeSupport(this);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Component getComponent() {
        synchronized (this) {
            if (component == null) {
                try {
                    component = new UploadWizardPanelCSOsUI(this);
                } catch (final WizardInitialisationException ex) {
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
        wizardDescriptor = (WizardDescriptor)settings;

        try {
            this.setSelectedSwmmProject(Integer.valueOf(
                    this.wizardDescriptor.getProperty(
                        UploadWizardAction.PROP_SELECTED_SWMM_PROJECT_ID).toString()));
        } catch (Exception e) {
            LOG.warn("could not set swmm project id, setting to default (-1)", e);
            this.setSelectedSwmmProject(-1);
        }

        this.newSwmmProjectBean = (CidsBean)wizardDescriptor.getProperty(UploadWizardAction.PROP_NEW_SWMM_PROJECT_BEAN);
        this.inpFile = wizardDescriptor.getProperty(UploadWizardAction.PROP_SWMM_INP_FILE).toString();
        this.copyCSOsComplete = (Boolean)wizardDescriptor.getProperty(UploadWizardAction.PROP_COPY_CSOS_COMPLETE);
        this.copyCSOsErroneous = (Boolean)wizardDescriptor.getProperty(UploadWizardAction.PROP_COPY_CSOS_ERRORNEOUS);
        this.copyCSOsInProgress = (Boolean)wizardDescriptor.getProperty(UploadWizardAction.PROP_COPY_CSOS_IN_PROGRESS);
        if (wizardDescriptor.getProperty(UploadWizardAction.PROP_COPIED_CSOS) != null) {
            this.copiedCSOs = (List<MetaObject>)wizardDescriptor.getProperty(UploadWizardAction.PROP_COPIED_CSOS);
        }

        component.init();

        this.fireChangeEvent();
    }

    @Override
    public void storeSettings(final Object settings) {
        wizardDescriptor.putProperty(UploadWizardAction.PROP_COPY_CSOS_COMPLETE, this.copyCSOsComplete);
        wizardDescriptor.putProperty(UploadWizardAction.PROP_COPY_CSOS_ERRORNEOUS, this.copyCSOsErroneous);
        wizardDescriptor.putProperty(UploadWizardAction.PROP_COPY_CSOS_IN_PROGRESS, this.copyCSOsInProgress);
        wizardDescriptor.putProperty(UploadWizardAction.PROP_NEW_SWMM_PROJECT_BEAN, this.newSwmmProjectBean);
        wizardDescriptor.putProperty(UploadWizardAction.PROP_COPIED_CSOS, this.getCopiedCSOs());
    }

    @Override
    public boolean isValid() {
        boolean valid = false;
        if (this.isCopyCSOsErroneous()) {
            wizardDescriptor.putProperty(
                WizardDescriptor.PROP_ERROR_MESSAGE,
                NbBundle.getMessage(
                    UploadWizardPanelCSOs.class,
                    "UploadWizardPanelCSOs.isValid().erroneous",
                    NbBundle.getMessage(
                        UploadWizardPanelCSOs.class,
                        "UploadWizardPanelCSOs.isValid().connectionError")));
        } else if (this.isCopyCSOsComplete()) {
            wizardDescriptor.putProperty(
                WizardDescriptor.PROP_INFO_MESSAGE,
                NbBundle.getMessage(
                    UploadWizardPanelCSOs.class,
                    "UploadWizardPanelCSOs.isValid().complete"));
            valid = true;
        } else if (this.isCopyCSOsInProgress()) {
            wizardDescriptor.putProperty(
                WizardDescriptor.PROP_INFO_MESSAGE,
                NbBundle.getMessage(
                    UploadWizardPanelCSOs.class,
                    "UploadWizardPanelCSOs.isValid().progressing"));
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

    /**
     * Get the value of inpFile.
     *
     * @return  the value of inpFile
     */
    public String getInpFile() {
        return inpFile;
    }

    /**
     * Get the value of copyCSOsComplete.
     *
     * @return  the value of copyCSOsComplete
     */
    public boolean isCopyCSOsComplete() {
        return copyCSOsComplete;
    }

    /**
     * Set the value of copyCSOsComplete.
     *
     * @param  copyCSOsComplete  new value of copyCSOsComplete
     */
    public void setCopyCSOsComplete(final boolean copyCSOsComplete) {
        if (copyCSOsComplete) {
            this.copyCSOsInProgress = false;
            this.copyCSOsErroneous = false;
        }

        this.copyCSOsComplete = copyCSOsComplete;
        this.fireChangeEvent();
    }

    /**
     * Get the value of copyCSOsErroneous.
     *
     * @return  the value of copyCSOsErroneous
     */
    public boolean isCopyCSOsErroneous() {
        return copyCSOsErroneous;
    }

    /**
     * Set the value of copyCSOsErroneous.
     *
     * @param  copyCSOsErroneous  new value of copyCSOsErroneous
     */
    public void setCopyCSOsErroneous(final boolean copyCSOsErroneous) {
        if (copyCSOsErroneous) {
            this.copyCSOsInProgress = false;
            this.copyCSOsComplete = false;
        }

        this.copyCSOsErroneous = copyCSOsErroneous;
        this.fireChangeEvent();
    }

    /**
     * Get the value of copyCSOsInProgress.
     *
     * @return  the value of copyCSOsInProgress
     */
    public boolean isCopyCSOsInProgress() {
        return copyCSOsInProgress;
    }

    /**
     * Set the value of copyCSOsInProgress.
     *
     * @param  copyCSOsInProgress  new value of copyCSOsInProgress
     */
    public void setCopyCSOsInProgress(final boolean copyCSOsInProgress) {
        this.copyCSOsInProgress = copyCSOsInProgress;
    }
    /**
     * Get the value of selectedSwmmProject.
     *
     * @return  the value of selectedSwmmProject
     */
    public int getSelectedSwmmProject() {
        return selectedSwmmProject;
    }

    /**
     * Set the value of selectedSwmmProject.
     *
     * @param  selectedSwmmProject  new value of selectedSwmmProject
     */
    public void setSelectedSwmmProject(final int selectedSwmmProject) {
        this.selectedSwmmProject = selectedSwmmProject;
    }

    /**
     * Get the value of newSwmmProjectBean.
     *
     * @return  the value of newSwmmProjectBean
     */
    public CidsBean getNewSwmmProjectBean() {
        return newSwmmProjectBean;
    }

    /**
     * Set the value of newSwmmProjectBean.
     *
     * @param  newSwmmProjectBean  new value of newSwmmProjectBean
     */
    public void setNewSwmmProjectBean(final CidsBean newSwmmProjectBean) {
        this.newSwmmProjectBean = newSwmmProjectBean;
    }

    /**
     * Get the value of copiedCSOs.
     *
     * @return  the value of copiedCSOs
     */
    public List<MetaObject> getCopiedCSOs() {
        return copiedCSOs;
    }

    /**
     * Set the value of copiedCSOs.
     *
     * @param  copiedCSOs  new value of copiedCSOs
     */
    public void setCopiedCSOs(final List<MetaObject> copiedCSOs) {
        this.copiedCSOs = copiedCSOs;
    }
}
