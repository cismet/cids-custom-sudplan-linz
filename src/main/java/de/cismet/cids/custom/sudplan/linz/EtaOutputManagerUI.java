/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * EtaOutputManagerUI.java
 *
 * Created on 07.12.2011, 19:05:30
 */
package de.cismet.cids.custom.sudplan.linz;

import eu.hansolo.steelseries.tools.LedColor;
import eu.hansolo.steelseries.tools.Section;

import org.apache.log4j.Logger;

import org.codehaus.jackson.map.ObjectMapper;

import java.awt.EventQueue;
import java.awt.GridLayout;

import java.io.IOException;

import java.text.DecimalFormat;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.tools.metaobjectrenderer.Titled;

/**
 * DOCUMENT ME!
 *
 * @author   pd
 * @version  $Revision$, $Date$
 */
public class EtaOutputManagerUI extends javax.swing.JPanel implements Titled {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(EtaInputManagerUI.class);

    //~ Instance fields --------------------------------------------------------

    final Section[] R720 = {
            new Section(0, 30, new java.awt.Color(1.0f, 0.0f, 0.0f, 0.3f)),
            new Section(30, 50, new java.awt.Color(1.0f, 1.0f, 0.0f, 0.3f)),
            new Section(50, 100, new java.awt.Color(0.0f, 1.0f, 0.0f, 0.3f)),
        };
    final Section[] SECTION_ETA_SED_75 = {
            new Section(0, 65, new java.awt.Color(1.0f, 0.0f, 0.0f, 0.3f)),
            new Section(65, 75, new java.awt.Color(1.0f, 1.0f, 0.0f, 0.3f)),
            new Section(75, 100, new java.awt.Color(0.0f, 1.0f, 0.0f, 0.3f)),
        };
    private final Section[] SECTION_ETA_HYD_50 = {
            new Section(0, 40, new java.awt.Color(1.0f, 0.0f, 0.0f, 0.3f)),
            new Section(40, 50, new java.awt.Color(1.0f, 1.0f, 0.0f, 0.3f)),
            new Section(50, 100, new java.awt.Color(0.0f, 1.0f, 0.0f, 0.3f)),
        };
    private final Section[] SECTION_ETA_HYD_60 = {
            new Section(0, 50, new java.awt.Color(1.0f, 0.0f, 0.0f, 0.3f)),
            new Section(50, 60, new java.awt.Color(1.0f, 1.0f, 0.0f, 0.3f)),
            new Section(60, 100, new java.awt.Color(0.0f, 1.0f, 0.0f, 0.3f)),
        };
    private final Section[] SECTION_ETA_SED_65 = {
            new Section(0, 55, new java.awt.Color(1.0f, 0.0f, 0.0f, 0.3f)),
            new Section(55, 65, new java.awt.Color(1.0f, 1.0f, 0.0f, 0.3f)),
            new Section(65, 100, new java.awt.Color(0.0f, 1.0f, 0.0f, 0.3f)),
        };
    private final transient EtaOutputManager outputManager;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel barsResultPanel;
    private eu.hansolo.steelseries.gauges.LinearBargraph etaHydBar;
    private eu.hansolo.steelseries.gauges.Radial etaHydGauge;
    private eu.hansolo.steelseries.gauges.LinearBargraph etaSedBar;
    private eu.hansolo.steelseries.gauges.Radial etaSedGauge;
    private javax.swing.JTextField fld_720;
    private javax.swing.JTextField fld_etaHydActual;
    private javax.swing.JTextField fld_etaHydRequired;
    private javax.swing.JTextField fld_etaSedActual;
    private javax.swing.JTextField fld_etaSedRequired;
    private javax.swing.JTextField fld_totalOverflowVolume;
    private javax.swing.JPanel gaugesResultPanel;
    private javax.swing.JLabel lblEtaHyd;
    private javax.swing.JLabel lblEtaHydBar;
    private javax.swing.JLabel lblEtaSed;
    private javax.swing.JLabel lblEtaSedBar;
    private javax.swing.JLabel lblR720;
    private javax.swing.JLabel lblR720Bar;
    private javax.swing.JLabel lblTotalOverflow;
    private javax.swing.JLabel lblTotalOverflowBar;
    private javax.swing.JLabel lbl_etaHydActual;
    private javax.swing.JLabel lbl_etaHydRequired;
    private javax.swing.JLabel lbl_etaSedActual;
    private javax.swing.JLabel lbl_etaSedRequired;
    private javax.swing.JLabel lbl_r720;
    private javax.swing.JLabel lbl_totalOverflowVolume;
    private eu.hansolo.steelseries.gauges.Linear r720Bar;
    private eu.hansolo.steelseries.gauges.Radial2Top r720Gauge;
    private javax.swing.JPanel singleResultPanel;
    private javax.swing.JPanel spacerOne;
    private javax.swing.JPanel spacerTwo;
    private eu.hansolo.steelseries.gauges.Linear totalOverflowBar;
    private eu.hansolo.steelseries.gauges.Radial2Top totalOverflowGauge;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form EtaOutputManagerUI.
     *
     * @param  outputManager  DOCUMENT ME!
     */
    public EtaOutputManagerUI(final EtaOutputManager outputManager) {
        this.outputManager = outputManager;
        initComponents();
        // this.r720Gauge.setFrameEffect(eu.hansolo.steelseries.tools.FrameEffect.EFFECT_INNER_FRAME);
        // this.totalOverflowGauge.setFrameEffect(eu.hansolo.steelseries.tools.FrameEffect.EFFECT_INNER_FRAME);
        this.r720Bar.setFrameEffect(eu.hansolo.steelseries.tools.FrameEffect.EFFECT_INNER_FRAME);
        this.totalOverflowBar.setFrameEffect(eu.hansolo.steelseries.tools.FrameEffect.EFFECT_INNER_FRAME);
        init();
    }

    /**
     * Creates a new EtaOutputManagerUI object.
     */
    private EtaOutputManagerUI() {
        this.outputManager = null;
        initComponents();
        // this.r720Gauge.setFrameEffect(eu.hansolo.steelseries.tools.FrameEffect.EFFECT_INNER_FRAME);
        // this.totalOverflowGauge.setFrameEffect(eu.hansolo.steelseries.tools.FrameEffect.EFFECT_INNER_FRAME);
        this.r720Bar.setFrameEffect(eu.hansolo.steelseries.tools.FrameEffect.EFFECT_INNER_FRAME);
        this.totalOverflowBar.setFrameEffect(eu.hansolo.steelseries.tools.FrameEffect.EFFECT_INNER_FRAME);
        // this.add(this.gaugesResultPanel);
        this.add(this.barsResultPanel);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  etaOutput  DOCUMENT ME!
     */
    private void updateGauges(final EtaOutput etaOutput) {
        final DecimalFormat decimalFormat = new DecimalFormat("#.#");
        final int etaHydRequired = (int)etaOutput.getEtaHydRequired();
        final int etaSedRequired = (int)etaOutput.getEtaSedRequired();

        // this.etaHydGauge.setThreshold(etaOutput.getEtaHydRequired());
        // this.etaHydGauge.setToolTipText(new DecimalFormat("#.#").format(etaOutput.getEtaHydActual()));
        this.etaHydBar.setThreshold(etaOutput.getEtaHydRequired());
        this.etaHydBar.setToolTipText(decimalFormat.format(etaOutput.getEtaHydRequired()));
        this.etaHydBar.setUnitString(this.etaHydBar.getUnitString()
                    + decimalFormat.format(etaOutput.getEtaHydRequired()) + '%');

        if (etaHydRequired <= 50) {
            // this.etaHydGauge.setSections(SECTION_ETA_HYD_50);
            // this.etaHydGauge.setAreas(SECTION_ETA_HYD_50);
            this.etaHydBar.setSections(SECTION_ETA_HYD_50);
            this.etaHydBar.setAreas(SECTION_ETA_HYD_50);
        } else if (etaHydRequired <= 60) {
            // this.etaHydGauge.setSections(SECTION_ETA_HYD_60);
            // this.etaHydGauge.setAreas(SECTION_ETA_HYD_60);
            this.etaHydBar.setSections(SECTION_ETA_HYD_60);
            this.etaHydBar.setAreas(SECTION_ETA_HYD_60);
        } else {
            LOG.warn("ETA HYD not in expected range (50-60):" + etaOutput.getEtaHydRequired());
            // this.etaHydGauge.setSections(SECTION_ETA_HYD_60);
            // this.etaHydGauge.setAreas(SECTION_ETA_HYD_60);
            this.etaHydBar.setSections(SECTION_ETA_HYD_60);
            this.etaHydBar.setAreas(SECTION_ETA_HYD_60);
        }

        if (etaOutput.getEtaHydActual() >= etaOutput.getEtaHydRequired()) {
            // this.etaHydGauge.setUserLedBlinking(false);
            // this.etaHydGauge.setUserLedColor(LedColor.GREEN_LED);
            this.etaHydBar.setUserLedBlinking(false);
            this.etaHydBar.setUserLedColor(LedColor.GREEN_LED);
        } else {
            // this.etaHydGauge.setUserLedBlinking(true);
            // this.etaHydGauge.setUserLedColor(LedColor.RED_LED);
            this.etaHydBar.setUserLedBlinking(true);
            this.etaHydBar.setUserLedColor(LedColor.RED_LED);
        }

        // this.etaSedGauge.setThreshold(etaOutput.getEtaSedRequired());
        // this.etaSedGauge.setToolTipText(new DecimalFormat("#.#").format(etaOutput.getEtaSedActual()));
        this.etaSedBar.setThreshold(etaOutput.getEtaSedRequired());
        this.etaSedBar.setToolTipText(decimalFormat.format(etaOutput.getEtaSedRequired()));
        this.etaSedBar.setUnitString(this.etaSedBar.getUnitString()
                    + decimalFormat.format(etaOutput.getEtaSedRequired()) + '%');

        if (etaSedRequired <= 65) {
            // this.etaSedGauge.setSections(SECTION_ETA_SED_65);
            // this.etaSedGauge.setAreas(SECTION_ETA_SED_65);
            this.etaSedBar.setSections(SECTION_ETA_SED_65);
            this.etaSedBar.setAreas(SECTION_ETA_SED_65);
        } else if (etaSedRequired <= 75) {
            // this.etaSedGauge.setSections(SECTION_ETA_SED_75);
            // this.etaSedGauge.setAreas(SECTION_ETA_SED_75);
            this.etaSedBar.setSections(SECTION_ETA_SED_75);
            this.etaSedBar.setAreas(SECTION_ETA_SED_75);
        } else {
            LOG.warn("ETA SED not in expected range (65-75):" + etaOutput.getEtaSedRequired());
            // this.etaSedGauge.setSections(SECTION_ETA_SED_75);
            // this.etaSedGauge.setAreas(SECTION_ETA_SED_75);
            this.etaSedBar.setSections(SECTION_ETA_SED_75);
            this.etaSedBar.setAreas(SECTION_ETA_SED_75);
        }

        if (etaOutput.getEtaSedActual() >= etaOutput.getEtaSedRequired()) {
            // this.etaSedGauge.setUserLedBlinking(false);
            // this.etaSedGauge.setUserLedColor(LedColor.GREEN_LED);
            this.etaSedBar.setUserLedBlinking(false);
            this.etaSedBar.setUserLedColor(LedColor.GREEN_LED);
        } else {
            // this.etaSedGauge.setUserLedBlinking(true);
            // this.etaSedGauge.setUserLedColor(LedColor.RED_LED);
            this.etaSedBar.setUserLedBlinking(true);
            this.etaSedBar.setUserLedColor(LedColor.RED_LED);
        }

        // this.r720Gauge.setToolTipText(new DecimalFormat("#.#").format(etaOutput.getR720()));
        // this.totalOverflowGauge.setToolTipText(new DecimalFormat("#.#").format(etaOutput.getTotalOverflowVolume()));
        this.r720Bar.setToolTipText(decimalFormat.format(etaOutput.getR720()));
        this.totalOverflowBar.setToolTipText(decimalFormat.format(etaOutput.getTotalOverflowVolume()));
        if (etaOutput.getTotalOverflowVolume() > 0) {
            // this.totalOverflowGauge.setMaxValue(etaOutput.getTotalOverflowVolume() * 1.5);
            this.totalOverflowBar.setMaxValue(etaOutput.getTotalOverflowVolume() * 1.5);
        } else {
            LOG.warn("total overflow volume not computed: " + etaOutput.getTotalOverflowVolume());
            // this.totalOverflowGauge.setMaxValue(100);
            this.totalOverflowBar.setMaxValue(100);
        }

        // this.etaHydGauge.setValueAnimated(etaOutput.getEtaHydActual());
        // this.r720Gauge.setValueAnimated(etaOutput.getR720());
        // this.totalOverflowGauge.setValueAnimated(etaOutput.getTotalOverflowVolume());
        // this.etaSedGauge.setValueAnimated(etaOutput.getEtaSedActual());
        this.etaHydBar.setValue(etaOutput.getEtaHydActual());
        this.r720Bar.setValue(etaOutput.getR720());
        this.totalOverflowBar.setValue(etaOutput.getTotalOverflowVolume());
        this.etaSedBar.setValue(etaOutput.getEtaSedActual());
    }

    /**
     * DOCUMENT ME!
     */
    private void init() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("initilaising EtaOutputManagerUI V2");
        }

        if ((this.outputManager.getCidsBeans() != null) && !this.outputManager.getCidsBeans().isEmpty()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("showing and comparing " + this.outputManager.getCidsBeans().size() + " eta results");
            }

            final EfficiencyRatesComparisionPanel etaOutputComparisionPanel = new EfficiencyRatesComparisionPanel();
            final TotalOverflowComparisionPanel totalOverflowComparisionPanel = new TotalOverflowComparisionPanel();

            final List<String> etaRunNames = new ArrayList<String>(this.outputManager.getCidsBeans().size());
            final List<EtaOutput> etaOutputs = new ArrayList<EtaOutput>(this.outputManager.getCidsBeans().size());

            for (final CidsBean modelOutputBean : this.outputManager.getCidsBeans()) {
                try {
                    etaOutputs.add(this.getEtaOutput(modelOutputBean));
                    etaRunNames.add((String)modelOutputBean.getProperty("name"));
                } catch (Exception e) {
                    LOG.error("could not process model output '" + modelOutputBean + "': " + e.getMessage(), e);
                }
            }

            if (EventQueue.isDispatchThread()) {
                etaOutputComparisionPanel.setEtaOutputs(etaRunNames, etaOutputs);
                totalOverflowComparisionPanel.setEtaOutputs(etaRunNames, etaOutputs);

                final JPanel contentPanel = new JPanel(new GridLayout(2, 0, 10, 0));
                contentPanel.setOpaque(false);
                contentPanel.add(etaOutputComparisionPanel);
                contentPanel.add(totalOverflowComparisionPanel);
                this.removeAll();
                this.add(contentPanel);
            } else {
                EventQueue.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            etaOutputComparisionPanel.setEtaOutputs(etaRunNames, etaOutputs);
                            totalOverflowComparisionPanel.setEtaOutputs(etaRunNames, etaOutputs);

                            final JPanel contentPanel = new JPanel(new GridLayout(2, 0, 10, 0));
                            contentPanel.setOpaque(false);
                            contentPanel.add(etaOutputComparisionPanel);
                            contentPanel.add(totalOverflowComparisionPanel);
                            removeAll();
                            add(contentPanel);
                        }
                    });
            }
        } else if (this.outputManager.getCidsBean() != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("showing only one eta result");
            }
            final EtaOutput etaOutput = this.getEtaOutput();

            if (EventQueue.isDispatchThread()) {
                this.removeAll();
                // this.add(this.gaugesResultPanel);
                this.add(this.barsResultPanel);
                this.updateGauges(etaOutput);
            } else {
                EventQueue.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            removeAll();
                            // add(gaugesResultPanel);
                            add(barsResultPanel);
                            updateGauges(etaOutput);
                        }
                    });
            }

//            this.fld_etaHydActual.setText(String.valueOf(etaOutput.getEtaHydActual()));
//            this.fld_etaHydRequired.setText(String.valueOf(etaOutput.getEtaHydRequired()));
//            this.fld_etaSedActual.setText(String.valueOf(etaOutput.getEtaSedActual()));
//            this.fld_etaSedRequired.setText(String.valueOf(etaOutput.getEtaSedRequired()));
//            this.fld_720.setText(String.valueOf(etaOutput.getR720()));
//            this.fld_totalOverflowVolume.setText(String.valueOf(etaOutput.getTotalOverflowVolume()));
//            this.add(this.singleResultPanel);

        } else {
            LOG.error("error initialising UI: no cidsbean(s) set");
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public EtaOutput getEtaOutput() {
        try {
            return this.outputManager.getUR();
        } catch (Exception ex) {
            LOG.error("could not load eta output", ex);
        }

        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   modelOutputBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    public EtaOutput getEtaOutput(final CidsBean modelOutputBean) throws IOException {
        final String json = (String)modelOutputBean.getProperty("ur"); // NOI18N
        final ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(json, EtaOutput.class);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        singleResultPanel = new javax.swing.JPanel();
        lbl_r720 = new javax.swing.JLabel();
        lbl_etaHydRequired = new javax.swing.JLabel();
        lbl_etaSedRequired = new javax.swing.JLabel();
        lbl_etaHydActual = new javax.swing.JLabel();
        lbl_etaSedActual = new javax.swing.JLabel();
        lbl_totalOverflowVolume = new javax.swing.JLabel();
        fld_720 = new javax.swing.JTextField();
        fld_etaHydRequired = new javax.swing.JTextField();
        fld_etaSedRequired = new javax.swing.JTextField();
        fld_etaHydActual = new javax.swing.JTextField();
        fld_etaSedActual = new javax.swing.JTextField();
        fld_totalOverflowVolume = new javax.swing.JTextField();
        barsResultPanel = new javax.swing.JPanel();
        spacerOne = new javax.swing.JPanel();
        lblEtaHydBar = new javax.swing.JLabel();
        lblEtaSedBar = new javax.swing.JLabel();
        etaHydBar = new eu.hansolo.steelseries.gauges.LinearBargraph();
        etaSedBar = new eu.hansolo.steelseries.gauges.LinearBargraph();
        lblR720Bar = new javax.swing.JLabel();
        lblTotalOverflowBar = new javax.swing.JLabel();
        r720Bar = new eu.hansolo.steelseries.gauges.Linear();
        totalOverflowBar = new eu.hansolo.steelseries.gauges.Linear();
        spacerTwo = new javax.swing.JPanel();
        gaugesResultPanel = new javax.swing.JPanel();
        lblEtaHyd = new javax.swing.JLabel();
        lblEtaSed = new javax.swing.JLabel();
        etaHydGauge = new eu.hansolo.steelseries.gauges.Radial();
        etaSedGauge = new eu.hansolo.steelseries.gauges.Radial();
        lblR720 = new javax.swing.JLabel();
        lblTotalOverflow = new javax.swing.JLabel();
        r720Gauge = new eu.hansolo.steelseries.gauges.Radial2Top();
        totalOverflowGauge = new eu.hansolo.steelseries.gauges.Radial2Top();

        singleResultPanel.setOpaque(false);
        singleResultPanel.setLayout(new java.awt.GridBagLayout());

        lbl_r720.setText(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.lbl_r720.text"));        // NOI18N
        lbl_r720.setToolTipText(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.lbl_r720.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        singleResultPanel.add(lbl_r720, gridBagConstraints);

        lbl_etaHydRequired.setText(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.lbl_etaHydRequired.text"));        // NOI18N
        lbl_etaHydRequired.setToolTipText(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.lbl_etaHydRequired.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        singleResultPanel.add(lbl_etaHydRequired, gridBagConstraints);

        lbl_etaSedRequired.setText(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.lbl_etaSedRequired.text"));        // NOI18N
        lbl_etaSedRequired.setToolTipText(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.lbl_etaSedRequired.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        singleResultPanel.add(lbl_etaSedRequired, gridBagConstraints);

        lbl_etaHydActual.setText(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.lbl_etaHydActual.text"));        // NOI18N
        lbl_etaHydActual.setToolTipText(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.lbl_etaHydActual.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        singleResultPanel.add(lbl_etaHydActual, gridBagConstraints);

        lbl_etaSedActual.setText(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.lbl_etaSedActual.text"));        // NOI18N
        lbl_etaSedActual.setToolTipText(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.lbl_etaSedActual.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        singleResultPanel.add(lbl_etaSedActual, gridBagConstraints);

        lbl_totalOverflowVolume.setText(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.lbl_totalOverflowVolume.text"));        // NOI18N
        lbl_totalOverflowVolume.setToolTipText(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.lbl_totalOverflowVolume.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        singleResultPanel.add(lbl_totalOverflowVolume, gridBagConstraints);

        fld_720.setColumns(4);
        fld_720.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        singleResultPanel.add(fld_720, gridBagConstraints);

        fld_etaHydRequired.setColumns(4);
        fld_etaHydRequired.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        singleResultPanel.add(fld_etaHydRequired, gridBagConstraints);

        fld_etaSedRequired.setColumns(4);
        fld_etaSedRequired.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        singleResultPanel.add(fld_etaSedRequired, gridBagConstraints);

        fld_etaHydActual.setColumns(4);
        fld_etaHydActual.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        singleResultPanel.add(fld_etaHydActual, gridBagConstraints);

        fld_etaSedActual.setColumns(4);
        fld_etaSedActual.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        singleResultPanel.add(fld_etaSedActual, gridBagConstraints);

        fld_totalOverflowVolume.setColumns(4);
        fld_totalOverflowVolume.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        singleResultPanel.add(fld_totalOverflowVolume, gridBagConstraints);

        barsResultPanel.setAlignmentX(0.0F);
        barsResultPanel.setAlignmentY(0.0F);
        barsResultPanel.setOpaque(false);
        barsResultPanel.setPreferredSize(new java.awt.Dimension(800, 600));
        barsResultPanel.setLayout(new java.awt.GridBagLayout());

        spacerOne.setOpaque(false);

        final javax.swing.GroupLayout spacerOneLayout = new javax.swing.GroupLayout(spacerOne);
        spacerOne.setLayout(spacerOneLayout);
        spacerOneLayout.setHorizontalGroup(
            spacerOneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                800,
                Short.MAX_VALUE));
        spacerOneLayout.setVerticalGroup(
            spacerOneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                70,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        barsResultPanel.add(spacerOne, gridBagConstraints);

        lblEtaHydBar.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        lblEtaHydBar.setForeground(new java.awt.Color(51, 51, 51));
        lblEtaHydBar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblEtaHydBar.setText(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.lblEtaHydBar.text"));         // NOI18N
        lblEtaHydBar.setAlignmentY(0.0F);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 20, 20, 20);
        barsResultPanel.add(lblEtaHydBar, gridBagConstraints);

        lblEtaSedBar.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        lblEtaSedBar.setForeground(new java.awt.Color(51, 51, 51));
        lblEtaSedBar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblEtaSedBar.setText(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.lblEtaSedBar.text"));         // NOI18N
        lblEtaSedBar.setAlignmentY(0.0F);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 20, 20, 20);
        barsResultPanel.add(lblEtaSedBar, gridBagConstraints);

        etaHydBar.setAlignmentX(0.0F);
        etaHydBar.setAlignmentY(0.0F);
        etaHydBar.setAreasVisible(true);
        etaHydBar.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.GLOSSY_METAL);
        etaHydBar.setFrameEffect(eu.hansolo.steelseries.tools.FrameEffect.EFFECT_INNER_FRAME);
        etaHydBar.setLabelNumberFormat(eu.hansolo.steelseries.tools.NumberFormat.STANDARD);
        etaHydBar.setLcdDecimals(2);
        etaHydBar.setLcdInfoString(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.etaHydBar.lcdInfoString"));             // NOI18N
        etaHydBar.setLcdUnitString(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.etaHydBar.lcdUnitString"));             // NOI18N
        etaHydBar.setLcdUnitStringVisible(true);
        etaHydBar.setLedVisible(false);
        etaHydBar.setMaxNoOfMajorTicks(5);
        etaHydBar.setMaximumSize(new java.awt.Dimension(400, 400));
        etaHydBar.setMinimumSize(new java.awt.Dimension(350, 150));
        etaHydBar.setRtzTimeBackToZero(0L);
        etaHydBar.setRtzTimeToValue(0L);
        etaHydBar.setSectionsVisible(true);
        etaHydBar.setStdTimeToValue(0L);
        etaHydBar.setThreshold(0.0);
        etaHydBar.setThresholdType(eu.hansolo.steelseries.tools.ThresholdType.ARROW);
        etaHydBar.setThresholdVisible(true);
        etaHydBar.setTicklabelsVisible(false);
        etaHydBar.setTitle(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.etaHydBar.title"));                     // NOI18N
        etaHydBar.setTitleAndUnitFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        etaHydBar.setTitleAndUnitFontEnabled(true);
        etaHydBar.setUnitString(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.etaHydBar.unitString"));                // NOI18N
        etaHydBar.setUserLedVisible(true);

        final javax.swing.GroupLayout etaHydBarLayout = new javax.swing.GroupLayout(etaHydBar);
        etaHydBar.setLayout(etaHydBarLayout);
        etaHydBarLayout.setHorizontalGroup(
            etaHydBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                360,
                Short.MAX_VALUE));
        etaHydBarLayout.setVerticalGroup(
            etaHydBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                150,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 20, 20);
        barsResultPanel.add(etaHydBar, gridBagConstraints);

        etaSedBar.setAlignmentX(0.0F);
        etaSedBar.setAlignmentY(0.0F);
        etaSedBar.setAreasVisible(true);
        etaSedBar.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.GLOSSY_METAL);
        etaSedBar.setFrameEffect(eu.hansolo.steelseries.tools.FrameEffect.EFFECT_INNER_FRAME);
        etaSedBar.setLabelNumberFormat(eu.hansolo.steelseries.tools.NumberFormat.STANDARD);
        etaSedBar.setLcdDecimals(2);
        etaSedBar.setLcdInfoString(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.etaSedBar.lcdInfoString"));             // NOI18N
        etaSedBar.setLcdThreshold(5.0);
        etaSedBar.setLcdUnitString(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.etaSedBar.lcdUnitString"));             // NOI18N
        etaSedBar.setLcdUnitStringVisible(true);
        etaSedBar.setLedVisible(false);
        etaSedBar.setMaxNoOfMajorTicks(5);
        etaSedBar.setMaximumSize(new java.awt.Dimension(400, 400));
        etaSedBar.setMinimumSize(new java.awt.Dimension(350, 150));
        etaSedBar.setRtzTimeBackToZero(0L);
        etaSedBar.setRtzTimeToValue(0L);
        etaSedBar.setSectionsVisible(true);
        etaSedBar.setStdTimeToValue(0L);
        etaSedBar.setThreshold(0.0);
        etaSedBar.setThresholdType(eu.hansolo.steelseries.tools.ThresholdType.ARROW);
        etaSedBar.setThresholdVisible(true);
        etaSedBar.setTicklabelsVisible(false);
        etaSedBar.setTitle(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.etaSedBar.title"));                     // NOI18N
        etaSedBar.setTitleAndUnitFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        etaSedBar.setTitleAndUnitFontEnabled(true);
        etaSedBar.setUnitString(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.etaSedBar.unitString"));                // NOI18N
        etaSedBar.setUserLedVisible(true);

        final javax.swing.GroupLayout etaSedBarLayout = new javax.swing.GroupLayout(etaSedBar);
        etaSedBar.setLayout(etaSedBarLayout);
        etaSedBarLayout.setHorizontalGroup(
            etaSedBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                360,
                Short.MAX_VALUE));
        etaSedBarLayout.setVerticalGroup(
            etaSedBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                150,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 20, 20);
        barsResultPanel.add(etaSedBar, gridBagConstraints);

        lblR720Bar.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        lblR720Bar.setForeground(new java.awt.Color(51, 51, 51));
        lblR720Bar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblR720Bar.setText(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.lblR720Bar.text"));         // NOI18N
        lblR720Bar.setAlignmentY(0.0F);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 20, 20, 20);
        barsResultPanel.add(lblR720Bar, gridBagConstraints);

        lblTotalOverflowBar.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        lblTotalOverflowBar.setForeground(new java.awt.Color(51, 51, 51));
        lblTotalOverflowBar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTotalOverflowBar.setText(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.lblTotalOverflowBar.text"));         // NOI18N
        lblTotalOverflowBar.setAlignmentY(0.0F);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 20, 20, 20);
        barsResultPanel.add(lblTotalOverflowBar, gridBagConstraints);

        r720Bar.setToolTipText(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.r720Bar.toolTipText"));   // NOI18N
        r720Bar.setAlignmentX(0.0F);
        r720Bar.setAlignmentY(0.0F);
        r720Bar.setAreasVisible(true);
        r720Bar.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.SHINY_METAL);
        r720Bar.setLabelNumberFormat(eu.hansolo.steelseries.tools.NumberFormat.STANDARD);
        r720Bar.setLcdInfoString(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.r720Bar.lcdInfoString")); // NOI18N
        r720Bar.setLcdUnitString(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.r720Bar.lcdUnitString")); // NOI18N
        r720Bar.setLcdUnitStringVisible(true);
        r720Bar.setLedVisible(false);
        r720Bar.setMaxValue(60.0);
        r720Bar.setMaximumSize(new java.awt.Dimension(300, 300));
        r720Bar.setMinimumSize(new java.awt.Dimension(350, 150));
        r720Bar.setRtzTimeBackToZero(0L);
        r720Bar.setRtzTimeToValue(0L);
        r720Bar.setStdTimeToValue(0L);
        r720Bar.setThreshold(0.0);
        r720Bar.setTitle(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.r720Bar.title"));         // NOI18N
        r720Bar.setTitleAndUnitFontEnabled(true);
        r720Bar.setTrackSection(30.0);
        r720Bar.setUnitString(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.r720Bar.unitString"));    // NOI18N
        r720Bar.setUnitStringVisible(false);

        final javax.swing.GroupLayout r720BarLayout = new javax.swing.GroupLayout(r720Bar);
        r720Bar.setLayout(r720BarLayout);
        r720BarLayout.setHorizontalGroup(
            r720BarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                360,
                Short.MAX_VALUE));
        r720BarLayout.setVerticalGroup(
            r720BarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                150,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 20, 20);
        barsResultPanel.add(r720Bar, gridBagConstraints);

        totalOverflowBar.setToolTipText(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.totalOverflowBar.toolTipText"));   // NOI18N
        totalOverflowBar.setAlignmentX(0.0F);
        totalOverflowBar.setAlignmentY(0.0F);
        totalOverflowBar.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.SHINY_METAL);
        totalOverflowBar.setLabelNumberFormat(eu.hansolo.steelseries.tools.NumberFormat.STANDARD);
        totalOverflowBar.setLcdInfoString(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.totalOverflowBar.lcdInfoString")); // NOI18N
        totalOverflowBar.setLcdUnitString(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.totalOverflowBar.lcdUnitString")); // NOI18N
        totalOverflowBar.setLcdUnitStringVisible(true);
        totalOverflowBar.setLedVisible(false);
        totalOverflowBar.setMaxValue(60.0);
        totalOverflowBar.setMaximumSize(new java.awt.Dimension(300, 300));
        totalOverflowBar.setMinimumSize(new java.awt.Dimension(350, 150));
        totalOverflowBar.setRtzTimeBackToZero(0L);
        totalOverflowBar.setRtzTimeToValue(0L);
        totalOverflowBar.setStdTimeToValue(0L);
        totalOverflowBar.setThreshold(0.0);
        totalOverflowBar.setTitle(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.totalOverflowBar.title"));         // NOI18N
        totalOverflowBar.setTitleAndUnitFontEnabled(true);
        totalOverflowBar.setTrackSection(30.0);
        totalOverflowBar.setTransparentSectionsEnabled(true);
        totalOverflowBar.setUnitString(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.totalOverflowBar.unitString"));    // NOI18N
        totalOverflowBar.setUnitStringVisible(false);

        final javax.swing.GroupLayout totalOverflowBarLayout = new javax.swing.GroupLayout(totalOverflowBar);
        totalOverflowBar.setLayout(totalOverflowBarLayout);
        totalOverflowBarLayout.setHorizontalGroup(
            totalOverflowBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                360,
                Short.MAX_VALUE));
        totalOverflowBarLayout.setVerticalGroup(
            totalOverflowBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                150,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 20, 20);
        barsResultPanel.add(totalOverflowBar, gridBagConstraints);

        spacerTwo.setOpaque(false);

        final javax.swing.GroupLayout spacerTwoLayout = new javax.swing.GroupLayout(spacerTwo);
        spacerTwo.setLayout(spacerTwoLayout);
        spacerTwoLayout.setHorizontalGroup(
            spacerTwoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                800,
                Short.MAX_VALUE));
        spacerTwoLayout.setVerticalGroup(
            spacerTwoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                70,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        barsResultPanel.add(spacerTwo, gridBagConstraints);

        gaugesResultPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(50, 50, 0, 50));
        gaugesResultPanel.setOpaque(false);
        gaugesResultPanel.setPreferredSize(new java.awt.Dimension(800, 600));
        gaugesResultPanel.setLayout(new java.awt.GridBagLayout());

        lblEtaHyd.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        lblEtaHyd.setForeground(new java.awt.Color(51, 51, 51));
        lblEtaHyd.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblEtaHyd.setText(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.lblEtaHyd.text"));         // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 40);
        gaugesResultPanel.add(lblEtaHyd, gridBagConstraints);

        lblEtaSed.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        lblEtaSed.setForeground(new java.awt.Color(51, 51, 51));
        lblEtaSed.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblEtaSed.setText(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.lblEtaSed.text"));         // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.insets = new java.awt.Insets(10, 40, 10, 10);
        gaugesResultPanel.add(lblEtaSed, gridBagConstraints);

        etaHydGauge.setAreasVisible(true);
        etaHydGauge.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.GLOSSY_METAL);
        etaHydGauge.setFrameEffect(eu.hansolo.steelseries.tools.FrameEffect.EFFECT_INNER_FRAME);
        etaHydGauge.setLcdVisible(false);
        etaHydGauge.setLedVisible(false);
        etaHydGauge.setMaximumSize(new java.awt.Dimension(400, 400));
        etaHydGauge.setSectionsVisible(true);
        etaHydGauge.setThreshold(0.0);
        etaHydGauge.setThresholdType(eu.hansolo.steelseries.tools.ThresholdType.ARROW);
        etaHydGauge.setThresholdVisible(true);
        etaHydGauge.setTitle(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.etaHydGauge.title")); // NOI18N
        etaHydGauge.setUnitString(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.etaHydGauge.unit"));  // NOI18N
        etaHydGauge.setUserLedVisible(true);

        final javax.swing.GroupLayout etaHydGaugeLayout = new javax.swing.GroupLayout(etaHydGauge);
        etaHydGauge.setLayout(etaHydGaugeLayout);
        etaHydGaugeLayout.setHorizontalGroup(
            etaHydGaugeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                207,
                Short.MAX_VALUE));
        etaHydGaugeLayout.setVerticalGroup(
            etaHydGaugeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                207,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.75;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 40);
        gaugesResultPanel.add(etaHydGauge, gridBagConstraints);

        etaSedGauge.setAreasVisible(true);
        etaSedGauge.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.GLOSSY_METAL);
        etaSedGauge.setFrameEffect(eu.hansolo.steelseries.tools.FrameEffect.EFFECT_INNER_FRAME);
        etaSedGauge.setLcdVisible(false);
        etaSedGauge.setLedVisible(false);
        etaSedGauge.setMaximumSize(new java.awt.Dimension(400, 400));
        etaSedGauge.setName(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.etaSedGauge.name"));       // NOI18N
        etaSedGauge.setSectionsVisible(true);
        etaSedGauge.setThreshold(0.0);
        etaSedGauge.setThresholdType(eu.hansolo.steelseries.tools.ThresholdType.ARROW);
        etaSedGauge.setThresholdVisible(true);
        etaSedGauge.setTitle(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.etaSedGauge.title"));      // NOI18N
        etaSedGauge.setUnitString(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.etaSedGauge.unitString")); // NOI18N
        etaSedGauge.setUserLedVisible(true);

        final javax.swing.GroupLayout etaSedGaugeLayout = new javax.swing.GroupLayout(etaSedGauge);
        etaSedGauge.setLayout(etaSedGaugeLayout);
        etaSedGaugeLayout.setHorizontalGroup(
            etaSedGaugeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                207,
                Short.MAX_VALUE));
        etaSedGaugeLayout.setVerticalGroup(
            etaSedGaugeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                207,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.75;
        gridBagConstraints.insets = new java.awt.Insets(10, 40, 10, 10);
        gaugesResultPanel.add(etaSedGauge, gridBagConstraints);

        lblR720.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        lblR720.setForeground(new java.awt.Color(51, 51, 51));
        lblR720.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblR720.setText(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.lblR720.text"));         // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.insets = new java.awt.Insets(40, 10, 10, 40);
        gaugesResultPanel.add(lblR720, gridBagConstraints);

        lblTotalOverflow.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        lblTotalOverflow.setForeground(new java.awt.Color(51, 51, 51));
        lblTotalOverflow.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTotalOverflow.setText(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.lblTotalOverflow.text"));         // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.insets = new java.awt.Insets(40, 40, 10, 10);
        gaugesResultPanel.add(lblTotalOverflow, gridBagConstraints);

        r720Gauge.setAreasVisible(true);
        r720Gauge.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.SHINY_METAL);
        r720Gauge.setGaugeType(eu.hansolo.steelseries.tools.GaugeType.TYPE1);
        r720Gauge.setLedVisible(false);
        r720Gauge.setMaxValue(60.0);
        r720Gauge.setMaximumSize(new java.awt.Dimension(300, 300));
        r720Gauge.setTitle(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.r720Gauge.title")); // NOI18N
        r720Gauge.setTrackSection(30.0);
        r720Gauge.setTrackVisible(true);
        r720Gauge.setUnitString(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.r720Gauge.unit"));  // NOI18N

        final javax.swing.GroupLayout r720GaugeLayout = new javax.swing.GroupLayout(r720Gauge);
        r720Gauge.setLayout(r720GaugeLayout);
        r720GaugeLayout.setHorizontalGroup(
            r720GaugeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                202,
                Short.MAX_VALUE));
        r720GaugeLayout.setVerticalGroup(
            r720GaugeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                202,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 0.25;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 40);
        gaugesResultPanel.add(r720Gauge, gridBagConstraints);

        totalOverflowGauge.setToolTipText(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.totalOverflowGauge.toolTipText")); // NOI18N
        totalOverflowGauge.setAreasVisible(true);
        totalOverflowGauge.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.SHINY_METAL);
        totalOverflowGauge.setGaugeType(eu.hansolo.steelseries.tools.GaugeType.TYPE1);
        totalOverflowGauge.setLedVisible(false);
        totalOverflowGauge.setMaxValue(60.0);
        totalOverflowGauge.setMaximumSize(new java.awt.Dimension(300, 300));
        totalOverflowGauge.setTitle(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.totalOverflowGauge.title"));       // NOI18N
        totalOverflowGauge.setTrackSection(30.0);
        totalOverflowGauge.setUnitString(org.openide.util.NbBundle.getMessage(
                EtaOutputManagerUI.class,
                "EtaOutputManagerUI.totalOverflowGauge.unitString"));  // NOI18N

        final javax.swing.GroupLayout totalOverflowGaugeLayout = new javax.swing.GroupLayout(totalOverflowGauge);
        totalOverflowGauge.setLayout(totalOverflowGaugeLayout);
        totalOverflowGaugeLayout.setHorizontalGroup(
            totalOverflowGaugeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                202,
                Short.MAX_VALUE));
        totalOverflowGaugeLayout.setVerticalGroup(
            totalOverflowGaugeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                202,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 0.25;
        gridBagConstraints.insets = new java.awt.Insets(10, 40, 0, 10);
        gaugesResultPanel.add(totalOverflowGauge, gridBagConstraints);

        setOpaque(false);
        setLayout(new java.awt.GridLayout(1, 0, 50, 50));
    } // </editor-fold>//GEN-END:initComponents

    @Override
    public String getTitle() {
        if ((this.outputManager.getCidsBeans() != null) && !this.outputManager.getCidsBeans().isEmpty()) {
            return org.openide.util.NbBundle.getMessage(
                        EtaOutputManagerUI.class,
                        "EtaOutputManagerUI.title.aggregated")
                        .replaceAll("%n", String.valueOf(this.outputManager.getCidsBeans().size()));
        } else if (this.outputManager.getCidsBean() != null) {
            return this.outputManager.getCidsBean().getProperty("name").toString();
        }

        return "";
    }

    @Override
    public void setTitle(final String title) {
        LOG.warn("set title '" + title + "' not supported by this UI");
    }
}
