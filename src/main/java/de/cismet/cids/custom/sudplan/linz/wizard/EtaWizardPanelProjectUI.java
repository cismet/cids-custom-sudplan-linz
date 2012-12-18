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

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;

import de.cismet.cids.custom.sudplan.WizardInitialisationException;
import de.cismet.cids.custom.sudplan.linz.wizard.SwmmWizardPanelProjectUI.NameRenderer;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public final class EtaWizardPanelProjectUI extends JPanel {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(EtaWizardPanelProjectUI.class);

    //~ Instance fields --------------------------------------------------------

    private final transient EtaWizardPanelProject model;
    private final transient ItemListener projectListener;
    private final transient ItemListener scenarioListener;
    private transient CidsBean lastSelectedSwmmProject = null;
    private transient CidsBean lastSelectedSwmmScenario = null;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cobProjects;
    private javax.swing.JComboBox cobScenarios;
    private javax.swing.JPanel configurationPanel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblDescription;
    private javax.swing.JLabel lblProject;
    private javax.swing.JLabel lblScenarioDescription;
    private javax.swing.JLabel lblScenarios;
    private javax.swing.JPanel projectPanel;
    private javax.swing.JTextArea taProjectDescriptionText;
    private javax.swing.JTextArea taScenarioDescriptionText;
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
    public EtaWizardPanelProjectUI(final EtaWizardPanelProject model) throws WizardInitialisationException {
        this.model = model;
        this.projectListener = new ProjectListener();
        this.scenarioListener = new ScenarioListener();

        initComponents();
        // name of the wizard step
        this.setName(NbBundle.getMessage(
                EtaWizardPanelProject.class,
                "EtaWizardPanelProject.this.name")); // NOI18N

        this.initProjectList();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    void init() {
        if (model.getSelectedSwmmProject() != lastSelectedSwmmProject) {
            lastSelectedSwmmProject = model.getSelectedSwmmProject();
            this.cobProjects.setSelectedItem(lastSelectedSwmmProject);
        }

        if (model.getSelectedSwmmScenario() != lastSelectedSwmmScenario) {
            lastSelectedSwmmScenario = model.getSelectedSwmmScenario();
            this.cobScenarios.setSelectedItem(lastSelectedSwmmScenario);
        }

        // this.cobProjects.setSelectedIndex(-1);
        // this.cobScenarios.setSelectedIndex(-1);

        // this should perform all the updates
        // this.bindingGroup.unbind();
        // this.bindingGroup.bind();
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  WizardInitialisationException  DOCUMENT ME!
     */
    private void initProjectList() throws WizardInitialisationException {
        final String domain = SessionManager.getSession().getUser().getDomain();
        final MetaClass mc = ClassCacheMultiple.getMetaClass(domain, EtaWizardAction.TABLENAME_SWMM_PROJECT);

        if (mc == null) {
            throw new WizardInitialisationException("cannot fetch swmm project metaclass '"
                        + EtaWizardAction.TABLENAME_SWMM_PROJECT + "' for domain '"
                        + domain + "'"); // NOI18N
        }

        final StringBuilder sb = new StringBuilder();

        sb.append("SELECT ").append(mc.getID()).append(',').append(mc.getPrimaryKey()); // NOI18N
        sb.append(" FROM ").append(mc.getTableName());                                  // NOI18N

        final ClassAttribute ca = mc.getClassAttribute("sortingColumn"); // NOI18N
        if (ca != null) {
            sb.append(" ORDER BY ").append(ca.getValue());               // NOI18N
        }

        final MetaObject[] swmmProjects;
        try {
            swmmProjects = SessionManager.getProxy().getMetaObjectByQuery(sb.toString(), 0);
        } catch (final ConnectionException ex) {
            final String message = "cannot get swmm project meta objects from database"; // NOI18N
            LOG.error(message, ex);
            throw new WizardInitialisationException(message, ex);
        }

        final DefaultComboBoxModel comboBoxModel = new DefaultComboBoxModel();
        for (int i = 0; i < swmmProjects.length; ++i) {
            comboBoxModel.addElement(swmmProjects[i].getBean());
        }

        this.cobProjects.setModel(comboBoxModel);
        this.cobProjects.setRenderer(new NameRenderer());
        this.cobProjects.addItemListener(WeakListeners.create(
                ItemListener.class,
                this.projectListener,
                this.cobProjects));

        this.cobProjects.setSelectedIndex(-1);
        if (swmmProjects.length > 0) {
            this.cobProjects.setSelectedIndex(0);
        } else {
            LOG.warn("no SWMM projects found!?");
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  swmmScenarios  DOCUMENT ME!
     */
    private void initScenarioList(final List<CidsBean> swmmScenarios) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("loading " + swmmScenarios.size() + " SWMM Sceanrios");
        }
        final DefaultComboBoxModel comboBoxModel = new DefaultComboBoxModel();

        if (!swmmScenarios.isEmpty()) {
            for (final CidsBean swmmScenario : swmmScenarios) {
                comboBoxModel.addElement(swmmScenario);
            }

            this.cobScenarios.setModel(comboBoxModel);
            this.cobScenarios.addItemListener(WeakListeners.create(
                    ItemListener.class,
                    this.scenarioListener,
                    this.cobScenarios));

            // fire selection event
            this.cobScenarios.setSelectedIndex(-1);
            this.cobScenarios.setSelectedIndex(0);
        } else {
            LOG.warn("no SWMM Calculations available for SWMM Project "
                        + this.model.getSelectedSwmmProject());
            this.cobScenarios.setSelectedIndex(-1);
        }
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
        jScrollPane1 = new javax.swing.JScrollPane();
        taProjectDescriptionText = new javax.swing.JTextArea();
        configurationPanel = new javax.swing.JPanel();
        lblScenarioDescription = new javax.swing.JLabel();
        lblScenarios = new javax.swing.JLabel();
        cobScenarios = new javax.swing.JComboBox();
        jScrollPane2 = new javax.swing.JScrollPane();
        taScenarioDescriptionText = new javax.swing.JTextArea();

        projectPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    EtaWizardPanelProjectUI.class,
                    "EtaWizardPanelProjectUI.projectPanel.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(
            lblProject,
            org.openide.util.NbBundle.getMessage(
                EtaWizardPanelProjectUI.class,
                "EtaWizardPanelProjectUI.lblProject.text")); // NOI18N

        cobProjects.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "LINZ @ Workshop 09.05.2011" }));

        org.openide.awt.Mnemonics.setLocalizedText(
            lblDescription,
            org.openide.util.NbBundle.getMessage(
                EtaWizardPanelProjectUI.class,
                "EtaWizardPanelProjectUI.lblDescription.text")); // NOI18N

        taProjectDescriptionText.setColumns(20);
        taProjectDescriptionText.setEditable(false);
        taProjectDescriptionText.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        taProjectDescriptionText.setLineWrap(true);
        taProjectDescriptionText.setRows(3);
        taProjectDescriptionText.setWrapStyleWord(true);

        final org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${model.selectedSwmmProject.description}"),
                taProjectDescriptionText,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane1.setViewportView(taProjectDescriptionText);

        final javax.swing.GroupLayout projectPanelLayout = new javax.swing.GroupLayout(projectPanel);
        projectPanel.setLayout(projectPanelLayout);
        projectPanelLayout.setHorizontalGroup(
            projectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                projectPanelLayout.createSequentialGroup().addContainerGap().addGroup(
                    projectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                        lblDescription).addComponent(lblProject)).addGap(34, 34, 34).addGroup(
                    projectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                        jScrollPane1).addComponent(
                        cobProjects,
                        0,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE)).addContainerGap()));
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
                    projectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                        lblDescription).addComponent(
                        jScrollPane1,
                        javax.swing.GroupLayout.PREFERRED_SIZE,
                        77,
                        javax.swing.GroupLayout.PREFERRED_SIZE)).addContainerGap(
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    Short.MAX_VALUE)));

        configurationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    EtaWizardPanelProjectUI.class,
                    "EtaWizardPanelProjectUI.configurationPanel.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(
            lblScenarioDescription,
            org.openide.util.NbBundle.getMessage(
                EtaWizardPanelProjectUI.class,
                "EtaWizardPanelProjectUI.lblScenarioDescription.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(
            lblScenarios,
            org.openide.util.NbBundle.getMessage(
                EtaWizardPanelProjectUI.class,
                "EtaWizardPanelProjectUI.lblScenarios.text")); // NOI18N

        taScenarioDescriptionText.setEditable(false);
        taScenarioDescriptionText.setColumns(20);
        taScenarioDescriptionText.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        taScenarioDescriptionText.setLineWrap(true);
        taScenarioDescriptionText.setRows(3);
        taScenarioDescriptionText.setWrapStyleWord(true);
        jScrollPane2.setViewportView(taScenarioDescriptionText);

        final javax.swing.GroupLayout configurationPanelLayout = new javax.swing.GroupLayout(configurationPanel);
        configurationPanel.setLayout(configurationPanelLayout);
        configurationPanelLayout.setHorizontalGroup(
            configurationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                configurationPanelLayout.createSequentialGroup().addContainerGap().addGroup(
                    configurationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(lblScenarioDescription).addComponent(lblScenarios)).addGroup(
                    configurationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                        configurationPanelLayout.createSequentialGroup().addPreferredGap(
                            javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                            35,
                            Short.MAX_VALUE).addComponent(
                            jScrollPane2,
                            javax.swing.GroupLayout.PREFERRED_SIZE,
                            323,
                            javax.swing.GroupLayout.PREFERRED_SIZE)).addGroup(
                        configurationPanelLayout.createSequentialGroup().addGap(35, 35, 35).addComponent(
                            cobScenarios,
                            0,
                            javax.swing.GroupLayout.DEFAULT_SIZE,
                            Short.MAX_VALUE))).addContainerGap()));
        configurationPanelLayout.setVerticalGroup(
            configurationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                configurationPanelLayout.createSequentialGroup().addContainerGap().addGroup(
                    configurationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(lblScenarios).addComponent(
                        cobScenarios,
                        javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.PREFERRED_SIZE)).addGap(14, 14, 14).addGroup(
                    configurationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(lblScenarioDescription).addComponent(
                        jScrollPane2,
                        javax.swing.GroupLayout.PREFERRED_SIZE,
                        71,
                        javax.swing.GroupLayout.PREFERRED_SIZE)).addContainerGap(
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    Short.MAX_VALUE)));

        final javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                layout.createSequentialGroup().addContainerGap().addGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                        projectPanel,
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
                    javax.swing.GroupLayout.PREFERRED_SIZE).addGap(18, 18, 18).addComponent(
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
    public EtaWizardPanelProject getModel() {
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
                    LOG.debug("SWMM Project '" + e.getItem().toString() + "' selected, updating list of SWMM Results");
                }

                final CidsBean swmmProject = (CidsBean)e.getItem();
                model.setSelectedSwmmProject(swmmProject);

                taProjectDescriptionText.setText(
                    (swmmProject.getProperty("description") != null) ? swmmProject.getProperty("description")
                                .toString() : null);

                final List<CidsBean> swmmScenarios = (List)swmmProject.getProperty("swmm_scenarios"); // NOI18N
                initScenarioList(swmmScenarios);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class ScenarioListener implements ItemListener {

        //~ Instance fields ----------------------------------------------------

        private final transient Logger LOG = Logger.getLogger(ScenarioListener.class);

        //~ Methods ------------------------------------------------------------

        @Override
        public void itemStateChanged(final ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("SWMM Scenario '" + e.getItem().toString() + "' selected");
                }

                final CidsBean swmmScenario = (CidsBean)e.getItem();
                model.setSelectedSwmmScenario(swmmScenario);

                taScenarioDescriptionText.setText(
                    (swmmScenario.getProperty("description") != null)
                        ? swmmScenario.getProperty("description").toString() : null);

                // dieser mist funktioniert einfach nicht
                // bindingGroup.unbind();
                // bindingGroup.bind();
            }
        }
    }
}
