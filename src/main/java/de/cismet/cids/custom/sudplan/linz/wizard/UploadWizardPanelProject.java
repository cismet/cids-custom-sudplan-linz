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

import java.io.File;

import javax.swing.event.ChangeListener;

import de.cismet.cids.custom.sudplan.WizardInitialisationException;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   pascal.dihe@cismet.de
 * @version  $Revision$, $Date$
 */
public final class UploadWizardPanelProject implements WizardDescriptor.Panel {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(UploadWizardPanelProject.class);

    //~ Instance fields --------------------------------------------------------

    private final transient ChangeSupport changeSupport;
    private transient WizardDescriptor wizardDescriptor;
    private transient UploadWizardPanelProjectUI component;
    private transient boolean formEnabled = true;
    private transient CidsBean newSwmmProjectBean;
    private transient String inpFile = "";
    private transient int selectedSwmmProject = -1;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new RainfallDownscalingWizardPanelScenarios object.
     */
    public UploadWizardPanelProject() {
        changeSupport = new ChangeSupport(this);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Component getComponent() {
        synchronized (this) {
            if (component == null) {
                try {
                    component = new UploadWizardPanelProjectUI(this);
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
        this.newSwmmProjectBean = (CidsBean)wizardDescriptor.getProperty(
                UploadWizardAction.PROP_NEW_SWMM_PROJECT_BEAN);

        assert newSwmmProjectBean != null : "SWMM Project Bean must not be null!";

        try {
            selectedSwmmProject = Integer.valueOf(
                    this.wizardDescriptor.getProperty(
                        UploadWizardAction.PROP_SELECTED_SWMM_PROJECT_ID).toString());
        } catch (Exception e) {
            LOG.warn("could not set swmm project id, setting to default (-1)", e);
            selectedSwmmProject = -1;
        }

        this.setInpFile((wizardDescriptor.getProperty(UploadWizardAction.PROP_SWMM_INP_FILE) != null)
                ? wizardDescriptor.getProperty(UploadWizardAction.PROP_SWMM_INP_FILE).toString() : "");

        final boolean uploadComplete = (Boolean)wizardDescriptor.getProperty(UploadWizardAction.PROP_UPLOAD_COMPLETE);
        final boolean uploadErroneous = (Boolean)wizardDescriptor.getProperty(
                UploadWizardAction.PROP_UPLOAD_ERRORNEOUS);
        final boolean uploadInProgress = (Boolean)wizardDescriptor.getProperty(
                UploadWizardAction.PROP_UPLOAD_IN_PROGRESS);

        if (uploadInProgress) {
            LOG.warn("model run is still in progress");
            this.formEnabled = false;
        } else {
            this.formEnabled = !uploadComplete || uploadErroneous;
        }

        assert component != null : "UploadWizardPanelProjectUI must not be null!";
        component.init();
    }

    @Override
    public void storeSettings(final Object settings) {
        wizardDescriptor = (WizardDescriptor)settings;
        try {
            if ((this.inpFile != null) && !this.inpFile.isEmpty()
                        && (this.inpFile.lastIndexOf(File.separator) != -1)) {
                this.newSwmmProjectBean.setProperty(
                    "inp_file_name",
                    this.inpFile.substring(this.inpFile.lastIndexOf(File.separator) + 1));
            } else {
                LOG.warn("Input file path '" + this.inpFile
                            + "' is not set or does not contain separator '" + File.separator + "'");
            }

            wizardDescriptor.putProperty(UploadWizardAction.PROP_SWMM_INP_FILE,
                this.getInpFile());

            wizardDescriptor.putProperty(UploadWizardAction.PROP_SELECTED_SWMM_PROJECT_ID, this.selectedSwmmProject);
            wizardDescriptor.putProperty(UploadWizardAction.PROP_NEW_SWMM_PROJECT_BEAN, this.newSwmmProjectBean);
        } catch (Exception e) {
            LOG.error("could not set property of SWMM Input File", e);
        }
    }

    @Override
    public boolean isValid() {
        boolean valid = true;
        if (this.selectedSwmmProject == -1) {
            wizardDescriptor.putProperty(
                WizardDescriptor.PROP_WARNING_MESSAGE,
                NbBundle.getMessage(UploadWizardPanelProject.class, "UploadWizardPanelProject.isValid().noproject"));
            valid = false;
        } else if (this.getTitle().isEmpty()) {
            wizardDescriptor.putProperty(
                WizardDescriptor.PROP_WARNING_MESSAGE,
                NbBundle.getMessage(
                    UploadWizardPanelProject.class,
                    "UploadWizardPanelProject.isValid().emptyName"));        // NOI18N
            valid = false;
        } else if (this.getDescription().isEmpty()) {
            wizardDescriptor.putProperty(
                WizardDescriptor.PROP_WARNING_MESSAGE,
                NbBundle.getMessage(
                    UploadWizardPanelProject.class,
                    "UploadWizardPanelProject.isValid().emptyDescription")); // NOI18N
        } else if (this.getInpFile().isEmpty()) {
            wizardDescriptor.putProperty(
                WizardDescriptor.PROP_WARNING_MESSAGE,
                NbBundle.getMessage(
                    UploadWizardPanelProject.class,
                    "UploadWizardPanelProject.isValid().emptyFile"));        // NOI18N
        } else {
            wizardDescriptor.putProperty(WizardDescriptor.PROP_INFO_MESSAGE, null);
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
     * Get the value of title.
     *
     * @return  the value of title
     */
    public String getTitle() {
        return ((newSwmmProjectBean != null) && (newSwmmProjectBean.getProperty("title") != null))
            ? newSwmmProjectBean.getProperty("title").toString() : "";
    }

    /**
     * Set the value of title.
     *
     * @param  title  new value of title
     */
    public void setTitle(final String title) {
        try {
            newSwmmProjectBean.setProperty("title", title);
//            if (LOG.isDebugEnabled()) {
//                LOG.debug("title set to " + title);
//            }
        } catch (Exception e) {
            LOG.error("could not set title of new project to " + title, e);
        }
    }

    /**
     * Get the value of description.
     *
     * @return  the value of description
     */
    public String getDescription() {
        return ((newSwmmProjectBean != null) && (newSwmmProjectBean.getProperty("description") != null))
            ? newSwmmProjectBean.getProperty("description").toString() : "";
    }

    /**
     * Set the value of description.
     *
     * @param  description  new value of description
     */
    public void setDescription(final String description) {
        try {
            newSwmmProjectBean.setProperty("description", description);
        } catch (Exception e) {
            LOG.error("could not set description of new project to " + description, e);
        }
    }

    /**
     * Get the value of inpFile.
     *
     * @return  the value of inpFile
     */
    public String getInpFile() {
        return this.inpFile;
    }

    /**
     * Set the value of inpFile.
     *
     * @param  inpFile  new value of inpFile
     */
    public void setInpFile(final String inpFile) {
        this.inpFile = inpFile;
        if (LOG.isDebugEnabled()) {
            LOG.debug("inpFile set to '" + inpFile + "'");
        }
    }

    /**
     * Get the value of formEnabled.
     *
     * @return  the value of formEnabled
     */
    public boolean isFormEnabled() {
        return formEnabled;
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
        this.fireChangeEvent();
    }
}
