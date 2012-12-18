/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.linz.wizard;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;

import org.apache.log4j.Logger;

import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;

import java.awt.EventQueue;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import java.io.File;

import java.util.Collection;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import de.cismet.cids.custom.sudplan.WizardInitialisationException;
import de.cismet.cids.custom.sudplan.server.search.LightwightSwmmProjectsSearch;
import de.cismet.cids.custom.sudplan.server.search.LightwightSwmmProjectsSearch.LightwightSwmmProject;

/**
 * DOCUMENT ME!
 *
 * @author   mscholl
 * @version  $Revision$, $Date$
 */
public final class UploadWizardPanelProjectUI extends javax.swing.JPanel {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(EtaWizardPanelProjectUI.class);

    //~ Instance fields --------------------------------------------------------

    private final transient UploadWizardPanelProject model;
    private final transient DocumentListener docL;
    private final transient ItemListener projectListener;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private final transient javax.swing.JButton btnFile = new javax.swing.JButton();
    private final transient javax.swing.JComboBox cobProjects = new javax.swing.JComboBox();
    private final transient javax.swing.JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
    private final transient javax.swing.JLabel lblDescription = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblInpFile = new javax.swing.JLabel();
    private final transient javax.swing.JLabel lblName = new javax.swing.JLabel();
    private final transient javax.swing.JPanel newProjectPanel = new javax.swing.JPanel();
    private final transient javax.swing.JPanel oldProjectPanel = new javax.swing.JPanel();
    private final transient javax.swing.JTextArea txaDescription = new javax.swing.JTextArea();
    private final transient javax.swing.JTextField txtFile = new javax.swing.JTextField();
    private final transient javax.swing.JTextField txtName = new javax.swing.JTextField();
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form RainfallDownscalingVisualPanelTargetDate.
     *
     * @param   model  DOCUMENT ME!
     *
     * @throws  WizardInitialisationException  DOCUMENT ME!
     */
    public UploadWizardPanelProjectUI(final UploadWizardPanelProject model) throws WizardInitialisationException {
        this.model = model;
        this.docL = new DocumentListenerImpl();
        this.projectListener = new ProjectListener();

        // name of the wizard step
        this.setName(NbBundle.getMessage(
                UploadWizardPanelProjectUI.class,
                "WizardPanelMetadataUI.this.name")); // NOI18N

        initComponents();

        txtName.getDocument().addDocumentListener(WeakListeners.document(docL, txtName.getDocument()));
        txaDescription.getDocument().addDocumentListener(WeakListeners.document(docL, txaDescription.getDocument()));
        this.initProjectList();

        this.cobProjects.addItemListener(WeakListeners.create(
                ItemListener.class,
                this.projectListener,
                this.cobProjects));
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    void init() {
        // txtName.setText(model.getTitle());
        // this.txaDescription.setText(model.getDescription());
        this.txtFile.setText(model.getInpFile());

        txtName.setSelectionStart(0);
        txtName.setSelectionEnd(txtName.getText().length());

        this.btnFile.setEnabled(model.isFormEnabled());
        this.cobProjects.setEnabled(model.isFormEnabled());
        this.txtName.setEnabled(model.isFormEnabled());
        this.txaDescription.setEnabled(model.isFormEnabled());
        this.cobProjects.setSelectedIndex(-1);

        if ((this.model.getSelectedSwmmProject() != -1)
                    && (this.cobProjects.getItemCount() > 0)) {
            final DefaultComboBoxModel comboBoxModel = (DefaultComboBoxModel)this.cobProjects.getModel();
            for (int i = 0; i < comboBoxModel.getSize(); i++) {
                final LightwightSwmmProject swmmProject = (LightwightSwmmProject)comboBoxModel.getElementAt(i);
                if (this.model.getSelectedSwmmProject() == swmmProject.getId()) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("selecting SWMM Project '" + swmmProject + "' ("
                                    + this.model.getSelectedSwmmProject() + ")");
                    }
                    EventQueue.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                cobProjects.setSelectedItem(swmmProject);
                            }
                        });
                    break;
                }
            }
        }

        model.fireChangeEvent();

        bindingGroup.unbind();
        bindingGroup.bind();

        EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    txtName.requestFocus();
                }
            });
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        setLayout(new java.awt.GridBagLayout());

        oldProjectPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5),
                javax.swing.BorderFactory.createTitledBorder(
                    org.openide.util.NbBundle.getMessage(
                        UploadWizardPanelProjectUI.class,
                        "UploadWizardPanelProjectUI.oldProjectPanel.border.insideBorder.title")))); // NOI18N
        oldProjectPanel.setLayout(new java.awt.GridBagLayout());

        cobProjects.setModel(new javax.swing.DefaultComboBoxModel(
                new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        oldProjectPanel.add(cobProjects, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(oldProjectPanel, gridBagConstraints);

        newProjectPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5),
                javax.swing.BorderFactory.createTitledBorder(
                    org.openide.util.NbBundle.getMessage(
                        UploadWizardPanelProjectUI.class,
                        "UploadWizardPanelProjectUI.newProjectPanel.title")))); // NOI18N
        newProjectPanel.setLayout(new java.awt.GridBagLayout());

        lblName.setText(NbBundle.getMessage(
                UploadWizardPanelProjectUI.class,
                "UploadWizardPanelProjectUI.lblName.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        newProjectPanel.add(lblName, gridBagConstraints);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${model.title}"),
                txtName,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        newProjectPanel.add(txtName, gridBagConstraints);

        lblDescription.setText(NbBundle.getMessage(
                UploadWizardPanelProjectUI.class,
                "UploadWizardPanelProjectUI.lblDescription.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 6);
        newProjectPanel.add(lblDescription, gridBagConstraints);

        txaDescription.setColumns(20);
        txaDescription.setRows(3);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${model.description}"),
                txaDescription,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane1.setViewportView(txaDescription);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 6);
        newProjectPanel.add(jScrollPane1, gridBagConstraints);

        lblInpFile.setText(NbBundle.getMessage(
                UploadWizardPanelProjectUI.class,
                "UploadWizardPanelProjectUI.lblInpFile.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        newProjectPanel.add(lblInpFile, gridBagConstraints);

        txtFile.setEditable(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${model.inpFile}"),
                txtFile,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                "infFile");
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        newProjectPanel.add(txtFile, gridBagConstraints);

        btnFile.setText(org.openide.util.NbBundle.getMessage(
                UploadWizardPanelProjectUI.class,
                "UploadWizardPanelProjectUI.btnFile.text"));          // NOI18N
        btnFile.setActionCommand(org.openide.util.NbBundle.getMessage(
                UploadWizardPanelProjectUI.class,
                "UploadWizardPanelProjectUI.btnFile.actionCommand")); // NOI18N
        btnFile.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnFileActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 6);
        newProjectPanel.add(btnFile, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(newProjectPanel, gridBagConstraints);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnFileActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnFileActionPerformed
        final JFileChooser jfc = new JFileChooser();
        jfc.addChoosableFileFilter(new InpFilter());

        jfc.setSelectedFile(new File(UploadWizardPanelProjectUI.this.txtFile.getText()));
        final int answer = jfc.showOpenDialog(UploadWizardPanelProjectUI.this);
        if (JFileChooser.APPROVE_OPTION == answer) {
            txtFile.setText(jfc.getSelectedFile().getAbsolutePath());
            model.fireChangeEvent();
        }
    } //GEN-LAST:event_btnFileActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public UploadWizardPanelProject getModel() {
        return this.model;
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  WizardInitialisationException  DOCUMENT ME!
     */
    private void initProjectList() throws WizardInitialisationException {
        final LightwightSwmmProjectsSearch swmmProjectsSearch = new LightwightSwmmProjectsSearch(
                SessionManager.getSession().getUser().getDomain());

        final Collection<LightwightSwmmProject> swmmProjects;
        try {
            swmmProjects = SessionManager.getProxy().customServerSearch(swmmProjectsSearch);
        } catch (ConnectionException ex) {
            final String message = "could not get swmm projects from localserver '"
                        + SessionManager.getSession().getUser().getDomain() + "'";
            LOG.error(message, ex);
            throw new WizardInitialisationException(message, ex);
        }

        final DefaultComboBoxModel comboBoxModel = new DefaultComboBoxModel();

        if (swmmProjects != null) {
            for (final LightwightSwmmProject swmmProject : swmmProjects) {
                comboBoxModel.addElement(swmmProject);
            }
        }

        this.cobProjects.setModel(comboBoxModel);
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class DocumentListenerImpl implements DocumentListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void insertUpdate(final DocumentEvent e) {
            // model.setTitle(txtName.getText());
            // model.setDescription(txaDescription.getText());
            model.fireChangeEvent();
        }

        @Override
        public void removeUpdate(final DocumentEvent e) {
            // model.setTitle(txtName.getText());
            // model.setDescription(txaDescription.getText());
            model.fireChangeEvent();
        }

        @Override
        public void changedUpdate(final DocumentEvent e) {
            // model.setTitle(txtName.getText());
            // model.setDescription(txaDescription.getText());
            model.fireChangeEvent();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class ProjectListener implements ItemListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void itemStateChanged(final ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("SWMM Project '" + e.getItem() + "' selected");
                }

                final LightwightSwmmProject swmmProject = (LightwightSwmmProject)e.getItem();
                model.setSelectedSwmmProject(swmmProject.getId());
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class InpFilter extends javax.swing.filechooser.FileFilter {

        //~ Methods ------------------------------------------------------------

        @Override
        public boolean accept(final File file) {
            final String filename = file.getName();
            return file.isDirectory() || filename.endsWith(".inp");
        }
        @Override
        public String getDescription() {
            return "SWMM Input File";
        }
    }
}
