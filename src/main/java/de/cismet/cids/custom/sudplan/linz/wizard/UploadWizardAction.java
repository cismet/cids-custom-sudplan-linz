/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.linz.wizard;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;
import Sirius.navigator.ui.ComponentRegistry;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.log4j.Logger;

import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;

import java.net.URL;

import java.security.cert.CertificateException;

import java.text.MessageFormat;

import java.util.List;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import de.cismet.cids.custom.sudplan.commons.SudplanConcurrency;
import de.cismet.cids.custom.sudplan.linz.SwmmInput;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cids.utils.abstracts.AbstractCidsBeanAction;

import de.cismet.tools.PasswordEncrypter;
import de.cismet.tools.PropertyReader;

/**
 * DOCUMENT ME!
 *
 * @author   pascal.dihe@cismet.de
 * @version  $Revision$, $Date$
 */
public final class UploadWizardAction extends AbstractCidsBeanAction {

    //~ Static fields/initializers ---------------------------------------------

    private static final PropertyReader propertyReader;
    private static final String FILE_PROPERTY = "/de/cismet/cids/custom/sudplan/repositories.properties";
    public static final String SWMM_WEBDAV_HOST;
    public static final String SWMM_WEBDAV_USERNAME;
    public static final String SWMM_WEBDAV_PASSWORD;
    public static final String TABLENAME_SWMM_PROJECT = SwmmInput.TABLENAME_SWMM_PROJECT;
    public static final String PROP_NEW_SWMM_PROJECT_BEAN = "__prop_new_swmm_project_bean__";       // NOI18N
    public static final String PROP_SELECTED_SWMM_PROJECT_ID = "__prop_selected_swmm_project_id__"; // NOI18N
    public static final String PROP_SWMM_INP_FILE = "__prop_swmm_inp_file__";                       // NOI18N
    public static final String PROP_UPLOAD_COMPLETE = "__prop_upload_complete__";                   // NOI18N
    public static final String PROP_UPLOAD_ERRORNEOUS = "__prop_upload_erroneous__";                // NOI18N
    public static final String PROP_UPLOAD_IN_PROGRESS = "__prop_upload_in_progress__";             // NOI18N
    public static final String PROP_COPY_CSOS_COMPLETE = "__prop_copy_csos_complete__";             // NOI18N
    public static final String PROP_COPY_CSOS_ERRORNEOUS = "__prop_copy_csos_erroneous__";          // NOI18N
    public static final String PROP_COPY_CSOS_IN_PROGRESS = "__prop_copy_csos_in_progress__";       // NOI18N
    public static final String PROP_COPIED_CSOS = "__prop_copied_csos__";                           // NOI18N
    private static final transient Logger LOG = Logger.getLogger(UploadWizardAction.class);

    static {
        propertyReader = new PropertyReader(FILE_PROPERTY);
        SWMM_WEBDAV_HOST = propertyReader.getProperty("SWMM_WEBDAV_HOST");
        SWMM_WEBDAV_USERNAME = propertyReader.getProperty("SWMM_WEBDAV_USERNAME");
        SWMM_WEBDAV_PASSWORD = String.valueOf(PasswordEncrypter.decrypt(
                    propertyReader.getProperty("SWMM_WEBDAV_PASSWORD").toCharArray(),
                    true));
    }

    //~ Instance fields --------------------------------------------------------

    private transient WizardDescriptor.Panel[] panels;
    private transient WizardDescriptor wizardDescriptor;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new UploadWizardAction object.
     */
    public UploadWizardAction() {
        super("Perform SWMM Project Upload");
        if (LOG.isDebugEnabled()) {
            LOG.debug("Perform SWMM Project Upload Action instanciated");
        }
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * EDT only !
     *
     * @return  DOCUMENT ME!
     */
    private WizardDescriptor.Panel[] getPanels() {
        assert EventQueue.isDispatchThread() : "can only be called from EDT"; // NOI18N

        if (panels == null) {
            panels = new WizardDescriptor.Panel[] {
                    new UploadWizardPanelProject(),
                    new UploadWizardPanelUpload(),
                    new UploadWizardPanelCSOs()
                };

            final String[] steps = new String[panels.length];
            for (int i = 0; i < panels.length; i++) {
                final Component c = panels[i].getComponent();
                // Default step name to component name of panel. Mainly useful
                // for getting the name of the target chooser to appear in the
                // list of steps.
                steps[i] = c.getName();
                if (c instanceof JComponent) {
                    // assume Swing components
                    final JComponent jc = (JComponent)c;
                    // Sets step number of a component
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, Integer.valueOf(i));
                    // Sets steps names for a panel
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, steps);
                    // Turn on subtitle creation on each step
                    jc.putClientProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, Boolean.TRUE);
                    // Show steps on the left side with the image on the
                    // background
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, Boolean.TRUE);
                    // Turn on numbering of all steps
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, Boolean.TRUE);
                }
            }
        }

        return panels;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  e  DOCUMENT ME!
     */
    @Override
    public void actionPerformed(final ActionEvent e) {
        LOG.info("Wizard actionPerformed: " + e.getActionCommand());

        final MetaClass swmmModelClass = ClassCacheMultiple.getMetaClass(
                SessionManager.getSession().getUser().getDomain(),
                TABLENAME_SWMM_PROJECT);

        final CidsBean newSwmmBean = swmmModelClass.getEmptyInstance().getBean();

        wizardDescriptor = new WizardDescriptor(this.getPanels());
        wizardDescriptor.setTitleFormat(new MessageFormat("{0}"));                // NOI18N
        wizardDescriptor.setTitle(NbBundle.getMessage(
                UploadWizardAction.class,
                "UploadWizardAction.actionPerformed(ActionEvent).wizard.title")); // NOI18N

        if (this.getCidsBean() != null) {
            wizardDescriptor.putProperty(PROP_SELECTED_SWMM_PROJECT_ID,
                this.getCidsBean().getProperty("id"));
        } else {
            wizardDescriptor.putProperty(PROP_SELECTED_SWMM_PROJECT_ID, "-1");
        }

        wizardDescriptor.putProperty(PROP_NEW_SWMM_PROJECT_BEAN, newSwmmBean);
        wizardDescriptor.putProperty(PROP_SWMM_INP_FILE, "");
        wizardDescriptor.putProperty(PROP_UPLOAD_COMPLETE, false);
        wizardDescriptor.putProperty(PROP_UPLOAD_ERRORNEOUS, false);
        wizardDescriptor.putProperty(PROP_UPLOAD_IN_PROGRESS, false);
        wizardDescriptor.putProperty(PROP_COPY_CSOS_COMPLETE, false);
        wizardDescriptor.putProperty(PROP_COPY_CSOS_ERRORNEOUS, false);
        wizardDescriptor.putProperty(PROP_COPY_CSOS_IN_PROGRESS, false);

        final Dialog dialog = DialogDisplayer.getDefault().createDialog(wizardDescriptor);

        dialog.pack();
        dialog.setLocationRelativeTo(ComponentRegistry.getRegistry().getMainWindow());
        dialog.setVisible(true);
        dialog.toFront();

        final boolean cancelled = wizardDescriptor.getValue() != WizardDescriptor.FINISH_OPTION;

        if (!cancelled) {
            if (LOG.isDebugEnabled()) {
                LOG.info("wizard closed (not cancelled), new SWMM Model saved");
            }
            ComponentRegistry.getRegistry().getCatalogueTree().requestRefreshNode("local.linz.projects");
        } else {
            final boolean uploadComplete = (Boolean)wizardDescriptor.getProperty(PROP_UPLOAD_COMPLETE);
            final boolean copyCSOsComplete = (Boolean)wizardDescriptor.getProperty(PROP_COPY_CSOS_COMPLETE);

            if (uploadComplete || copyCSOsComplete) {
                LOG.warn("Wizard cancelled :o(! Trying to remove created meta objects");
                final CleanUpThread cleanUpThread = new CleanUpThread(uploadComplete, copyCSOsComplete);
                SudplanConcurrency.getSudplanGeneralPurposePool().execute(cleanUpThread);
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Wizard cancelled: nothing to remove :o)");
                }
            }
        }
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class CleanUpThread implements Runnable {

        //~ Instance fields ----------------------------------------------------

        final boolean uploadComplete;
        final boolean copyCSOsComplete;

        private final transient Logger LOG = Logger.getLogger(CleanUpThread.class);

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new CleanUpThread object.
         *
         * @param  uploadComplete    DOCUMENT ME!
         * @param  copyCSOsComplete  DOCUMENT ME!
         */
        private CleanUpThread(final boolean uploadComplete, final boolean copyCSOsComplete) {
            this.uploadComplete = uploadComplete;
            this.copyCSOsComplete = copyCSOsComplete;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public void run() {
            assert wizardDescriptor.getProperty(PROP_NEW_SWMM_PROJECT_BEAN) != null : "SWMM Project Bean must not be null";
            final CidsBean newSwmmBean = (CidsBean)wizardDescriptor.getProperty(PROP_NEW_SWMM_PROJECT_BEAN);
            final String domain = SessionManager.getSession().getUser().getDomain();

            if (((Integer)newSwmmBean.getProperty("id")) != -1) {
                LOG.warn("deleting new SWMM Bean #" + newSwmmBean.getProperty("id"));

                try {
                    SessionManager.getProxy().deleteMetaObject(newSwmmBean.getMetaObject(), domain);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("new SWMM Bean #" + newSwmmBean.getProperty("id") + " deleted successfully");
                    }
                } catch (Exception e) {
                    LOG.error("removal of new SWMM Bean #" + newSwmmBean.getProperty("id") + " failed: "
                                + e.getLocalizedMessage(),
                        e);
                }
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("new SWMM Bean not stored yet, no need to delete it");
                }
            }

            if (this.uploadComplete) {
                final String inpFile = newSwmmBean.getProperty("inp_file_name").toString();
                LOG.warn("deleting upload file '" + inpFile + "' from " + UploadWizardAction.SWMM_WEBDAV_HOST);

                final UsernamePasswordCredentials defaultcreds = new UsernamePasswordCredentials(
                        UploadWizardAction.SWMM_WEBDAV_USERNAME,
                        UploadWizardAction.SWMM_WEBDAV_PASSWORD);

                try {
                    final SSLSocketFactory sslsf = new SSLSocketFactory(new TrustStrategy() {

                                @Override
                                public boolean isTrusted(final java.security.cert.X509Certificate[] chain,
                                        final String authType) throws CertificateException {
                                    return true;
                                }
                            });
                    final Scheme httpScheme = new Scheme("http", 80, PlainSocketFactory.getSocketFactory());
                    final Scheme httpsScheme = new Scheme("https", 443, sslsf);
                    final SchemeRegistry schemeRegistry = new SchemeRegistry();
                    schemeRegistry.register(httpScheme);
                    schemeRegistry.register(httpsScheme);

                    final ClientConnectionManager cm = new SingleClientConnManager(schemeRegistry);

                    final HttpContext localContext = new BasicHttpContext();
                    final DefaultHttpClient httpClient = new DefaultHttpClient(cm);

                    httpClient.getCredentialsProvider().setCredentials(
                        new AuthScope(AuthScope.ANY),
                        defaultcreds);

                    final URL targetLocation = new URL(
                            UploadWizardAction.SWMM_WEBDAV_HOST
                                    + inpFile);

                    final HttpGet getMethod = new HttpGet(new URL(UploadWizardAction.SWMM_WEBDAV_HOST)
                                    .toExternalForm());

                    HttpResponse response = httpClient.execute(getMethod, localContext);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("pre-put authentication with GET returned '"
                                    + response.getStatusLine() + "'");
                    }

                    if ((response.getStatusLine().getStatusCode() != 200)) {
                        LOG.warn("pre-put authentication with GET failed with status code: "
                                    + response.getStatusLine().getStatusCode());
                    }

                    getMethod.abort();

                    final HttpDelete deleteMethod = new HttpDelete(targetLocation.toExternalForm());

                    if (LOG.isDebugEnabled()) {
                        LOG.debug("deleting uploaded file '" + inpFile + "'");
                    }
                    response = httpClient.execute(deleteMethod, localContext);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("deleting '" + inpFile + "' completed");
                    }

                    final int statusCode = response.getStatusLine().getStatusCode();
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Leaving delete '" + inpFile + "' with status code: " + statusCode);
                    }

                    if ((statusCode != 200) && (statusCode != 202) && (statusCode != 204)) {
                        final String message = "Deletion of file '"
                                    + inpFile + "' not successful, server returned status '"
                                    + response.getStatusLine() + "'";
                        throw new Exception(message);
                    }

                    try {
                        httpClient.getConnectionManager().shutdown();
                    } catch (Exception e) {
                        LOG.warn("could not close httpClient connection", e);
                    }
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("uploaded file '" + inpFile + "' successfully deleted from "
                                    + UploadWizardAction.SWMM_WEBDAV_HOST);
                    }
                } catch (Exception e) {
                    LOG.error("removal of uploaded file '" + inpFile + "' from "
                                + UploadWizardAction.SWMM_WEBDAV_HOST + " failed: "
                                + e.getLocalizedMessage(),
                        e);
                }
            }

            if (copyCSOsComplete) {
                if (wizardDescriptor.getProperty(UploadWizardAction.PROP_COPIED_CSOS) != null) {
                    final List<MetaObject> copiedCSOs = (List<MetaObject>)wizardDescriptor.getProperty(
                            UploadWizardAction.PROP_COPIED_CSOS);
                    LOG.warn("removing " + copiedCSOs.size() + " copied CSOs from " + domain);

                    int i = 0;
                    for (final MetaObject cso : copiedCSOs) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("removing CSO '" + cso + "' from " + domain);
                        }
                        try {
                            SessionManager.getProxy().deleteMetaObject(cso, domain);
                            i++;
                        } catch (Exception ex) {
                            LOG.error("could not remove CSO '" + cso + "' from " + domain + ": "
                                        + ex.getMessage(), ex);
                        }
                    }
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("successfully removed " + i + " out of " + copiedCSOs.size() + " CSOs from "
                                    + domain);
                    }
                } else {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(
                            "could not remove copied CSOs: copyCSOsComplete is true, but list of CSOs is empty?!");
                    }
                }
            }
        }
    }
}
