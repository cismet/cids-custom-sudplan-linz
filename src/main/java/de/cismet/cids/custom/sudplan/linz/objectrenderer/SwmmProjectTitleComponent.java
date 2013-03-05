/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.linz.objectrenderer;

import org.apache.log4j.Logger;

import org.openide.util.NbBundle;

import java.awt.EventQueue;

import de.cismet.cids.custom.objectactions.sudplan.ActionProviderFactory;
import de.cismet.cids.custom.sudplan.linz.wizard.EtaWizardAction;
import de.cismet.cids.custom.sudplan.linz.wizard.SwmmPlusEtaWizardAction;
import de.cismet.cids.custom.sudplan.linz.wizard.UploadWizardAction;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.utils.interfaces.CidsBeanAction;

/**
 * DOCUMENT ME!
 *
 * @author   pascal.dihe@cismet.de
 * @version  $Revision$, $Date$
 */
public class SwmmProjectTitleComponent extends javax.swing.JPanel {

    // private static final transient Logger LOG = Logger.getLogger(SwmmProjectTitleComponent.class);

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnNewSwmmProject;
    private javax.swing.JButton btnRunEta;
    private javax.swing.JButton btnRunSwmmPlusEta;
    private javax.swing.JLabel lblIcon;
    private javax.swing.JLabel lblTitle;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form RunGeoCPMTitleComponent.
     */
    public SwmmProjectTitleComponent() {
        initComponents();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  title  DOCUMENT ME!
     */
    public void setTitle(final String title) {
        if (EventQueue.isDispatchThread()) {
            lblTitle.setText(title);
        } else {
            EventQueue.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        lblTitle.setText(title);
                    }
                });
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBean  DOCUMENT ME!
     */
    public void setCidsBean(final CidsBean cidsBean) {
        if (btnRunSwmmPlusEta.getAction() instanceof CidsBeanAction) {
            final CidsBeanAction cba = (CidsBeanAction)btnRunSwmmPlusEta.getAction();
            cba.setCidsBean(cidsBean);

            // trigger the action enable
            cba.isEnabled();
        }

        if (btnRunEta.getAction() instanceof CidsBeanAction) {
            final CidsBeanAction cba = (CidsBeanAction)btnRunEta.getAction();
            cba.setCidsBean(cidsBean);

            // trigger the action enable
            cba.isEnabled();
        }
        if (btnNewSwmmProject.getAction() instanceof CidsBeanAction) {
            final CidsBeanAction cba = (CidsBeanAction)btnNewSwmmProject.getAction();
            cba.setCidsBean(cidsBean);

            // trigger the action enable
            cba.isEnabled();
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

        lblIcon = new javax.swing.JLabel();
        lblTitle = new javax.swing.JLabel();
        btnNewSwmmProject = new javax.swing.JButton();
        btnRunSwmmPlusEta = new javax.swing.JButton();
        btnRunEta = new javax.swing.JButton();

        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        lblIcon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblIcon.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/sudplan/linz/EPAlogo32ct.png"))); // NOI18N
        lblIcon.setText(org.openide.util.NbBundle.getMessage(
                SwmmProjectTitleComponent.class,
                "SwmmProjectTitleComponent.lblIcon.text"));                                      // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblIcon, gridBagConstraints);

        lblTitle.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblTitle.setText(NbBundle.getMessage(
                SwmmProjectTitleComponent.class,
                "SwmmProjectTitleComponent.lblTitle.text"));  // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblTitle, gridBagConstraints);

        btnNewSwmmProject.setAction(ActionProviderFactory.getCidsBeanAction(UploadWizardAction.class));
        btnNewSwmmProject.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/sudplan/linz/newSwmmProject24.png"))); // NOI18N
        btnNewSwmmProject.setText("");
        btnNewSwmmProject.setToolTipText(org.openide.util.NbBundle.getMessage(
                SwmmProjectTitleComponent.class,
                "SwmmProjectTitleComponent.btnNewSwmmProject.tooltip"));                              // NOI18N
        btnNewSwmmProject.setMargin(new java.awt.Insets(4, 4, 4, 4));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(btnNewSwmmProject, gridBagConstraints);

        btnRunSwmmPlusEta.setAction(ActionProviderFactory.getCidsBeanAction(SwmmPlusEtaWizardAction.class));
        btnRunSwmmPlusEta.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/sudplan/linz/newSwmmCalculation24.png"))); // NOI18N
        btnRunSwmmPlusEta.setText("");
        btnRunSwmmPlusEta.setToolTipText(org.openide.util.NbBundle.getMessage(
                SwmmProjectTitleComponent.class,
                "SwmmProjectTitleComponent.btnRunSwmmPlusEta.tooltip"));                                  // NOI18N
        btnRunSwmmPlusEta.setMargin(new java.awt.Insets(4, 4, 4, 4));
        add(btnRunSwmmPlusEta, new java.awt.GridBagConstraints());

        btnRunEta.setAction(ActionProviderFactory.getCidsBeanAction(EtaWizardAction.class));
        btnRunEta.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/sudplan/linz/newEtaCalculation24.png"))); // NOI18N
        btnRunEta.setText("");
        btnRunEta.setToolTipText(org.openide.util.NbBundle.getMessage(
                SwmmProjectTitleComponent.class,
                "SwmmProjectTitleComponent.btnRunEta.toolTipText"));                                     // NOI18N
        btnRunEta.setMargin(new java.awt.Insets(4, 4, 4, 4));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(btnRunEta, gridBagConstraints);
    }                                                                                                    // </editor-fold>//GEN-END:initComponents
}
