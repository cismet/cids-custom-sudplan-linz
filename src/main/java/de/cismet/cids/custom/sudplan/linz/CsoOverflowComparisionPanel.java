/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.linz;

import org.apache.log4j.BasicConfigurator;
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

import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

import java.awt.Color;
import java.awt.GradientPaint;

import java.text.DecimalFormat;

import java.util.Arrays;
import java.util.Collection;

import javax.swing.JFrame;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   pascal
 * @version  $Revision$, $Date$
 */
public class CsoOverflowComparisionPanel extends javax.swing.JPanel {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(CsoOverflowComparisionPanel.class);

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form EfficiencyRatesComparisionPanel.
     */
    public CsoOverflowComparisionPanel() {
        initComponents();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  swmmResults  DOCUMENT ME!
     */
    public void setSwmmResults(final Collection<CidsBean> swmmResults) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("setSwmmResults for Cso Overflow Comparision: " + swmmResults.size());
        }

        if (swmmResults.isEmpty()) {
            LOG.warn("empty SWMM results list");
            return;
        }

        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (final CidsBean swmmResultBean : swmmResults) {
            final String name = (String)swmmResultBean.getProperty("name");

            final String overflowVolumeLabel = NbBundle.getMessage(
                    CsoOverflowComparisionPanel.class,
                    "CsoOverflowComparisionPanel.chart.overflowVolume");

            final Object overflowVolume = swmmResultBean.getProperty("overflow_volume");
            if (overflowVolume != null) {
                dataset.addValue((Float)overflowVolume, overflowVolumeLabel, name);
            } else {
                dataset.addValue(0, overflowVolumeLabel, name);
            }
        }

        final JFreeChart chart = ChartFactory.createBarChart(
                NbBundle.getMessage(CsoOverflowComparisionPanel.class, "CsoOverflowComparisionPanel.chart.title"),
                NbBundle.getMessage(CsoOverflowComparisionPanel.class, "CsoOverflowComparisionPanel.chart.domain"),
                NbBundle.getMessage(CsoOverflowComparisionPanel.class, "CsoOverflowComparisionPanel.chart.range"),
                dataset,                  // data
                PlotOrientation.VERTICAL, // orientation
                false,                    // include legend
                false,                    // tooltips?
                false                     // URLs?
                );

        chart.setBackgroundPaint(new Color(255, 255, 255));
        // chart.setBackgroundImageAlpha(0.0f);

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
// new Color(128, 26, 12),
// 0.0f,
// 0.0f,
// new Color(168, 107, 76));

        renderer.setSeriesPaint(0, new Color(142, 91, 62));

        final CategoryItemRenderer categoryRenderer = (CategoryItemRenderer)plot.getRenderer();
        final CategoryItemLabelGenerator generator = new StandardCategoryItemLabelGenerator(
                "{2}",
                new DecimalFormat("0"));
        categoryRenderer.setBaseItemLabelGenerator(generator);
        categoryRenderer.setBaseItemLabelsVisible(true);

        final CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.STANDARD);
//        domainAxis.setCategoryLabelPositions(
//        CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0));
        // OPTIONAL CUSTOMISATION COMPLETED.

        final ChartPanel chartPanel = new ChartPanel(chart);
        // chartPanel.setOpaque(false);
        chartPanel.setBackground(new Color(255, 255, 255));
        this.add(chartPanel);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        setBackground(new java.awt.Color(255, 255, 255));
        setPreferredSize(new java.awt.Dimension(150, 150));
        setLayout(new java.awt.GridLayout(1, 1));
    } // </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
