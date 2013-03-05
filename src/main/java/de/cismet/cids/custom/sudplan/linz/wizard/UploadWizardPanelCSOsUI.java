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

import Sirius.server.middleware.types.MetaObject;

import org.apache.log4j.Logger;

import org.openide.awt.Mnemonics;
import org.openide.util.NbBundle;

import java.awt.CardLayout;
import java.awt.EventQueue;

import java.util.List;

import javax.swing.JPanel;
import javax.swing.table.AbstractTableModel;

import de.cismet.cids.custom.sudplan.WizardInitialisationException;
import de.cismet.cids.custom.sudplan.commons.SudplanConcurrency;
import de.cismet.cids.custom.sudplan.linz.server.actions.CopyCSOsAction;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.server.actions.ServerActionParameter;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public final class UploadWizardPanelCSOsUI extends JPanel {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(UploadWizardPanelCSOsUI.class);

    //~ Instance fields --------------------------------------------------------

    private final transient UploadWizardPanelCSOs model;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel cardPanel;
    private javax.swing.JPanel csoConfigurationPanel;
    private javax.swing.JScrollPane jScrollPaneCsoConfiguration;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JLabel progressLabel;
    private javax.swing.JPanel progressPanel;
    private javax.swing.JTable tblCsoConfiguration;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new SwmmWizardPanelProjectUI object.
     *
     * @param   model  DOCUMENT ME!
     *
     * @throws  WizardInitialisationException  DOCUMENT ME!
     */
    public UploadWizardPanelCSOsUI(final UploadWizardPanelCSOs model) throws WizardInitialisationException {
        this.model = model;

        initComponents();

        // name of the wizard step
        this.setName(NbBundle.getMessage(
                UploadWizardPanelCSOsUI.class,
                "UploadWizardPanelCSOs.this.name")); // NOI18N
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    void init() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("initialising user interface");
        }

        if (this.model.isCopyCSOsComplete()
                    && (this.model.getCopiedCSOs() != null)
                    && !this.model.getCopiedCSOs().isEmpty()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("CSOs already available");
            }
        } else if (this.model.isCopyCSOsInProgress()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("CSO copy thread still in progress");
            }
        } else if (this.model.isCopyCSOsErroneous()) {
            LOG.warn("CSO copy process was erroneous");
            progressBar.setIndeterminate(false);
            org.openide.awt.Mnemonics.setLocalizedText(
                progressLabel,
                org.openide.util.NbBundle.getMessage(
                    UploadWizardPanelCSOsUI.class,
                    "UploadWizardPanelCSOsUI.progressLabel.error"));   // NOI18N
            ((CardLayout)cardPanel.getLayout()).show(cardPanel, "progress");
        } else if (this.model.getSelectedSwmmProject() != -1) {
            Mnemonics.setLocalizedText(
                progressLabel,
                NbBundle.getMessage(
                    UploadWizardPanelCSOsUI.class,
                    "UploadWizardPanelCSOsUI.progressLabel.loading")); // NOI18N
            progressBar.setIndeterminate(true);
            ((CardLayout)cardPanel.getLayout()).show(cardPanel, "progress");

            final CsoCopyThread csoCopyThread = new CsoCopyThread();
            SudplanConcurrency.getSudplanGeneralPurposePool().execute(csoCopyThread);
        } else {
            progressBar.setIndeterminate(false);
            org.openide.awt.Mnemonics.setLocalizedText(
                progressLabel,
                org.openide.util.NbBundle.getMessage(
                    UploadWizardPanelCSOsUI.class,
                    "UploadWizardPanelCSOsUI.progressLabel.error")); // NOI18N
            ((CardLayout)cardPanel.getLayout()).show(cardPanel, "progress");
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception              WizardInitialisationException DOCUMENT ME!
     * @throws  IllegalStateException  DOCUMENT ME!
     */
    private CsoConfigurationTableModel copyCSOs() throws Exception {
        // before we can copy the CSOs we have to perstis the new swmm bean ot obain an ID
        CidsBean newSwmmProject = model.getNewSwmmProjectBean();
        final String projectName = (newSwmmProject.getProperty("title") != null)
            ? newSwmmProject.getProperty("title").toString() : "";
        if (LOG.isDebugEnabled()) {
            LOG.debug("saving new SWMM Project '" + projectName + "'");
        }
        newSwmmProject = newSwmmProject.persist();
        model.setNewSwmmProjectBean(newSwmmProject);

        final String domain = SessionManager.getSession().getUser().getDomain();

        final ServerActionParameter oldProjectparameter = new ServerActionParameter(
                CopyCSOsAction.PARAMETER_OLD_PROJECT,
                String.valueOf(model.getSelectedSwmmProject()));
        final ServerActionParameter newProjectparameter = new ServerActionParameter(
                CopyCSOsAction.PARAMETER_NEW_PROJECT,
                newSwmmProject.getProperty("id").toString());
        final ServerActionParameter newProjectNameParameter = new ServerActionParameter(
                CopyCSOsAction.PARAMETER_NEW_PROJECT_NAME,
                projectName);
        if (LOG.isDebugEnabled()) {
            LOG.debug("copying CSOs of SWMM Project #" + oldProjectparameter.getValue()
                        + " to new SWMM Project #" + newProjectparameter.getValue());
        }
        final Object object = SessionManager.getProxy()
                    .executeTask(
                        CopyCSOsAction.CSO_SERVER_ACTION,
                        domain,
                        null,
                        oldProjectparameter,
                        newProjectparameter,
                        newProjectNameParameter);

        if ((object != null) && List.class.isAssignableFrom(object.getClass())) {
            final List<MetaObject> copiedCSOs = (List<MetaObject>)object;
            LOG.info(copiedCSOs.size() + " CSOs copied to SWMM Project #" + newProjectparameter.getValue());
            model.setCopiedCSOs(copiedCSOs);
            return new CsoConfigurationTableModel(copiedCSOs);
        } else {
            throw new IllegalStateException("copied CSOs list is either null or not of type java.util.List: " + object);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        csoConfigurationPanel = new javax.swing.JPanel();
        cardPanel = new javax.swing.JPanel();
        jScrollPaneCsoConfiguration = new javax.swing.JScrollPane();
        tblCsoConfiguration = new javax.swing.JTable();
        progressPanel = new javax.swing.JPanel();
        progressBar = new javax.swing.JProgressBar();
        progressLabel = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        csoConfigurationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    UploadWizardPanelCSOsUI.class,
                    "UploadWizardPanelCSOsUI.csoConfigurationPanel.border.title"))); // NOI18N
        csoConfigurationPanel.setLayout(new java.awt.GridBagLayout());

        cardPanel.setLayout(new java.awt.CardLayout());

        tblCsoConfiguration.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][] {},
                new String[] {}));
        jScrollPaneCsoConfiguration.setViewportView(tblCsoConfiguration);

        cardPanel.add(jScrollPaneCsoConfiguration, "csos");

        progressPanel.setLayout(new java.awt.GridBagLayout());

        progressBar.setIndeterminate(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 25, 5, 25);
        progressPanel.add(progressBar, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            progressLabel,
            org.openide.util.NbBundle.getMessage(
                UploadWizardPanelCSOsUI.class,
                "UploadWizardPanelCSOsUI.progressLabel.loading")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        progressPanel.add(progressLabel, gridBagConstraints);

        cardPanel.add(progressPanel, "progress");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 10, 10);
        csoConfigurationPanel.add(cardPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(csoConfigurationPanel, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public UploadWizardPanelCSOs getModel() {
        return model;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class CsoConfigurationTableModel extends AbstractTableModel {

        //~ Instance fields ----------------------------------------------------

        private final transient Logger LOG = Logger.getLogger(CsoConfigurationTableModel.class);
        private final List<MetaObject> csoConfigurations;
        // private final Class[] columnClasses = { String.class, String.class, String.class,String.class,String.class };

        private final String[] columnNames = {
                NbBundle.getMessage(UploadWizardPanelCSOsUI.class, "CsoConfigurationTableModel.column.name"),
                NbBundle.getMessage(UploadWizardPanelCSOsUI.class, "CsoConfigurationTableModel.column.outfall"),
                NbBundle.getMessage(UploadWizardPanelCSOsUI.class, "CsoConfigurationTableModel.column.storage_unit"),
                NbBundle.getMessage(
                    UploadWizardPanelCSOsUI.class,
                    "CsoConfigurationTableModel.column.max_throttle_discharge")
            };

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new CsoConfigurationTableModel object.
         *
         * @param  csoConfigurations  metaObjects DOCUMENT ME!
         */
        private CsoConfigurationTableModel(final List<MetaObject> csoConfigurations) {
            this.csoConfigurations = csoConfigurations;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public int getRowCount() {
            return csoConfigurations.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public Object getValueAt(final int rowIndex, final int columnIndex) {
            final CidsBean csoBean = csoConfigurations.get(rowIndex).getBean();
            assert csoBean != null : "CSO Bean must not be null";

            switch (columnIndex) {
                case 0: {
                    return (csoBean.getProperty("name") != null) ? csoBean.getProperty("name").toString() : "";
                }
                case 1: {
                    return (csoBean.getProperty("outfall") != null)
                        ? ((CidsBean)csoBean.getProperty("outfall")).getProperty("name").toString() : "";
                }
                case 2: {
                    return (csoBean.getProperty("storage_unit") != null)
                        ? ((CidsBean)csoBean.getProperty("storage_unit")).getProperty("name").toString() : "";
                }
                case 3: {
                    return (csoBean.getProperty("volume") != null) ? csoBean.getProperty("volume").toString() : "";
                }
                case 4: {
                    return (csoBean.getProperty("max_throttle_discharge") != null)
                        ? csoBean.getProperty("max_throttle_discharge").toString() : "";
                }
            }

            return "";
        }

        @Override
        public void setValueAt(final Object value, final int row, final int col) {
            LOG.warn("changing of CSO configurations not supported!");
        }

        @Override
        public String getColumnName(final int col) {
            return columnNames[col];
        }

        @Override
        public Class getColumnClass(final int col) {
            // return columnClasses[col];
            return String.class;
        }

        @Override
        public boolean isCellEditable(final int row, final int col) {
            return false;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class CsoCopyThread implements Runnable {

        //~ Instance fields ----------------------------------------------------

        private final transient Logger LOG = Logger.getLogger(CsoCopyThread.class);
        private transient CsoConfigurationTableModel csoConfigurationTableModel;

        //~ Methods ------------------------------------------------------------

        @Override
        public void run() {
            model.setCopyCSOsInProgress(true);
            try {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("CsoCopyThread: copying CSOs from SWMM project "
                                + model.getSelectedSwmmProject());
                }

                csoConfigurationTableModel = copyCSOs();
                model.setCopyCSOsComplete(true);

                EventQueue.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug(
                                    "CsoUpdater: updating table with "
                                            + csoConfigurationTableModel.getRowCount()
                                            + " copied CSOs");
                            }

                            progressBar.setIndeterminate(false);
                            tblCsoConfiguration.setModel(csoConfigurationTableModel);
                            ((CardLayout)cardPanel.getLayout()).show(cardPanel, "csos");
                        }
                    });
            } catch (Exception e) {
                LOG.error("CsoCopyThread: could not copy CSOs: " + e.getMessage(), e);
                model.setCopyCSOsErroneous(true);
                EventQueue.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            progressBar.setIndeterminate(false);
                            org.openide.awt.Mnemonics.setLocalizedText(
                                progressLabel,
                                org.openide.util.NbBundle.getMessage(
                                    UploadWizardPanelCSOsUI.class,
                                    "UploadWizardPanelCSOsUI.progressLabel.error")); // NOI18N
                        }
                    });
            }
        }
    }
}
