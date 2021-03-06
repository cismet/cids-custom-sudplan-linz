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
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import org.openide.util.NbBundle;

import java.awt.Color;
import java.awt.GradientPaint;

import java.util.List;

/**
 * DOCUMENT ME!
 *
 * @author   pascal
 * @version  $Revision$, $Date$
 */
public class TotalOverflowComparisionPanel extends javax.swing.JPanel {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(TotalOverflowComparisionPanel.class);

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form EfficiencyRatesComparisionPanel.
     */
    public TotalOverflowComparisionPanel() {
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
        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        int i = 0;
        for (final String etaRunName : etaRunNames) {
            final EtaOutput etaOutput = etaOutputs.get(i);

            final String totalOverflowVolume = NbBundle.getMessage(
                    TotalOverflowComparisionPanel.class,
                    "TotalOverflowComparisionPanel.chart.totalOverflowVolume");

            dataset.addValue(etaOutput.getTotalOverflowVolume(), totalOverflowVolume, etaRunName);
            i++;
        }

        final JFreeChart chart = ChartFactory.createBarChart(
                NbBundle.getMessage(TotalOverflowComparisionPanel.class, "TotalOverflowComparisionPanel.chart.title"),
                NbBundle.getMessage(TotalOverflowComparisionPanel.class, "TotalOverflowComparisionPanel.chart.domain"),
                NbBundle.getMessage(TotalOverflowComparisionPanel.class, "TotalOverflowComparisionPanel.chart.range"),
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
        plot.setBackgroundPaint(new Color(228, 228, 197));
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);

        // set the range axis to display integers only...
        final NumberAxis rangeAxis = (NumberAxis)plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        // disable bar outlines...
        final BarRenderer renderer = (BarRenderer)plot.getRenderer();
        renderer.setDrawBarOutline(false);

        // set up gradient paints for series...
// final GradientPaint gp0 = new GradientPaint(
// 0.0f,
// 0.0f,
// Color.blue,
// 0.0f,
// 0.0f,
// Color.lightGray);

        // renderer.setSeriesPaint(0, new Color(128, 26, 12));
        renderer.setSeriesPaint(0, new Color(142, 91, 62));

        final CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.STANDARD);
//        domainAxis.setCategoryLabelPositions(
//        CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0));
        // OPTIONAL CUSTOMISATION COMPLETED.

        final ChartPanel chartPanel = new ChartPanel(chart);
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
