/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.linz.wizard;

import Sirius.navigator.connection.SessionManager;

import org.apache.log4j.Logger;

import org.openide.awt.Mnemonics;
import org.openide.util.NbBundle;

import java.awt.CardLayout;
import java.awt.EventQueue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.table.AbstractTableModel;

import de.cismet.cids.custom.sudplan.WizardInitialisationException;
import de.cismet.cids.custom.sudplan.commons.SudplanConcurrency;
import de.cismet.cids.custom.sudplan.linz.EtaConfiguration;
import de.cismet.cids.custom.sudplan.linz.server.search.LightwightCsoSearch;
import de.cismet.cids.custom.sudplan.linz.server.search.LightwightCsoSearch.LightwightCso;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public final class EtaWizardPanelEtaConfigurationUI extends JPanel {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(EtaWizardPanelEtaConfigurationUI.class);

    //~ Instance fields --------------------------------------------------------

    private final transient EtaWizardPanelEtaConfiguration model;
    private transient int lastSwmmProjectId = -1;
    private CsoUpdater csoUpdater;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel cardPanel;
    private javax.swing.JPanel etaConfigurationPanel;
    private javax.swing.JScrollPane jScrollPaneEtaConfiguration;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JLabel progressLabel;
    private javax.swing.JPanel progressPanel;
    private javax.swing.JTable tblEtaConfiguration;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new SwmmWizardPanelProjectUI object.
     *
     * @param   model  DOCUMENT ME!
     *
     * @throws  WizardInitialisationException  DOCUMENT ME!
     */
    public EtaWizardPanelEtaConfigurationUI(final EtaWizardPanelEtaConfiguration model)
            throws WizardInitialisationException {
        this.model = model;

        initComponents();

        // name of the wizard step
        this.setName(NbBundle.getMessage(
                EtaWizardPanelEtaConfigurationUI.class,
                "EtaWizardPanelEtaConfiguration.this.name")); // NOI18N
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    void init() {
//        if (LOG.isDebugEnabled()) {
//            LOG.debug("initialising user interface");
//        }
        try {
            assert this.model.getSwmmProjectId() != -1 : "SWMM Project id cannot be -1!";

            if ((this.model.getEtaConfigurations() != null) && !this.model.getEtaConfigurations().isEmpty()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("ETA Configurations already available, updating Table with stored ETA Configuration");
                }

                if ((csoUpdater != null) && csoUpdater.isRunning()) {
                    LOG.warn("A CSO update thread is running, stopping Thread");
                    csoUpdater.stopIt();
                }

                this.tblEtaConfiguration.setModel(new EtaConfigurationTableModel(this.model.getEtaConfigurations()));

                // trigger change event
                // this.model.setEtaConfigurations(this.model.getEtaConfigurations());
                this.model.fireChangeEvent();
            } else if (this.model.getSwmmProjectId() != this.lastSwmmProjectId) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("project id changed (" + this.model.getSwmmProjectId() + "), loading CSO list");
                }

                Mnemonics.setLocalizedText(
                    progressLabel,
                    NbBundle.getMessage(
                        EtaWizardPanelEtaConfigurationUI.class,
                        "EtaWizardPanelEtaConfigurationUI.progressLabel.text")); // NOI18N
                progressBar.setIndeterminate(true);
                ((CardLayout)cardPanel.getLayout()).show(cardPanel, "progress");

                if ((csoUpdater != null) && csoUpdater.isRunning()) {
                    LOG.warn("another cso update thread is running, stopping thred");
                    csoUpdater.stopIt();
                }

                csoUpdater = new CsoUpdater();
                SudplanConcurrency.getSudplanGeneralPurposePool().execute(csoUpdater);
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("project id did not change (" + this.lastSwmmProjectId + "), using cached CSO List");
                }

                if ((csoUpdater != null) && csoUpdater.isRunning()) {
                    if (LOG.isDebugEnabled()) {
                        LOG.warn("cso update thread is still running, waiting for thred to finish");
                    }
                } else {
                    ((CardLayout)cardPanel.getLayout()).show(cardPanel, "csos");
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            progressBar.setIndeterminate(false);
            org.openide.awt.Mnemonics.setLocalizedText(
                progressLabel,
                org.openide.util.NbBundle.getMessage(
                    EtaWizardPanelEtaConfigurationUI.class,
                    "EtaWizardPanelEtaConfiguration.progressLabel.error")); // NOI18N
            ((CardLayout)cardPanel.getLayout()).show(cardPanel, "progress");
        }

        this.lastSwmmProjectId = this.model.getSwmmProjectId();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   swmmProjectId  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  WizardInitialisationException  DOCUMENT ME!
     */
    private EtaConfigurationTableModel initCSOs(final int swmmProjectId) throws WizardInitialisationException {
        final String domain = SessionManager.getSession().getUser().getDomain();

        final LightwightCsoSearch csoSearch = new LightwightCsoSearch(
                domain,
                swmmProjectId);

        final Collection<LightwightCso> lightwightCsos;
        try {
            lightwightCsos = SessionManager.getProxy().customServerSearch(csoSearch);
        } catch (Exception ex) {
            final String message = "could not get CSO for SWMM Project #" + swmmProjectId + " from localserver '"
                        + domain + "'";
            LOG.error(message, ex);
            throw new WizardInitialisationException(message, ex);
        }

        List<EtaConfiguration> etaConfigurations = new ArrayList(0);
        if ((lightwightCsos != null) && !lightwightCsos.isEmpty()) {
            etaConfigurations = new ArrayList<EtaConfiguration>(lightwightCsos.size());

            for (final LightwightCso lightwightCso : lightwightCsos) {
                final EtaConfiguration etaConfiguration = new EtaConfiguration();
                etaConfiguration.setName(lightwightCso.getName());
                etaConfiguration.setCso(lightwightCso.getId());
                etaConfigurations.add(etaConfiguration);
            }
            Collections.sort(etaConfigurations);
        } else {
            LOG.error("search for CSOs for SWMM Project #" + swmmProjectId + " from localserver '"
                        + SessionManager.getSession().getUser().getDomain() + "' did not return any results");
        }
        // trigger change event
        this.model.setEtaConfigurations(etaConfigurations);
        return new EtaConfigurationTableModel(model.getEtaConfigurations());
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        etaConfigurationPanel = new javax.swing.JPanel();
        cardPanel = new javax.swing.JPanel();
        jScrollPaneEtaConfiguration = new javax.swing.JScrollPane();
        tblEtaConfiguration = new javax.swing.JTable();
        progressPanel = new javax.swing.JPanel();
        progressBar = new javax.swing.JProgressBar();
        progressLabel = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        etaConfigurationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    EtaWizardPanelEtaConfigurationUI.class,
                    "EtaWizardPanelEtaConfigurationUI.etaConfigurationPanel.border.title"))); // NOI18N
        etaConfigurationPanel.setLayout(new java.awt.GridBagLayout());

        cardPanel.setLayout(new java.awt.CardLayout());

        tblEtaConfiguration.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][] {},
                new String[] {}));
        tblEtaConfiguration.setRowSelectionAllowed(false);
        tblEtaConfiguration.setSelectionBackground(new java.awt.Color(255, 255, 255));
        tblEtaConfiguration.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPaneEtaConfiguration.setViewportView(tblEtaConfiguration);

        cardPanel.add(jScrollPaneEtaConfiguration, "csos");

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
                EtaWizardPanelEtaConfigurationUI.class,
                "EtaWizardPanelEtaConfigurationUI.progressLabel.text")); // NOI18N
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
        etaConfigurationPanel.add(cardPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(etaConfigurationPanel, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public EtaWizardPanelEtaConfiguration getModel() {
        return model;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class EtaConfigurationTableModel extends AbstractTableModel {

        //~ Instance fields ----------------------------------------------------

        private final transient Logger LOG = Logger.getLogger(EtaConfigurationTableModel.class);

        private final List<EtaConfiguration> etaConfigurations;

        private final String[] columnNames = {
                NbBundle.getMessage(EtaWizardPanelEtaConfigurationUI.class, "EtaConfigurationTableModel.column.cso"),
                NbBundle.getMessage(EtaWizardPanelEtaConfigurationUI.class, "EtaConfigurationTableModel.column.active"),
                NbBundle.getMessage(EtaWizardPanelEtaConfigurationUI.class, "EtaConfigurationTableModel.column.eta_sed"),
            };

        private final Class[] columnClasses = { String.class, Boolean.class, Float.class };

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new EtaConfigurationTableModel object.
         *
         * @param  etaConfigurations  metaObjects DOCUMENT ME!
         */
        private EtaConfigurationTableModel(final List<EtaConfiguration> etaConfigurations) {
            this.etaConfigurations = etaConfigurations;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public int getRowCount() {
            return etaConfigurations.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public Object getValueAt(final int rowIndex, final int columnIndex) {
            switch (columnIndex) {
                case 0: {
                    return etaConfigurations.get(rowIndex).getName();
                }
                case 1: {
                    return etaConfigurations.get(rowIndex).isEnabled();
                }
                case 2: {
                    return etaConfigurations.get(rowIndex).getSedimentationEfficency();
                }
            }

            return null;
        }

        @Override
        public void setValueAt(final Object value, final int row, final int col) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("updating CSO configuration at " + row + "/" + col + " to '" + value + "'");
            }
            if (col == 1) {
                final boolean enabled = (Boolean)value;
                etaConfigurations.get(row).setEnabled(enabled);
                if (!enabled) {
                    etaConfigurations.get(row).setSedimentationEfficency(0.0f);
                }

                if (LOG.isDebugEnabled()) {
                    LOG.debug("configurations of CSO '" + etaConfigurations.get(row) + "' changed: considered=" + value
                                + ", need to re-compute total overflow volume!");
                }
            } else if (col == 2) {
                etaConfigurations.get(row).setSedimentationEfficency((Float)value);
            }

            fireTableCellUpdated(row, col);
        }

        @Override
        public String getColumnName(final int col) {
            return columnNames[col];
        }

        @Override
        public Class getColumnClass(final int col) {
            return columnClasses[col];
        }

        @Override
        public boolean isCellEditable(final int row, final int col) {
            return (col == 1) || (col == 2);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class CsoUpdater implements Runnable {

        //~ Instance fields ----------------------------------------------------

        private final transient Logger LOG = Logger.getLogger(CsoUpdater.class);

        private transient boolean run = true;
        private EtaConfigurationTableModel etaConfigurationTableModel;

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         */
        public void stopIt() {
            run = false;
            LOG.warn("CsoUpdater stopped");
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public boolean isRunning() {
            return run;
        }

        @Override
        public void run() {
            if (run) {
                try {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("CsoUpdater: loading results");
                    }
                    etaConfigurationTableModel = initCSOs(model.getSwmmProjectId());
                } catch (Exception e) {
                    LOG.error("CsoUpdater: could not retrieve CSOs: " + e.getMessage(), e);
                    run = false;
                    EventQueue.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                progressBar.setIndeterminate(false);
                                org.openide.awt.Mnemonics.setLocalizedText(
                                    progressLabel,
                                    org.openide.util.NbBundle.getMessage(
                                        EtaWizardPanelEtaConfigurationUI.class,
                                        "EtaWizardPanelEtaConfigurationUI.progressLabel.error")); // NOI18N
                            }
                        });
                }

                if (run) {
                    EventQueue.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug("CsoUpdater: updating loaded results");
                                }
                                tblEtaConfiguration.setModel(etaConfigurationTableModel);
                                ((CardLayout)cardPanel.getLayout()).show(cardPanel, "csos");
                                run = false;
                            }
                        });
                } else {
                    LOG.warn("CsoUpdater stopped, ignoring retrieved results");
                }
            }
        }
    }
}
