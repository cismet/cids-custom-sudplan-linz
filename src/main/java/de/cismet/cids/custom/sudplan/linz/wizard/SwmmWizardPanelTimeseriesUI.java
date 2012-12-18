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

import org.openide.awt.Mnemonics;
import org.openide.util.NbBundle;

import java.awt.CardLayout;
import java.awt.EventQueue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import de.cismet.cids.custom.sudplan.WizardInitialisationException;
import de.cismet.cids.custom.sudplan.commons.SudplanConcurrency;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.tools.CismetThreadPool;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public final class SwmmWizardPanelTimeseriesUI extends JPanel {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(SwmmWizardPanelTimeseriesUI.class);

    //~ Instance fields --------------------------------------------------------

    private final transient SwmmWizardPanelTimeseries model;
    // private transient TimeseriesTableModel timeseriesTableModel;
    private transient HashSet<Integer> lastStationIds = new HashSet<Integer>();
    private transient boolean lastForecast;
    private transient TimeseriesUpdater timeseriesUpdater;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel cardPanel;
    private javax.swing.JTextArea descriptionArea;
    private javax.swing.JScrollPane jScrollPaneTimeseries;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JLabel progressLabel;
    private javax.swing.JPanel progressPanel;
    private javax.swing.JTable tblTimeseries;
    private javax.swing.JPanel timeseriesPanel;
    private javax.swing.JPanel timeseriesView;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new SwmmWizardPanelProjectUI object.
     *
     * @param   model  DOCUMENT ME!
     *
     * @throws  WizardInitialisationException  DOCUMENT ME!
     */
    public SwmmWizardPanelTimeseriesUI(final SwmmWizardPanelTimeseries model) throws WizardInitialisationException {
        this.model = model;

        initComponents();

        // name of the wizard step
        this.setName(NbBundle.getMessage(
                SwmmWizardPanelTimeseries.class,
                "SwmmWizardPanelTimeseries.this.name")); // NOI18N

        this.tblTimeseries.getSelectionModel().addListSelectionListener(new TimeseriesListener());
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    void init() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("initialising user interface");
        }
        try {
            if ((model.isForecast() == this.lastForecast)
                        && (this.lastStationIds.size() == model.getStationIds().size())
                        && this.lastStationIds.containsAll(model.getStationIds())) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("selected stations (" + model.getStationIds().size() + ") did not change, "
                                + "no need to update the timeseries table");
                }

                if ((timeseriesUpdater != null) && timeseriesUpdater.isRunning()) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("timeseries update thread is still running");
                    }
                } else {
                    ((TimeseriesTableModel)this.tblTimeseries.getModel()).setSelectedTimeseries(
                        model.getTimeseriesIds());
                    ((CardLayout)cardPanel.getLayout()).show(cardPanel, "timeseries");
                }
                return;
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("selected stations changed, updating the timeseries table");
            }

            Mnemonics.setLocalizedText(
                progressLabel,
                NbBundle.getMessage(
                    SwmmWizardPanelTimeseriesUI.class,
                    "SwmmWizardPanelTimeseriesUI.progressLabel.text")); // NOI18N
            progressBar.setIndeterminate(true);
            ((CardLayout)cardPanel.getLayout()).show(cardPanel, "progress");

            this.lastForecast = model.isForecast();
            this.lastStationIds.clear();
            this.lastStationIds.addAll(model.getStationIds());
            model.getTimeseriesIds().clear();

            if ((timeseriesUpdater != null) && timeseriesUpdater.isRunning()) {
                LOG.warn("another timeseries update thread is running, stopping thred");
                timeseriesUpdater.stopIt();
            }

            timeseriesUpdater = new TimeseriesUpdater();
            SudplanConcurrency.getSudplanGeneralPurposePool().execute(timeseriesUpdater);

            // this.initTimeseries(model.getStationIds(), model.isForecast());
            // this.timeseriesTableModel.setSelectedTimeseries(model.getTimeseriesIds());
            // this.tblTimeseries.setModel(this.timeseriesTableModel);

        } catch (Exception e) {
            LOG.error(e.getMessage(), e);

            progressBar.setIndeterminate(false);
            org.openide.awt.Mnemonics.setLocalizedText(
                progressLabel,
                org.openide.util.NbBundle.getMessage(
                    SwmmWizardPanelTimeseriesUI.class,
                    "SwmmWizardPanelTimeseriesUI.progressLabel.error")); // NOI18N
            ((CardLayout)cardPanel.getLayout()).show(cardPanel, "progress");
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   stationIds  DOCUMENT ME!
     * @param   forecast    DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  WizardInitialisationException  DOCUMENT ME!
     */
    private TimeseriesTableModel initTimeseries(final List<Integer> stationIds, final boolean forecast)
            throws WizardInitialisationException {
        final String domain = SessionManager.getSession().getUser().getDomain();
        final MetaClass mc = ClassCacheMultiple.getMetaClass(domain, SwmmPlusEtaWizardAction.TABLENAME_TIMESERIES);

        if (mc == null) {
            throw new WizardInitialisationException("cannot fetch timeseries metaclass"); // NOI18N
        }

        final StringBuilder sb = new StringBuilder();

        sb.append("SELECT ").append(mc.getID()).append(',').append(mc.getPrimaryKey()); // NOI18N
        sb.append(" FROM ").append(mc.getTableName());                                  // NOI18N

        assert stationIds.size() > 0 : "no station ids defined";
        sb.append(" WHERE station IN (");

        int i = 0;
        for (final int stationId : stationIds) {
            i++;
            sb.append(stationId);
            if (i < stationIds.size()) {
                sb.append(", ");
            }
        }

        sb.append(") AND forecast = ");

        if (forecast) {
            sb.append("TRUE ");
        } else {
            sb.append("FALSE ");
        }

        final ClassAttribute ca = mc.getClassAttribute("sortingColumn"); // NOI18N
        if (ca != null) {
            sb.append("ORDER BY ").append(ca.getValue());                // NOI18N
        }

        final MetaObject[] metaObjects;
        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug(sb);
            }
            metaObjects = SessionManager.getProxy().getMetaObjectByQuery(sb.toString(), 0);
        } catch (final ConnectionException ex) {
            final String message = "cannot get time series  meta objects from database"; // NOI18N
            LOG.error(message, ex);
            throw new WizardInitialisationException(message, ex);
        }

        return new TimeseriesTableModel(metaObjects);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        timeseriesPanel = new javax.swing.JPanel();
        cardPanel = new javax.swing.JPanel();
        progressPanel = new javax.swing.JPanel();
        progressBar = new javax.swing.JProgressBar();
        progressLabel = new javax.swing.JLabel();
        timeseriesView = new javax.swing.JPanel();
        jScrollPaneTimeseries = new javax.swing.JScrollPane();
        tblTimeseries = new javax.swing.JTable();
        descriptionArea = new javax.swing.JTextArea();

        setLayout(new java.awt.GridBagLayout());

        timeseriesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    SwmmWizardPanelTimeseriesUI.class,
                    "SwmmWizardPanelTimeseriesUI.timeseriesPanel.border.title"))); // NOI18N
        timeseriesPanel.setLayout(new java.awt.GridBagLayout());

        cardPanel.setLayout(new java.awt.CardLayout());

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
                SwmmWizardPanelTimeseriesUI.class,
                "SwmmWizardPanelTimeseriesUI.progressLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        progressPanel.add(progressLabel, gridBagConstraints);

        cardPanel.add(progressPanel, "progress");

        timeseriesView.setLayout(new java.awt.GridBagLayout());

        tblTimeseries.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][] {},
                new String[] {}));
        tblTimeseries.setRowSelectionAllowed(false);
        tblTimeseries.setSelectionBackground(new java.awt.Color(255, 255, 255));
        jScrollPaneTimeseries.setViewportView(tblTimeseries);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        timeseriesView.add(jScrollPaneTimeseries, gridBagConstraints);

        descriptionArea.setColumns(40);
        descriptionArea.setLineWrap(true);
        descriptionArea.setRows(4);
        descriptionArea.setEnabled(false);
        descriptionArea.setFocusable(false);
        descriptionArea.setMinimumSize(new java.awt.Dimension(200, 60));
        descriptionArea.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 1, 0, 1);
        timeseriesView.add(descriptionArea, gridBagConstraints);

        cardPanel.add(timeseriesView, "timeseries");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 10, 10);
        timeseriesPanel.add(cardPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(timeseriesPanel, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public SwmmWizardPanelTimeseries getModel() {
        return model;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class TimeseriesTableModel extends AbstractTableModel {

        //~ Instance fields ----------------------------------------------------

        private final MetaObject[] timeseries;
        private final boolean[] selectedTimeseries;
        // private final String[] columnNames = { "Name", "Beschreibung", "Auswahl" };
        // private final Class[] columnClasses = { String.class, String.class, Boolean.class };
        private final String[] columnNames = {
                org.openide.util.NbBundle.getMessage(
                    SwmmWizardPanelTimeseriesUI.class,
                    "SwmmWizardPanelTimeseriesUI.table.name"),
                org.openide.util.NbBundle.getMessage(
                    SwmmWizardPanelTimeseriesUI.class,
                    "SwmmWizardPanelTimeseriesUI.table.selection")
            };
        private final Class[] columnClasses = { String.class, Boolean.class };

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new TimeseriesTableModel object.
         *
         * @param  metaObjects  DOCUMENT ME!
         */
        private TimeseriesTableModel(final MetaObject[] metaObjects) {
            this.timeseries = metaObjects;
            this.selectedTimeseries = new boolean[timeseries.length];
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public int getRowCount() {
            return timeseries.length;
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        /**
         * DOCUMENT ME!
         *
         * @param   row  DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public CidsBean getTimeseriesBean(final int row) {
            return this.timeseries[row].getBean();
        }

        @Override
        public Object getValueAt(final int rowIndex, final int columnIndex) {
            switch (columnIndex) {
                case 0: {
                    return timeseries[rowIndex].getName();
                }
//                case 1: {
//                    return "keine Beschreibung vorhanden";
//                }
                case 1: {
                    return selectedTimeseries[rowIndex];
                }
            }

            return null;
        }

        @Override
        public void setValueAt(final Object value, final int row, final int col) {
//            if (LOG.isDebugEnabled()) {
//                LOG.debug("Setting value at " + row + "," + col
//                            + " to " + value + " (an instance of " + value.getClass() + ")");
//            }

            if (col == 1) {
                this.selectedTimeseries[row] = (Boolean)value;
            }

            // update selected timeseries in the model
            CismetThreadPool.execute(new Runnable() {

                    @Override
                    public void run() {
                        saveSelectedTimeseries();
                    }
                });

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
            return col == 1;
        }

        /**
         * DOCUMENT ME!
         */
        private void saveSelectedTimeseries() {
            final ArrayList<Integer> selectedTimeseriesIds = new ArrayList<Integer>();
            final ArrayList<String> selectedTimeseriesURLs = new ArrayList<String>();

            for (int i = 0; i < this.selectedTimeseries.length; i++) {
                if (this.selectedTimeseries[i]) {
                    selectedTimeseriesIds.add(this.timeseries[i].getId());
                    selectedTimeseriesURLs.add(this.timeseries[i].getBean().getProperty("uri").toString());
                }
            }

            model.setTimeseries(selectedTimeseriesIds, selectedTimeseriesURLs);
        }

        /**
         * DOCUMENT ME!
         *
         * @param  selectedStationsList  DOCUMENT ME!
         */
        private void setSelectedTimeseries(final List<Integer> selectedStationsList) {
            for (int i = 0; i < this.selectedTimeseries.length; i++) {
                if (selectedStationsList.contains(timeseries[i].getId())) {
                    this.selectedTimeseries[i] = true;
                } else {
                    this.selectedTimeseries[i] = false;
                }
            }

            this.fireTableRowsUpdated(0, this.selectedTimeseries.length);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class TimeseriesUpdater implements Runnable {

        //~ Instance fields ----------------------------------------------------

        private transient boolean run = true;
        private TimeseriesTableModel timeseriesTableModel;

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         */
        public void stopIt() {
            run = false;
            LOG.warn("TimeseriesUpdater stopped");
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
                        LOG.debug("TimeseriesUpdater: loading results");
                    }
                    timeseriesTableModel = initTimeseries(model.getStationIds(), model.isForecast());
                } catch (Exception e) {
                    LOG.error("TimeseriesUpdater: could not retrieve timeseries: " + e.getMessage(), e);
                    run = false;
                    EventQueue.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                progressBar.setIndeterminate(false);
                                org.openide.awt.Mnemonics.setLocalizedText(
                                    progressLabel,
                                    org.openide.util.NbBundle.getMessage(
                                        SwmmWizardPanelTimeseriesUI.class,
                                        "SwmmWizardPanelTimeseriesUI.progressLabel.error")); // NOI18N
                            }
                        });
                }

                if (run) {
                    EventQueue.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug("TimeseriesUpdater: updating loaded results");
                                }
                                tblTimeseries.setModel(timeseriesTableModel);
                                tblTimeseries.getColumnModel().getColumn(1).setPreferredWidth(40);
                                tblTimeseries.getColumnModel().getColumn(1).setMaxWidth(60);
                                ((CardLayout)cardPanel.getLayout()).show(cardPanel, "timeseries");
                                run = false;
                            }
                        });
                } else {
                    LOG.warn("TimeseriesUpdater stopped, ignoring retrieved results");
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class TimeseriesListener implements ListSelectionListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void valueChanged(final ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                final CidsBean timeseriesBean = ((TimeseriesTableModel)tblTimeseries.getModel()).getTimeseriesBean(
                        e.getLastIndex());

                if ((timeseriesBean != null) && (timeseriesBean.getProperty("description") != null)) {
                    descriptionArea.setText(timeseriesBean.getProperty("description").toString());
                }
            }
        }
    }
}
