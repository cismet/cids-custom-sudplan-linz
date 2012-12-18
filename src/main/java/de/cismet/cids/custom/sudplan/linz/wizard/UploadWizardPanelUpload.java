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

import javax.swing.event.ChangeListener;

/**
 * DOCUMENT ME!
 *
 * @author   pascal.dihe@cismet.de
 * @version  $Revision$, $Date$
 */
public final class UploadWizardPanelUpload implements WizardDescriptor.Panel {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(UploadWizardPanelUpload.class);

    //~ Instance fields --------------------------------------------------------

    private final transient ChangeSupport changeSupport;
    private transient WizardDescriptor wizardDescriptor;
    private transient UploadWizardPanelUploadUI component;
    private String inpFile = null;

    private boolean uploadComplete = false;

    // private boolean uploadCanceled = false;

    private boolean uploadErroneous = false;

    private boolean uploadInProgress = false;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new RainfallDownscalingWizardPanelScenarios object.
     */
    public UploadWizardPanelUpload() {
        changeSupport = new ChangeSupport(this);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Component getComponent() {
        if (component == null) {
            component = new UploadWizardPanelUploadUI(this);
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
        this.inpFile = wizardDescriptor.getProperty(UploadWizardAction.PROP_SWMM_INP_FILE).toString();
        this.uploadComplete = (Boolean)wizardDescriptor.getProperty(UploadWizardAction.PROP_UPLOAD_COMPLETE);
        this.uploadErroneous = (Boolean)wizardDescriptor.getProperty(UploadWizardAction.PROP_UPLOAD_ERRORNEOUS);
        this.uploadInProgress = (Boolean)wizardDescriptor.getProperty(UploadWizardAction.PROP_UPLOAD_IN_PROGRESS);

        this.fireChangeEvent();

        component.init();
    }

    @Override
    public void storeSettings(final Object settings) {
        wizardDescriptor.putProperty(UploadWizardAction.PROP_UPLOAD_COMPLETE, this.uploadComplete);
        wizardDescriptor.putProperty(UploadWizardAction.PROP_UPLOAD_ERRORNEOUS, this.uploadErroneous);
        wizardDescriptor.putProperty(UploadWizardAction.PROP_UPLOAD_IN_PROGRESS, this.uploadInProgress);
    }

    @Override
    public boolean isValid() {
        boolean valid = false;
        if (this.isUploadErroneous()) {
            wizardDescriptor.putProperty(
                WizardDescriptor.PROP_ERROR_MESSAGE,
                NbBundle.getMessage(
                    UploadWizardPanelUpload.class,
                    "UploadWizardPanelUpload.isValid().erroneous",
                    NbBundle.getMessage(
                        UploadWizardPanelUpload.class,
                        "UploadWizardPanelUpload.isValid().connectionError")));
//        } else if (this.isUploadCanceled()) {
//            wizard.putProperty(
//                WizardDescriptor.PROP_WARNING_MESSAGE,
//                NbBundle.getMessage(
//                    UploadWizardPanelProject.class,
//                    "UploadWizardPanelUpload.isValid().canceled"));
//            return false;
        } else if (this.isUploadComplete()) {
            wizardDescriptor.putProperty(
                WizardDescriptor.PROP_INFO_MESSAGE,
                NbBundle.getMessage(
                    UploadWizardPanelUpload.class,
                    "UploadWizardPanelUpload.isValid().complete"));
            valid = true;
        } else if (this.isUploadInProgress()) {
            wizardDescriptor.putProperty(
                WizardDescriptor.PROP_INFO_MESSAGE,
                NbBundle.getMessage(
                    UploadWizardPanelUpload.class,
                    "UploadWizardPanelUpload.isValid().progressing"));
        } else {
            wizardDescriptor.putProperty(
                WizardDescriptor.PROP_INFO_MESSAGE,
                NbBundle.getMessage(
                    UploadWizardPanelUpload.class,
                    "UploadWizardPanelUpload.isValid().upload"));
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
     * Get the value of uploadComplete.
     *
     * @return  the value of uploadComplete
     */
    public boolean isUploadComplete() {
        return uploadComplete;
    }

    /**
     * Set the value of uploadComplete.
     *
     * @param  uploadComplete  new value of uploadComplete
     */
    public void setUploadComplete(final boolean uploadComplete) {
        if (uploadComplete) {
            this.uploadInProgress = false;
            // this.uploadCanceled = false;
            this.uploadErroneous = false;
        }

        this.uploadComplete = uploadComplete;
        this.fireChangeEvent();
    }

//    /**
//     * Get the value of uploadCanceled.
//     *
//     * @return  the value of uploadCanceled
//     */
//    public boolean isUploadCanceled() {
//        return uploadCanceled;
//    }
//
//    /**
//     * Set the value of uploadCanceled.
//     *
//     * @param  uploadCanceled  new value of uploadCanceled
//     */
//    public void setUploadCanceled(final boolean uploadCanceled) {
//        if (uploadCanceled) {
//            this.uploadInProgress = false;
//            this.uploadComplete = false;
//            this.uploadErroneous = false;
//        }
//
//        this.uploadCanceled = uploadCanceled;
//        this.fireChangeEvent();
//    }

    /**
     * Get the value of uploadErroneous.
     *
     * @return  the value of uploadErroneous
     */
    public boolean isUploadErroneous() {
        return uploadErroneous;
    }

    /**
     * Set the value of uploadErroneous.
     *
     * @param  uploadErroneous  new value of uploadErroneous
     */
    public void setUploadErroneous(final boolean uploadErroneous) {
        if (uploadErroneous) {
            this.uploadInProgress = false;
            this.uploadComplete = false;
            // this.uploadCanceled = false;
        }

        this.uploadErroneous = uploadErroneous;
        this.fireChangeEvent();
    }

    /**
     * Get the value of uploadInProgress.
     *
     * @return  the value of uploadInProgress
     */
    public boolean isUploadInProgress() {
        return uploadInProgress;
    }

    /**
     * Set the value of uploadInProgress.
     *
     * @param  uploadInProgress  new value of uploadInProgress
     */
    public void setUploadInProgress(final boolean uploadInProgress) {
        this.uploadInProgress = uploadInProgress;
    }
}
