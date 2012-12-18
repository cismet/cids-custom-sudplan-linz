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

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.table.AbstractTableModel;

import de.cismet.cids.custom.sudplan.WizardInitialisationException;
import de.cismet.cids.custom.sudplan.linz.SwmmInput;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public final class SwmmWizardPanelStationsUI extends JPanel {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(SwmmWizardPanelStationsUI.class);

    //~ Instance fields --------------------------------------------------------

    private final transient SwmmWizardPanelStations model;
    private transient StationsTableModel stationsTableModel;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox chbForecast;
    private javax.swing.JPanel forecastPanel;
    private javax.swing.JScrollPane jScrollPaneStations;
    private javax.swing.JPanel stationsPanel;
    private javax.swing.JTable tblStations;
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
    public SwmmWizardPanelStationsUI(final SwmmWizardPanelStations model) throws WizardInitialisationException {
        this.model = model;

        initComponents();

        // name of the wizard step
        this.setName(NbBundle.getMessage(
                SwmmWizardPanelStations.class,
                "SwmmWizardPanelStations.this.name"));

        this.initStations();
        this.tblStations.setModel(this.stationsTableModel);
        this.tblStations.getColumnModel().getColumn(1).setPreferredWidth(40);
        this.tblStations.getColumnModel().getColumn(1).setMaxWidth(60);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    void init() {
        this.stationsTableModel.setSelectedStations(model.getStationsIds());

        this.bindingGroup.unbind();
        this.bindingGroup.bind();
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  WizardInitialisationException  DOCUMENT ME!
     */
    private void initStations() throws WizardInitialisationException {
        final String domain = SessionManager.getSession().getUser().getDomain();
        final MetaClass mc = ClassCacheMultiple.getMetaClass(domain, SwmmPlusEtaWizardAction.TABLENAME_MONITOR_STATION);

        if (mc == null) {
            throw new WizardInitialisationException("cannot fetch swmm project metaclass"); // NOI18N
        }

        final StringBuilder sb = new StringBuilder();

        sb.append("SELECT ")
                .append(mc.getID())
                .append(',')
                .append(mc.getTableName())
                .append('.')
                .append(mc.getPrimaryKey()); // NOI18N
        sb.append(" FROM ").append(mc.getTableName());
        sb.append(" WHERE ")
                .append(mc.getTableName())
                .append('.')
                .append(SwmmInput.FK_MONITOR_STATION_KEY)
                .append(" LIKE '")
                .append(SwmmPlusEtaWizardAction.LINZ_RAINFALL_STATION_KEY)
                .append('\'');

        final ClassAttribute ca = mc.getClassAttribute("sortingColumn");                        // NOI18N
        if (ca != null) {
            sb.append(" ORDER BY ").append(ca.getValue());                                      // NOI18N
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("executing SQL statement: \n" + sb);
        }
        final MetaObject[] metaObjects;
        try {
            metaObjects = SessionManager.getProxy().getMetaObjectByQuery(sb.toString(), 0);
        } catch (final ConnectionException ex) {
            final String message = "cannot get monitoring station  meta objects from database"; // NOI18N
            LOG.error(message, ex);
            throw new WizardInitialisationException(message, ex);
        }

        this.stationsTableModel = new StationsTableModel(metaObjects);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        stationsPanel = new javax.swing.JPanel();
        jScrollPaneStations = new javax.swing.JScrollPane();
        tblStations = new javax.swing.JTable();
        forecastPanel = new javax.swing.JPanel();
        chbForecast = new javax.swing.JCheckBox();

        setLayout(new java.awt.GridBagLayout());

        stationsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    SwmmWizardPanelStationsUI.class,
                    "SwmmWizardPanelStationsUI.stationsPanel.border.title"))); // NOI18N
        stationsPanel.setLayout(new java.awt.GridBagLayout());

        jScrollPaneStations.setPreferredSize(new java.awt.Dimension(200, 150));

        tblStations.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][] {},
                new String[] {}));
        tblStations.setRowSelectionAllowed(false);
        tblStations.setSelectionBackground(new java.awt.Color(255, 255, 255));
        tblStations.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPaneStations.setViewportView(tblStations);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 10, 10);
        stationsPanel.add(jScrollPaneStations, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 5, 10);
        add(stationsPanel, gridBagConstraints);

        forecastPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    SwmmWizardPanelStationsUI.class,
                    "SwmmWizardPanelStationsUI.forecastPanel.border.title"))); // NOI18N
        forecastPanel.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            chbForecast,
            org.openide.util.NbBundle.getMessage(
                SwmmWizardPanelStationsUI.class,
                "SwmmWizardPanelStationsUI.chbForecast.text")); // NOI18N

        final org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${model.swmmInput.forecast}"),
                chbForecast,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        forecastPanel.add(chbForecast, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 10, 10);
        add(forecastPanel, gridBagConstraints);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public SwmmWizardPanelStations getModel() {
        return model;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class StationsTableModel extends AbstractTableModel {

        //~ Instance fields ----------------------------------------------------

        private final MetaObject[] stations;
        private final boolean[] selectedStations;
        // private final String[] columnNames = { "Name", "Beschreibung", "Auswahl" };
        // private final Class[] columnClasses = { String.class, String.class, Boolean.class };
        private final String[] columnNames = {
                org.openide.util.NbBundle.getMessage(
                    SwmmWizardPanelStationsUI.class,
                    "SwmmWizardPanelStationsUI.table.name"),
                org.openide.util.NbBundle.getMessage(
                    SwmmWizardPanelStationsUI.class,
                    "SwmmWizardPanelStationsUI.table.selection")
            };
        private final Class[] columnClasses = { String.class, Boolean.class };

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new StationsTableModel object.
         *
         * @param  metaObjects  DOCUMENT ME!
         */
        private StationsTableModel(final MetaObject[] metaObjects) {
            this.stations = metaObjects;
            this.selectedStations = new boolean[stations.length];
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public int getRowCount() {
            return stations.length;
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public Object getValueAt(final int rowIndex, final int columnIndex) {
            switch (columnIndex) {
                case 0: {
                    return stations[rowIndex].getName();
                }
//                case 1: {
//                    return "keine Beschreibung vorhanden";
//                }
                case 1: {
                    return selectedStations[rowIndex];
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
                this.selectedStations[row] = (Boolean)value;
            }

            fireTableCellUpdated(row, col);

            // update selected stations
            model.setStationsIds(this.getSelectedStations());
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
         *
         * @return  DOCUMENT ME!
         */
        private List<Integer> getSelectedStations() {
            final ArrayList<Integer> selectedStationsList = new ArrayList<Integer>();

            for (int i = 0; i < this.selectedStations.length; i++) {
                if (this.selectedStations[i]) {
                    selectedStationsList.add(this.stations[i].getId());
                }
            }

            return selectedStationsList;
        }

        /**
         * DOCUMENT ME!
         *
         * @param  selectedStationsList  DOCUMENT ME!
         */
        private void setSelectedStations(final List<Integer> selectedStationsList) {
            for (int i = 0; i < this.selectedStations.length; i++) {
                if (selectedStationsList.contains(stations[i].getId())) {
                    this.selectedStations[i] = true;
                } else {
                    this.selectedStations[i] = false;
                }
            }

            this.fireTableRowsUpdated(0, this.selectedStations.length);
        }
    }
}
