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

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;

import Sirius.server.localserver.attribute.ClassAttribute;
import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;

import org.apache.log4j.Logger;

import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

import de.cismet.cids.custom.sudplan.WizardInitialisationException;
import de.cismet.cids.custom.sudplan.linz.SwmmInput;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public final class SwmmWizardPanelProjectUI extends JPanel {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(SwmmWizardPanelProjectUI.class);

    //~ Instance fields --------------------------------------------------------

    private final transient SwmmWizardPanelProject model;
    private final transient ItemListener projectListener;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox chbEta;
    private javax.swing.JComboBox cobProjects;
    private javax.swing.JPanel configurationPanel;
    private javax.swing.JTextField fldInpFile;
    private com.toedter.calendar.JDateChooser jdcEndDate;
    private com.toedter.calendar.JDateChooser jdcStartDate;
    private javax.swing.JLabel lblDescription;
    private javax.swing.JLabel lblEndDate;
    private javax.swing.JLabel lblEta;
    private javax.swing.JLabel lblInpFile;
    private javax.swing.JLabel lblProject;
    private javax.swing.JLabel lblStartDate;
    private javax.swing.JPanel projectPanel;
    private javax.swing.JTextArea taDescriptionText;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new SwmmWizardPanelProjectUI object.
     *
     * @param   model  DOCUMENT ME!
     *
     * @throws  WizardInitialisationException  DOCUMENT ME!
     */
    public SwmmWizardPanelProjectUI(final SwmmWizardPanelProject model) throws WizardInitialisationException {
        this.model = model;
        this.projectListener = new ProjectListener();

        initComponents();
        // name of the wizard step
        this.setName(NbBundle.getMessage(
                SwmmWizardPanelProject.class,
                "SwmmWizardPanelProject.this.name")); // NOI18N

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
        this.cobProjects.setSelectedIndex(-1);
        this.cobProjects.setSelectedItem(model.getSwmmProject());

        // set default name of inp file (not required if setSelectedItem triggers an event)
// if ((model.getSwmmInput().getInpFile() == null) || model.getSwmmInput().getInpFile().isEmpty()) {
// final String inpFile = (String) model.getSwmmProject().getProperty("title") + ".inp";
// model.getSwmmInput().setInpFile(inpFile);
// }

        // this should perform all the updates
        this.bindingGroup.unbind();
        this.bindingGroup.bind();

        // this.fldStartDate.setText(model.getSwmmInput().getStartDate());
        // this.fldEndDate.setText(model.getSwmmInput().getEndDate());
        // this.lblDescriptionText.setText((String)model.getSwmmProject().getProperty("description"));
        // this.fldInpFile.setText(model.getSwmmInput().getInpFile());

    }

    /**
     * DOCUMENT ME!
     *
     * @throws  WizardInitialisationException  DOCUMENT ME!
     */
    private void initProjectList() throws WizardInitialisationException {
        final String domain = SessionManager.getSession().getUser().getDomain();
        final MetaClass mc = ClassCacheMultiple.getMetaClass(domain, SwmmPlusEtaWizardAction.TABLENAME_SWMM_PROJECT);

        if (mc == null) {
            throw new WizardInitialisationException("cannot fetch swmm project metaclass '"
                        + SwmmPlusEtaWizardAction.TABLENAME_SWMM_PROJECT + "' for domain '"
                        + domain + "'"); // NOI18N
        }

        final StringBuilder sb = new StringBuilder();

        sb.append("SELECT ").append(mc.getID()).append(',').append(mc.getPrimaryKey()); // NOI18N
        sb.append(" FROM ").append(mc.getTableName());                                  // NOI18N

        final ClassAttribute ca = mc.getClassAttribute("sortingColumn"); // NOI18N
        if (ca != null) {
            sb.append(" ORDER BY ").append(ca.getValue());               // NOI18N
        }

        final MetaObject[] metaObjects;
        try {
            metaObjects = SessionManager.getProxy().getMetaObjectByQuery(sb.toString(), 0);
        } catch (final ConnectionException ex) {
            final String message = "cannot get swmm project meta objects from database"; // NOI18N
            LOG.error(message, ex);
            throw new WizardInitialisationException(message, ex);
        }

        final DefaultComboBoxModel comboBoxModel = new DefaultComboBoxModel();
        for (int i = 0; i < metaObjects.length; ++i) {
            comboBoxModel.addElement(metaObjects[i].getBean());
        }

        this.cobProjects.setModel(comboBoxModel);
        this.cobProjects.setRenderer(new NameRenderer());
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        projectPanel = new javax.swing.JPanel();
        lblProject = new javax.swing.JLabel();
        cobProjects = new javax.swing.JComboBox();
        lblDescription = new javax.swing.JLabel();
        taDescriptionText = new javax.swing.JTextArea();
        configurationPanel = new javax.swing.JPanel();
        lblStartDate = new javax.swing.JLabel();
        lblEndDate = new javax.swing.JLabel();
        lblEta = new javax.swing.JLabel();
        lblInpFile = new javax.swing.JLabel();
        fldInpFile = new javax.swing.JTextField();
        chbEta = new javax.swing.JCheckBox();
        jdcStartDate = new com.toedter.calendar.JDateChooser();
        jdcEndDate = new com.toedter.calendar.JDateChooser();

        projectPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    SwmmWizardPanelProjectUI.class,
                    "SwmmWizardPanelProjectUI.projectPanel.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(
            lblProject,
            org.openide.util.NbBundle.getMessage(
                SwmmWizardPanelProjectUI.class,
                "SwmmWizardPanelProjectUI.lblProject.text")); // NOI18N

        cobProjects.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "LINZ @ Workshop 09.05.2011" }));

        org.openide.awt.Mnemonics.setLocalizedText(
            lblDescription,
            org.openide.util.NbBundle.getMessage(
                SwmmWizardPanelProjectUI.class,
                "SwmmWizardPanelProjectUI.lblDescription.text")); // NOI18N

        taDescriptionText.setColumns(20);
        taDescriptionText.setEditable(false);
        taDescriptionText.setLineWrap(true);
        taDescriptionText.setRows(2);
        taDescriptionText.setWrapStyleWord(true);
        taDescriptionText.setOpaque(false);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${model.swmmProject.description}"),
                taDescriptionText,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("");
        bindingGroup.addBinding(binding);

        final javax.swing.GroupLayout projectPanelLayout = new javax.swing.GroupLayout(projectPanel);
        projectPanel.setLayout(projectPanelLayout);
        projectPanelLayout.setHorizontalGroup(
            projectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                projectPanelLayout.createSequentialGroup().addContainerGap().addGroup(
                    projectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                        lblDescription).addComponent(lblProject)).addGap(34, 34, 34).addGroup(
                    projectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                        projectPanelLayout.createSequentialGroup().addComponent(
                            cobProjects,
                            javax.swing.GroupLayout.PREFERRED_SIZE,
                            185,
                            javax.swing.GroupLayout.PREFERRED_SIZE).addGap(0, 0, Short.MAX_VALUE)).addComponent(
                        taDescriptionText)).addContainerGap()));
        projectPanelLayout.setVerticalGroup(
            projectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                projectPanelLayout.createSequentialGroup().addContainerGap().addGroup(
                    projectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(
                        lblProject).addComponent(
                        cobProjects,
                        javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.PREFERRED_SIZE)).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addGroup(
                    projectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(
                        lblDescription).addComponent(
                        taDescriptionText,
                        javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.PREFERRED_SIZE)).addContainerGap(
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    Short.MAX_VALUE)));

        configurationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    SwmmWizardPanelProjectUI.class,
                    "SwmmWizardPanelProjectUI.configurationPanel.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(
            lblStartDate,
            org.openide.util.NbBundle.getMessage(
                SwmmWizardPanelProjectUI.class,
                "SwmmWizardPanelProjectUI.lblStartDate.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(
            lblEndDate,
            org.openide.util.NbBundle.getMessage(
                SwmmWizardPanelProjectUI.class,
                "SwmmWizardPanelProjectUI.lblEndDate.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(
            lblEta,
            org.openide.util.NbBundle.getMessage(
                SwmmWizardPanelProjectUI.class,
                "SwmmWizardPanelProjectUI.lblEta.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(
            lblInpFile,
            org.openide.util.NbBundle.getMessage(
                SwmmWizardPanelProjectUI.class,
                "SwmmWizardPanelProjectUI.lblInpFile.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${model.swmmInput.inpFile}"),
                fldInpFile,
                org.jdesktop.beansbinding.BeanProperty.create("text_ON_ACTION_OR_FOCUS_LOST"),
                "inpFile");
        bindingGroup.addBinding(binding);

        org.openide.awt.Mnemonics.setLocalizedText(
            chbEta,
            org.openide.util.NbBundle.getMessage(
                SwmmWizardPanelProjectUI.class,
                "SwmmWizardPanelProjectUI.chbEta.text"));        // NOI18N
        chbEta.setToolTipText(org.openide.util.NbBundle.getMessage(
                SwmmWizardPanelProjectUI.class,
                "SwmmWizardPanelProjectUI.chbEta.toolTipText")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${model.etaCalculationEnabled}"),
                chbEta,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(true);
        binding.setSourceUnreadableValue(true);
        bindingGroup.addBinding(binding);

        jdcStartDate.setDateFormatString(org.openide.util.NbBundle.getMessage(
                SwmmWizardPanelProjectUI.class,
                "SwmmWizardPanelProjectUI.jdcStartDate.dateFormatString")); // NOI18N
        jdcStartDate.setOpaque(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${model.swmmInput.startDate}"),
                jdcStartDate,
                org.jdesktop.beansbinding.BeanProperty.create("date"));
        bindingGroup.addBinding(binding);

        jdcEndDate.setDateFormatString(org.openide.util.NbBundle.getMessage(
                SwmmWizardPanelProjectUI.class,
                "SwmmWizardPanelProjectUI.jdcEndDate.dateFormatString")); // NOI18N
        jdcEndDate.setOpaque(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${model.swmmInput.endDate}"),
                jdcEndDate,
                org.jdesktop.beansbinding.BeanProperty.create("date"));
        bindingGroup.addBinding(binding);

        final javax.swing.GroupLayout configurationPanelLayout = new javax.swing.GroupLayout(configurationPanel);
        configurationPanel.setLayout(configurationPanelLayout);
        configurationPanelLayout.setHorizontalGroup(
            configurationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                configurationPanelLayout.createSequentialGroup().addContainerGap().addGroup(
                    configurationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(lblStartDate).addComponent(lblEndDate).addComponent(lblInpFile)
                                .addComponent(lblEta)).addGap(33, 33, 33).addGroup(
                    configurationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(chbEta).addComponent(fldInpFile).addComponent(
                        jdcStartDate,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        219,
                        Short.MAX_VALUE).addComponent(
                        jdcEndDate,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE)).addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
        configurationPanelLayout.setVerticalGroup(
            configurationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                configurationPanelLayout.createSequentialGroup().addContainerGap().addGroup(
                    configurationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(
                                    fldInpFile,
                                    javax.swing.GroupLayout.PREFERRED_SIZE,
                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                    javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(lblInpFile)).addGap(
                    14,
                    14,
                    14).addGroup(
                    configurationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                        configurationPanelLayout.createSequentialGroup().addComponent(lblStartDate).addGap(
                            18,
                            18,
                            18).addComponent(lblEndDate)).addGroup(
                        configurationPanelLayout.createSequentialGroup().addComponent(
                            jdcStartDate,
                            javax.swing.GroupLayout.PREFERRED_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE,
                            javax.swing.GroupLayout.PREFERRED_SIZE).addGap(11, 11, 11).addComponent(
                            jdcEndDate,
                            javax.swing.GroupLayout.PREFERRED_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE,
                            javax.swing.GroupLayout.PREFERRED_SIZE))).addGap(18, 18, 18).addGroup(
                    configurationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(lblEta, javax.swing.GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE)
                                .addComponent(chbEta)).addContainerGap()));

        final javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                layout.createSequentialGroup().addContainerGap().addGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                        projectPanel,
                        javax.swing.GroupLayout.Alignment.TRAILING,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE).addComponent(
                        configurationPanel,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE)).addContainerGap()));
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                layout.createSequentialGroup().addContainerGap().addComponent(
                    projectPanel,
                    javax.swing.GroupLayout.PREFERRED_SIZE,
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(
                    configurationPanel,
                    javax.swing.GroupLayout.PREFERRED_SIZE,
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    javax.swing.GroupLayout.PREFERRED_SIZE).addContainerGap(
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    Short.MAX_VALUE)));

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public SwmmWizardPanelProject getModel() {
        return model;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class ProjectListener implements ItemListener {

        //~ Instance fields ----------------------------------------------------

        private final transient Logger LOG = Logger.getLogger(ProjectListener.class);

        //~ Methods ------------------------------------------------------------

        @Override
        public void itemStateChanged(final ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("swmm project '" + e.getItem().toString() + "' selected, updating swmm model input");
                }

                final CidsBean swmmProject = (CidsBean)e.getItem();
                model.setSwmmProject(swmmProject);
                model.getSwmmInput().setSwmmProject((Integer)swmmProject.getProperty("id"));

                // um aufrufe nach getSwmmInput() zu minimieren (löst einen change event aus)
                final SwmmInput swmmInput = model.getSwmmInput();

                // set default inp file name
                // if ((swmmInput.getInpFile() == null) || swmmInput.getInpFile().isEmpty()) {
                if (swmmProject.getProperty("inp_file_name") != null) {
                    swmmInput.setInpFile(swmmProject.getProperty("inp_file_name").toString());
                } else {
                    LOG.warn("INP File not set in swmm model configuration, setting automatically to '"
                                + swmmProject.getProperty("title") + "'");
                    final String inpFile = (String)swmmProject.getProperty("title") + ".inp";
                    swmmInput.setInpFile(inpFile);
                }
                // }

                // fldInpFile.setText(inpFile);
                // lblDescriptionText.setText((String)swmmProject.getProperty("description"));

                // dieser mist funktioniert einfach nicht
                bindingGroup.unbind();
                bindingGroup.bind();
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public static final class NameRenderer extends DefaultListCellRenderer {

        //~ Methods ------------------------------------------------------------

        @Override
        public Component getListCellRendererComponent(final JList list,
                final Object value,
                final int index,
                final boolean isSelected,
                final boolean cellHasFocus) {
            final Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if ((comp instanceof JLabel) && (value instanceof CidsBean)) {
                final JLabel label = (JLabel)comp;
                final CidsBean obj = (CidsBean)value;
                final String name = (String)obj.getProperty("title"); // NOI18N
                label.setText(name);
            }

            return comp;
        }
    }
}
