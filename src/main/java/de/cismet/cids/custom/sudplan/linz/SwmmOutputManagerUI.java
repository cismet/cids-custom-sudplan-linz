/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * SwmmOutputManagerUI.java
 *
 * Created on 07.12.2011, 15:18:55
 */
package de.cismet.cids.custom.sudplan.linz;

import Sirius.navigator.ui.ComponentRegistry;

import org.apache.log4j.Logger;

import org.openide.util.NbBundle;

import java.awt.Color;
import java.awt.Component;

import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import de.cismet.cids.custom.sudplan.linz.server.trigger.SwmmResultGeoserverUpdater;

import de.cismet.cismap.commons.XBoundingBox;
import de.cismet.cismap.commons.interaction.CismapBroker;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWMS;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWmsGetMapUrl;

import de.cismet.tools.PropertyReader;

/**
 * DOCUMENT ME!
 *
 * @author   Pascal Dihé
 * @version  $Revision$, $Date$
 */
public class SwmmOutputManagerUI extends javax.swing.JPanel {

    //~ Static fields/initializers ---------------------------------------------

    private static final PropertyReader propertyReader;
    private static final String FILE_PROPERTY = "/de/cismet/cids/custom/sudplan/repositories.properties";

    private static final String GEOSERVER_HOST;
    private static final XBoundingBox LINZ_BB = new XBoundingBox(13.979, 48.102, 14.521, 48.473, "EPSG:4326", false);
    private static final String GEOSERVER_SWMM_LAYER_TEMPLATE;

    private static final transient Logger LOG = Logger.getLogger(SwmmOutputManagerUI.class);

    static {
        propertyReader = new PropertyReader(FILE_PROPERTY);
        GEOSERVER_HOST = propertyReader.getProperty("GEOSERVER_HOST");
        GEOSERVER_SWMM_LAYER_TEMPLATE = propertyReader.getProperty("GEOSERVER_SWMM_LAYER_TEMPLATE");
    }

    //~ Instance fields --------------------------------------------------------

    private final transient SwmmOutputManager outputManager;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable csoTable;
    private javax.swing.JScrollPane csoTableScrollPane;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JButton showInMapButton;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form SwmmOutputManagerUI.
     *
     * @param  outputManager  DOCUMENT ME!
     */
    public SwmmOutputManagerUI(final SwmmOutputManager outputManager) {
        this.outputManager = outputManager;
        initComponents();
        init();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    protected final void init() {
        try {
            final SwmmOutput swmmOutput = outputManager.getUR();
            final ArrayList<CsoOverflow> csoOverflows = new ArrayList<CsoOverflow>(swmmOutput.getCsoOverflows().size());
            csoOverflows.addAll(swmmOutput.getCsoOverflows().values());
            final CsoTableModel csoTableModel = new CsoTableModel(csoOverflows);
            this.csoTable.setModel(csoTableModel);
            final CsoTableCellRenderer csoTableCellRenderer = new CsoTableCellRenderer();
            this.csoTable.setDefaultRenderer(String.class, new CsoTableCellRenderer());
            this.csoTable.setDefaultRenderer(Float.class, new CsoTableCellRenderer());
            this.lblTitle.setText(lblTitle.getText() + swmmOutput.getSwmmRunName());
        } catch (Exception ex) {
            LOG.error("cannot initialise swmm output manager ui: "
                        + ex.getMessage(), ex); // NOI18N
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        lblTitle = new javax.swing.JLabel();
        showInMapButton = new javax.swing.JButton();
        csoTableScrollPane = new javax.swing.JScrollPane();
        csoTable = new javax.swing.JTable();

        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        lblTitle.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(51, 51, 51));
        lblTitle.setText(org.openide.util.NbBundle.getMessage(
                SwmmOutputManagerUI.class,
                "SwmmOutputManagerUI.lblTitle.text"));        // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblTitle, gridBagConstraints);

        showInMapButton.setText(org.openide.util.NbBundle.getMessage(
                SwmmOutputManagerUI.class,
                "SwmmOutputManagerUI.showInMapButton.text")); // NOI18N
        showInMapButton.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    showInMapButtonActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(showInMapButton, gridBagConstraints);

        csoTable.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][] {},
                new String[] {}));
        csoTableScrollPane.setViewportView(csoTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 2.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(csoTableScrollPane, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void showInMapButtonActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_showInMapButtonActionPerformed
        try {
            // TODO add your handling code here:
            final SwmmOutput swmmOutput = outputManager.getUR();
            final String layerName = SwmmResultGeoserverUpdater.GEOSERVER_WORKSPACE
                        + ':' + SwmmResultGeoserverUpdater.VIEW_NAME_BASE + swmmOutput.getSwmmRun();

            LOG.info("showing result of SWMM RUN '" + swmmOutput.getSwmmRunName()
                        + "' (" + swmmOutput.getSwmmRun() + ") in layer '" + layerName + "'");

            final String wmsURL = GEOSERVER_HOST + (GEOSERVER_SWMM_LAYER_TEMPLATE.replace("%LAYERS%", layerName));
            if (LOG.isDebugEnabled()) {
                LOG.debug(wmsURL);
            }

            final SimpleWMS swms = new SimpleWMS(new SimpleWmsGetMapUrl(wmsURL));
            swms.setName(swmmOutput.getSwmmRunName());
            CismapBroker.getInstance().getMappingComponent().getMappingModel().addLayer(swms);
            CismapBroker.getInstance().getMappingComponent().gotoBoundingBoxWithHistory(LINZ_BB);
            ComponentRegistry.getRegistry().showComponent("cismap");
        } catch (Exception ex) {
            LOG.error("could not show result of SWMM RUN in map:" + ex.getMessage(), ex);

            JOptionPane.showMessageDialog(
                this,
                "<html>"
                        + NbBundle.getMessage(SwmmOutputManagerUI.class,
                            "SwmmOutputManagerUI.msgError.text")
                        + "<br/>"
                        + ex.getMessage()
                        + "</html>",
                NbBundle.getMessage(SwmmOutputManagerUI.class,
                    "SwmmOutputManagerUI.msgError.title"),
                JOptionPane.ERROR_MESSAGE);
        }
    } //GEN-LAST:event_showInMapButtonActionPerformed

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class CsoTableCellRenderer extends DefaultTableCellRenderer {

        //~ Instance fields ----------------------------------------------------

        private final transient Color green = new Color(209, 231, 81);

        //~ Methods ------------------------------------------------------------

        @Override
        public Component getTableCellRendererComponent(final JTable table,
                final Object value,
                final boolean isSelected,
                final boolean hasFocus,
                final int row,
                final int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (isSelected) {
                super.setForeground(table.getSelectionForeground());
                super.setBackground(table.getSelectionBackground());
            } else {
                final Object volume = csoTable.getModel().getValueAt(row, 1);
                Color background = table.getBackground();

                if ((volume != null) && (((Float)volume).floatValue() <= 0f)) {
                    background = green;
                } else if (background == null) {
                    background = Color.WHITE;
                }

                super.setBackground(background);
                super.setForeground(table.getForeground());
            }
            return this;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class CsoTableModel extends AbstractTableModel {

        //~ Instance fields ----------------------------------------------------

        final ArrayList<CsoOverflow> csoOverflows;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new CsoTableModel object.
         *
         * @param  csoOverflows  DOCUMENT ME!
         */
        public CsoTableModel(final ArrayList<CsoOverflow> csoOverflows) {
            this.csoOverflows = csoOverflows;
            Collections.sort(this.csoOverflows);
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public int getRowCount() {
            return this.csoOverflows.size();
        }

        @Override
        public int getColumnCount() {
            return 3;
        }

        @Override
        public Class<?> getColumnClass(final int columnIndex) {
            switch (columnIndex) {
                case 0: {
                    return String.class;
                }
                case 1: {
                    return Float.class;
                }
                case 2: {
                    return Float.class;
                }
                default: {
                    return Object.class;
                }
            }
        }

        @Override
        public String getColumnName(final int columnIndex) {
            switch (columnIndex) {
                case 0: {
                    return NbBundle.getMessage(SwmmOutputManagerUI.class, "SwmmOutputManagerUI.column.name");
                }
                case 1: {
                    return NbBundle.getMessage(SwmmOutputManagerUI.class, "SwmmOutputManagerUI.column.volume");
                }
                case 2: {
                    return NbBundle.getMessage(SwmmOutputManagerUI.class, "SwmmOutputManagerUI.column.frequency");
                }
                default: {
                    return "n/a";
                }
            }
        }

        @Override
        public Object getValueAt(final int rowIndex, final int columnIndex) {
            switch (columnIndex) {
                case 0: {
                    return this.csoOverflows.get(rowIndex).getName();
                }
                case 1: {
                    return this.csoOverflows.get(rowIndex).getOverflowVolume();
                }
                case 2: {
                    return this.csoOverflows.get(rowIndex).getOverflowFrequency();
                }
                default: {
                    return -1;
                }
            }
        }
    }
}
