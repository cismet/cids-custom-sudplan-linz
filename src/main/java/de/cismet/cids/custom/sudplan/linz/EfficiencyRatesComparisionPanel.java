/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.linz;

import org.apache.log4j.Logger;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import org.openide.util.NbBundle;

import java.awt.Color;

import java.text.DecimalFormat;

import java.util.List;

/**
 * DOCUMENT ME!
 *
 * @author   pascal
 * @version  $Revision$, $Date$
 */
public class EfficiencyRatesComparisionPanel extends javax.swing.JPanel {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(EfficiencyRatesComparisionPanel.class);

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form EfficiencyRatesComparisionPanel.
     */
    public EfficiencyRatesComparisionPanel() {
        initComponents();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  etaRunNames  DOCUMENT ME!
     * @param  etaOutputs   DOCUMENT ME!
     */
    public void setEtaOutputs(final List<String> etaRunNames, final List<EtaOutput> etaOutputs) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("setEtaOutputs: " + etaRunNames.size());
        }
        final String etaHydRequired = NbBundle.getMessage(
                EfficiencyRatesComparisionPanel.class,
                "EfficiencyRatesComparisionPanel.chart.etaHydRequired");
        final String etaHydActual = NbBundle.getMessage(
                EfficiencyRatesComparisionPanel.class,
                "EfficiencyRatesComparisionPanel.chart.etaHydActual");
        final String etaSedRequired = NbBundle.getMessage(
                EfficiencyRatesComparisionPanel.class,
                "EfficiencyRatesComparisionPanel.chart.etaSedRequired");
        final String etaSedActual = NbBundle.getMessage(
                EfficiencyRatesComparisionPanel.class,
                "EfficiencyRatesComparisionPanel.chart.etaSedActual");
        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        int i = 0;
        for (final String etaRunName : etaRunNames) {
            final EtaOutput etaOutput = etaOutputs.get(i);
            dataset.addValue(etaOutput.getEtaHydRequired(), etaHydRequired, etaRunName);
            dataset.addValue(etaOutput.getEtaHydActual(), etaHydActual, etaRunName);
            dataset.addValue(etaOutput.getEtaSedRequired(), etaSedRequired, etaRunName);
            dataset.addValue(etaOutput.getEtaSedActual(), etaSedActual, etaRunName);
            i++;
        }

        final JFreeChart chart = ChartFactory.createBarChart(
                NbBundle.getMessage(
                    EfficiencyRatesComparisionPanel.class,
                    "EfficiencyRatesComparisionPanel.chart.title"),
                NbBundle.getMessage(
                    EfficiencyRatesComparisionPanel.class,
                    "EfficiencyRatesComparisionPanel.chart.domain"),
                NbBundle.getMessage(
                    EfficiencyRatesComparisionPanel.class,
                    "EfficiencyRatesComparisionPanel.chart.range"),
                dataset,                  // data
                PlotOrientation.VERTICAL, // orientation
                true,                     // include legend
                true,                     // tooltips?
                false                     // URLs?
                );

        // set the background color for the chart...
        // chart.setBackgroundPaint(Color.white);
        chart.setBackgroundPaint(new Color(255, 255, 255, 0));
        chart.setBackgroundImageAlpha(0.0f);

        // get a reference to the plot for further customisation...
        final CategoryPlot plot = chart.getCategoryPlot();
        // plot.setBackgroundPaint(Color.lightGray);
        plot.setBackgroundPaint(new Color(228, 228, 197));
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);

        // set the range axis to display integers only...
        final NumberAxis rangeAxis = (NumberAxis)plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        // disable bar outlines...
        final BarRenderer renderer = (BarRenderer)plot.getRenderer();
        renderer.setDrawBarOutline(false);
        renderer.setItemMargin(0.0);

        // set up gradient paints for series...
// final GradientPaint gp0 = new GradientPaint(
// 0.0f,
// 0.0f,
// new Color(27, 103, 107),
// 0.0f,
// 0.0f,
// new Color(81, 149, 72));
//
// final GradientPaint gp1 = new GradientPaint(
// 0.0f,
// 0.0f,
// new Color(11, 16, 140),
// 0.0f,
// 0.0f,
// new Color(14, 78, 173));
//
// final GradientPaint gp2 = new GradientPaint(
// 0.0f,
// 0.0f,
// new Color(136, 196, 37),
// 0.0f,
// 0.0f,
// new Color(190, 242, 2));
//
// final GradientPaint gp3 = new GradientPaint(
// 0.0f,
// 0.0f,
// new Color(14, 78, 173),
// 0.0f,
// 0.0f,
// new Color(16, 127, 201));
        renderer.setSeriesPaint(0, new Color(27, 103, 107));
        renderer.setSeriesPaint(1, new Color(11, 16, 140));
        renderer.setSeriesPaint(2, new Color(136, 196, 37));
        renderer.setSeriesPaint(3, new Color(14, 78, 173));

        final CategoryItemRenderer categoryRenderer = (CategoryItemRenderer)plot.getRenderer();
        final CategoryItemLabelGenerator generator = new StandardCategoryItemLabelGenerator(
                "{2}",
                new DecimalFormat("0.0"));
        categoryRenderer.setBaseItemLabelGenerator(generator);
        categoryRenderer.setBaseItemLabelsVisible(true);

        final CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.STANDARD);
//        domainAxis.setCategoryLabelPositions(
//        CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0));
        // OPTIONAL CUSTOMISATION COMPLETED.

        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setOpaque(false);
        this.add(chartPanel);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        setOpaque(false);
        setLayout(new java.awt.GridLayout(1, 1));
    } // </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
