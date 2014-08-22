/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * FotosEditor.java
 *
 * Created on 10.08.2010, 16:47:00
 */
package de.cismet.cids.custom.sudplan.linz.objectrenderer;

import Sirius.navigator.ui.ComponentRegistry;
import Sirius.navigator.ui.RequestsFullSizeComponent;

import org.apache.commons.io.IOUtils;

import org.jdesktop.swingx.JXHyperlink;

import org.openide.util.WeakListeners;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import java.lang.ref.SoftReference;

import java.net.URLEncoder;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.event.IIOReadProgressListener;
import javax.imageio.stream.ImageInputStream;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.border.Border;

import de.cismet.cids.custom.sudplan.AbstractCidsBeanRenderer;
import de.cismet.cids.custom.sudplan.ImageUtil;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

import de.cismet.netutil.Proxy;

import de.cismet.commons.security.WebDavClient;

import de.cismet.tools.CismetThreadPool;
import de.cismet.tools.PasswordEncrypter;
import de.cismet.tools.PropertyReader;

import de.cismet.tools.gui.BorderProvider;
import de.cismet.tools.gui.FooterComponentProvider;
import de.cismet.tools.gui.RoundedPanel;
import de.cismet.tools.gui.TitleComponentProvider;

/**
 * DOCUMENT ME!
 *
 * @author   stefan
 * @version  $Revision$, $Date$
 */
public class LinzCsoRenderer extends AbstractCidsBeanRenderer implements BorderProvider,
    CidsBeanRenderer,
    FooterComponentProvider,
    TitleComponentProvider,
    RequestsFullSizeComponent {

    //~ Static fields/initializers ---------------------------------------------

    private static final PropertyReader propertyReader;
    private static final String FILE_PROPERTY = "/de/cismet/cids/custom/sudplan/repositories.properties";

    private static final String CARD_1 = "CARD1";
    private static final String CARD_2 = "CARD2";
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LinzCsoRenderer.class);
    private static final ImageIcon ERROR_ICON = new ImageIcon(LinzCsoRenderer.class.getResource(
                "/de/cismet/cids/custom/objectrenderer/sudplan/file-broken.png"));
    private static final ImageIcon FOLDER_ICON = new ImageIcon(LinzCsoRenderer.class.getResource(
                "/de/cismet/cids/custom/objectrenderer/sudplan/inode-directory.png"));
    private static final String LINZ_IMAGES_WEBDAV_USERNAME;
    private static final String LINZ_IMAGES_WEBDAV_PASSWORD;
    private static final String LINZ_IMAGES_WEBDAV_HOST;
    private static final int CACHE_SIZE = 20;
    private static final Map<String, SoftReference<BufferedImage>> IMAGE_CACHE =
        new LinkedHashMap<String, SoftReference<BufferedImage>>(CACHE_SIZE) {

            @Override
            protected boolean removeEldestEntry(final Entry<String, SoftReference<BufferedImage>> eldest) {
                return size() >= CACHE_SIZE;
            }
        };

    static {
        propertyReader = new PropertyReader(FILE_PROPERTY);

        LINZ_IMAGES_WEBDAV_HOST = propertyReader.getProperty("LINZ_IMAGES_WEBDAV_HOST");
        LINZ_IMAGES_WEBDAV_USERNAME = propertyReader.getProperty("LINZ_IMAGES_WEBDAV_USERNAME");
        LINZ_IMAGES_WEBDAV_PASSWORD = String.valueOf(PasswordEncrypter.decrypt(
                    propertyReader.getProperty("LINZ_IMAGES_WEBDAV_PASSWORD").toCharArray(),
                    false));
    }

    //~ Instance fields --------------------------------------------------------

    private final transient LinzCsoTitleComponent linzCsoTitleComponent = new LinzCsoTitleComponent();

    // private final transient LinzCsoTitleComponent titleComponent = new LinzCsoTitleComponent();
    private final transient Timer timer;
    private final transient CardLayout cardLayout;
    private transient ImageResizeWorker currentResizeWorker;
    private transient BufferedImage image;
    private transient boolean listListenerEnabled = true;
    private transient boolean resizeListenerEnabled;
    private final transient WebDavClient webDavClient;
    private transient LinksListener linksListener;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnForward;
    private javax.swing.JButton btnNextPage;
    private javax.swing.JButton btnPrevPage;
    private de.cismet.cids.custom.sudplan.linz.CsoOverflowComparisionPanel csoOverflowComparisionPanel;
    private de.cismet.cids.custom.sudplan.linz.CsoTotalOverflowComparisionPanel csoTotalOverflowComparisionPanel;
    private de.cismet.tools.gui.RoundedPanel etaScenarioPanel;
    private org.jdesktop.swingx.JXBusyLabel lblBusy;
    private javax.swing.JLabel lblDescription;
    private javax.swing.JLabel lblDescriptionText;
    private javax.swing.JLabel lblEtaScenarios;
    private javax.swing.JLabel lblFotoList;
    private javax.swing.JLabel lblHeadingPhotos;
    private javax.swing.JLabel lblLinksHeading;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblNameText;
    private javax.swing.JLabel lblNextPage;
    private javax.swing.JLabel lblOutfall;
    private javax.swing.JLabel lblPicture;
    private javax.swing.JLabel lblPrevPage;
    private javax.swing.JLabel lblPropertiesHeading;
    private javax.swing.JLabel lblStorageUnit;
    private javax.swing.JLabel lblSwmmProject;
    private javax.swing.JLabel lblSwmmProjectText;
    private javax.swing.JLabel lblSwmmScenarios;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblVolume;
    private javax.swing.JLabel lblVolumeText;
    private de.cismet.tools.gui.RoundedPanel linksPanel;
    private javax.swing.JList lstFotos;
    private javax.swing.JPanel panButtons;
    private javax.swing.JPanel panButtons1;
    private javax.swing.JPanel panCard;
    private javax.swing.JPanel panFooter;
    private javax.swing.JPanel panFooterLeft;
    private javax.swing.JPanel panFooterNextPage;
    private javax.swing.JPanel panFooterPrevPage;
    private javax.swing.JPanel panFooterRight;
    private de.cismet.tools.gui.SemiRoundedPanel panHeadInfo;
    private de.cismet.tools.gui.SemiRoundedPanel panHeadInfo1;
    private de.cismet.tools.gui.SemiRoundedPanel panHeadInfoScenario;
    private de.cismet.tools.gui.SemiRoundedPanel panHeadInfoScenario1;
    private de.cismet.tools.gui.SemiRoundedPanel panLinksHeadInfo;
    private javax.swing.JPanel panLinksSpacer;
    private javax.swing.JPanel panPage1;
    private javax.swing.JPanel panPage2;
    private javax.swing.JPanel panPreview;
    private javax.swing.JPanel panTitle;
    private de.cismet.tools.gui.RoundedPanel photosPanel;
    private de.cismet.tools.gui.RoundedPanel propertiesPanel;
    private javax.swing.JScrollPane scpFotoList;
    private javax.swing.JScrollPane scrollPane;
    private de.cismet.tools.gui.RoundedPanel swmmScenarioPanel;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form FotosEditor.
     */
    public LinzCsoRenderer() {
        this(false);
    }

    /**
     * Creates a new FotodokumentationEditor object.
     *
     * @param  editable  DOCUMENT ME!
     */
    public LinzCsoRenderer(final boolean editable) {
        this.listListenerEnabled = true;
        this.resizeListenerEnabled = true;
        this.webDavClient = new WebDavClient(Proxy.fromPreferences(),
                LINZ_IMAGES_WEBDAV_USERNAME,
                LINZ_IMAGES_WEBDAV_PASSWORD);
        initComponents();

        timer = new Timer(300, new ActionListener() {

                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        if (resizeListenerEnabled) {
//                    if (isShowing()) {
                            if (currentResizeWorker != null) {
                                currentResizeWorker.cancel(true);
                            }
                            currentResizeWorker = new ImageResizeWorker();
                            CismetThreadPool.execute(currentResizeWorker);
//                    } else {
//                        timer.restart();
//                    }
                        }
                    }
                });
        timer.setRepeats(false);
        cardLayout = (CardLayout)panCard.getLayout();
        this.addComponentListener(new ComponentAdapter() {

                @Override
                public void componentResized(final ComponentEvent e) {
                    if ((image != null) && !lblBusy.isBusy()) {
                        showWait(true);
                        timer.restart();
                    }
                }
            });

        lblPicture.setIcon(FOLDER_ICON);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void dispose() {
        if (currentResizeWorker != null) {
            currentResizeWorker.cancel(true);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   fileName  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    private BufferedImage downloadImageFromWebDAV(final String fileName) throws IOException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("downloadImageFromWebDAV:" + fileName);
        }
        final String encodedFileName = encodeURL(fileName);
        final InputStream iStream = webDavClient.getInputStream(LINZ_IMAGES_WEBDAV_HOST
                        + encodedFileName);
        if (LOG.isDebugEnabled()) {
            LOG.debug("original: " + fileName + "\nweb dav path: " + LINZ_IMAGES_WEBDAV_HOST + encodedFileName);
        }
        try {
            final ImageInputStream iiStream = ImageIO.createImageInputStream(iStream);
            final Iterator<ImageReader> itReader = ImageIO.getImageReaders(iiStream);

            if (!itReader.hasNext()) {
                throw new IOException("could not download image '" + fileName + "' from webdav '"
                            + LINZ_IMAGES_WEBDAV_HOST + "': file not found");
            }

            final ImageReader reader = itReader.next();
            final ProgressMonitor monitor = new ProgressMonitor(this, "Bild wird übertragen...", "", 0, 100);
//            monitor.setMillisToPopup(500);
            reader.addIIOReadProgressListener(new IIOReadProgressListener() {

                    @Override
                    public void sequenceStarted(final ImageReader source, final int minIndex) {
                    }

                    @Override
                    public void sequenceComplete(final ImageReader source) {
                    }

                    @Override
                    public void imageStarted(final ImageReader source, final int imageIndex) {
                        monitor.setProgress(monitor.getMinimum());
                    }

                    @Override
                    public void imageProgress(final ImageReader source, final float percentageDone) {
                        if (monitor.isCanceled()) {
                            try {
                                iiStream.close();
                            } catch (IOException ex) {
                                // NOP
                            }
                        } else {
                            monitor.setProgress(Math.round(percentageDone));
                        }
                    }

                    @Override
                    public void imageComplete(final ImageReader source) {
                        monitor.setProgress(monitor.getMaximum());
                    }

                    @Override
                    public void thumbnailStarted(final ImageReader source,
                            final int imageIndex,
                            final int thumbnailIndex) {
                    }

                    @Override
                    public void thumbnailProgress(final ImageReader source, final float percentageDone) {
                    }

                    @Override
                    public void thumbnailComplete(final ImageReader source) {
                    }

                    @Override
                    public void readAborted(final ImageReader source) {
                        monitor.close();
                    }
                });

            final ImageReadParam param = reader.getDefaultReadParam();
            reader.setInput(iiStream, true, true);
            final BufferedImage result;
            try {
                result = reader.read(0, param);
            } finally {
                reader.dispose();
                iiStream.close();
            }
            return result;
        } finally {
            IOUtils.closeQuietly(iStream);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   url  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String encodeURL(final String url) {
        try {
            if (url == null) {
                return null;
            }
            final String[] tokens = url.split("/", -1);
            StringBuilder encodedURL = null;

            for (final String tmp : tokens) {
                if (encodedURL == null) {
                    encodedURL = new StringBuilder(URLEncoder.encode(tmp, "UTF-8"));
                } else {
                    encodedURL.append("/").append(URLEncoder.encode(tmp, "UTF-8"));
                }
            }

            if (encodedURL != null) {
                // replace all + with %20 because the method URLEncoder.encode() replaces all spaces with '+', but
                // the web dav client interprets %20 as a space.
                return encodedURL.toString().replaceAll("\\+", "%20");
            } else {
                return "";
            }
        } catch (final UnsupportedEncodingException e) {
            LOG.error("Unsupported encoding.", e);
        }
        return url;
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        panFooter = new javax.swing.JPanel();
        panButtons1 = new javax.swing.JPanel();
        panFooterPrevPage = new javax.swing.JPanel();
        lblPrevPage = new javax.swing.JLabel();
        btnPrevPage = new javax.swing.JButton();
        panFooterNextPage = new javax.swing.JPanel();
        btnNextPage = new javax.swing.JButton();
        lblNextPage = new javax.swing.JLabel();
        panTitle = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        panPage1 = new javax.swing.JPanel();
        propertiesPanel = new de.cismet.tools.gui.RoundedPanel();
        panHeadInfo = new de.cismet.tools.gui.SemiRoundedPanel();
        lblPropertiesHeading = new javax.swing.JLabel();
        lblName = new javax.swing.JLabel();
        lblNameText = new javax.swing.JLabel();
        lblDescription = new javax.swing.JLabel();
        lblDescriptionText = new javax.swing.JLabel();
        lblVolume = new javax.swing.JLabel();
        lblVolumeText = new javax.swing.JLabel();
        scpFotoList = new javax.swing.JScrollPane();
        lstFotos = new javax.swing.JList();
        lblFotoList = new javax.swing.JLabel();
        linksPanel = new de.cismet.tools.gui.RoundedPanel();
        panLinksHeadInfo = new de.cismet.tools.gui.SemiRoundedPanel();
        lblLinksHeading = new javax.swing.JLabel();
        lblOutfall = new javax.swing.JLabel();
        lblStorageUnit = new javax.swing.JLabel();
        lblSwmmProject = new javax.swing.JLabel();
        lblSwmmProjectText = new javax.swing.JLabel();
        panLinksSpacer = new javax.swing.JPanel();
        photosPanel = new de.cismet.tools.gui.RoundedPanel();
        panHeadInfo1 = new de.cismet.tools.gui.SemiRoundedPanel();
        lblHeadingPhotos = new javax.swing.JLabel();
        panCard = new javax.swing.JPanel();
        lblBusy = new org.jdesktop.swingx.JXBusyLabel(new Dimension(75, 75));
        panPreview = new javax.swing.JPanel();
        lblPicture = new javax.swing.JLabel();
        final RoundedPanel rp = new RoundedPanel();
        rp.setBackground(new java.awt.Color(51, 51, 51));
        rp.setAlpha(255);
        panButtons = rp;
        panFooterLeft = new javax.swing.JPanel();
        btnBack = new javax.swing.JButton();
        panFooterRight = new javax.swing.JPanel();
        btnForward = new javax.swing.JButton();
        panPage2 = new javax.swing.JPanel();
        swmmScenarioPanel = new de.cismet.tools.gui.RoundedPanel();
        panHeadInfoScenario = new de.cismet.tools.gui.SemiRoundedPanel();
        lblSwmmScenarios = new javax.swing.JLabel();
        csoOverflowComparisionPanel = new de.cismet.cids.custom.sudplan.linz.CsoOverflowComparisionPanel();
        etaScenarioPanel = new de.cismet.tools.gui.RoundedPanel();
        panHeadInfoScenario1 = new de.cismet.tools.gui.SemiRoundedPanel();
        lblEtaScenarios = new javax.swing.JLabel();
        scrollPane = new javax.swing.JScrollPane();
        csoTotalOverflowComparisionPanel = new de.cismet.cids.custom.sudplan.linz.CsoTotalOverflowComparisionPanel();

        panFooter.setOpaque(false);
        panFooter.setLayout(new java.awt.BorderLayout());

        panButtons1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 6, 0));
        panButtons1.setOpaque(false);
        panButtons1.setLayout(new java.awt.GridBagLayout());

        panFooterPrevPage.setMaximumSize(null);
        panFooterPrevPage.setOpaque(false);
        panFooterPrevPage.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 5));

        lblPrevPage.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblPrevPage.setForeground(new java.awt.Color(255, 255, 255));
        lblPrevPage.setText(org.openide.util.NbBundle.getMessage(
                LinzCsoRenderer.class,
                "LinzCsoRenderer.lblPrevPage.text"));            // NOI18N
        lblPrevPage.setEnabled(false);
        lblPrevPage.setMaximumSize(null);
        lblPrevPage.setPreferredSize(null);
        lblPrevPage.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    lblPrevPageMouseClicked(evt);
                }
            });
        panFooterPrevPage.add(lblPrevPage);

        btnPrevPage.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/sudplan/linz/arrow-left.png"))); // NOI18N
        btnPrevPage.setBorderPainted(false);
        btnPrevPage.setContentAreaFilled(false);
        btnPrevPage.setEnabled(false);
        btnPrevPage.setFocusPainted(false);
        btnPrevPage.setMaximumSize(new java.awt.Dimension(30, 30));
        btnPrevPage.setMinimumSize(new java.awt.Dimension(30, 30));
        btnPrevPage.setPreferredSize(new java.awt.Dimension(30, 30));
        btnPrevPage.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnPrevPageActionPerformed(evt);
                }
            });
        panFooterPrevPage.add(btnPrevPage);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        panButtons1.add(panFooterPrevPage, gridBagConstraints);

        panFooterNextPage.setMaximumSize(null);
        panFooterNextPage.setOpaque(false);
        panFooterNextPage.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 5));

        btnNextPage.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/sudplan/linz/arrow-right.png"))); // NOI18N
        btnNextPage.setBorderPainted(false);
        btnNextPage.setContentAreaFilled(false);
        btnNextPage.setFocusPainted(false);
        btnNextPage.setMaximumSize(new java.awt.Dimension(30, 30));
        btnNextPage.setMinimumSize(new java.awt.Dimension(30, 30));
        btnNextPage.setPreferredSize(new java.awt.Dimension(30, 30));
        btnNextPage.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnNextPageActionPerformed(evt);
                }
            });
        panFooterNextPage.add(btnNextPage);

        lblNextPage.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblNextPage.setForeground(new java.awt.Color(255, 255, 255));
        lblNextPage.setText(org.openide.util.NbBundle.getMessage(
                LinzCsoRenderer.class,
                "LinzCsoRenderer.lblNextPage.text"));            // NOI18N
        lblNextPage.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    lblNextPageMouseClicked(evt);
                }
            });
        panFooterNextPage.add(lblNextPage);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        panButtons1.add(panFooterNextPage, gridBagConstraints);

        panFooter.add(panButtons1, java.awt.BorderLayout.CENTER);

        panTitle.setOpaque(false);
        panTitle.setLayout(new java.awt.GridBagLayout());

        lblTitle.setFont(new java.awt.Font("Tahoma", 1, 18));                                                           // NOI18N
        lblTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblTitle.setText(org.openide.util.NbBundle.getMessage(LinzCsoRenderer.class, "LinzCsoRenderer.lblTitle.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 15, 10, 10);
        panTitle.add(lblTitle, gridBagConstraints);

        setOpaque(false);
        setLayout(new java.awt.CardLayout());

        panPage1.setOpaque(false);
        panPage1.setLayout(new java.awt.GridBagLayout());

        propertiesPanel.setLayout(new java.awt.GridBagLayout());

        panHeadInfo.setBackground(new java.awt.Color(51, 51, 51));
        panHeadInfo.setMinimumSize(new java.awt.Dimension(109, 24));
        panHeadInfo.setPreferredSize(new java.awt.Dimension(109, 24));
        panHeadInfo.setLayout(new java.awt.FlowLayout());

        lblPropertiesHeading.setForeground(new java.awt.Color(255, 255, 255));
        lblPropertiesHeading.setText(org.openide.util.NbBundle.getMessage(
                LinzCsoRenderer.class,
                "LinzCsoRenderer.lblPropertiesHeading.text")); // NOI18N
        panHeadInfo.add(lblPropertiesHeading);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        propertiesPanel.add(panHeadInfo, gridBagConstraints);

        lblName.setFont(new java.awt.Font("Tahoma", 1, 11));                                                          // NOI18N
        lblName.setText(org.openide.util.NbBundle.getMessage(LinzCsoRenderer.class, "LinzCsoRenderer.lblName.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        propertiesPanel.add(lblName, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        propertiesPanel.add(lblNameText, gridBagConstraints);

        lblDescription.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblDescription.setText(org.openide.util.NbBundle.getMessage(
                LinzCsoRenderer.class,
                "LinzCsoRenderer.lblDescription.text"));            // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        propertiesPanel.add(lblDescription, gridBagConstraints);

        lblDescriptionText.setText(org.openide.util.NbBundle.getMessage(
                LinzCsoRenderer.class,
                "LinzCsoRenderer.lblDescriptionText.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        propertiesPanel.add(lblDescriptionText, gridBagConstraints);

        lblVolume.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblVolume.setText(org.openide.util.NbBundle.getMessage(
                LinzCsoRenderer.class,
                "LinzCsoRenderer.lblVolume.text"));            // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        propertiesPanel.add(lblVolume, gridBagConstraints);

        lblVolumeText.setName(org.openide.util.NbBundle.getMessage(
                LinzCsoRenderer.class,
                "LinzCsoRenderer.lblVolumeText.name")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        propertiesPanel.add(lblVolumeText, gridBagConstraints);

        scpFotoList.setPreferredSize(new java.awt.Dimension(258, 150));

        lstFotos.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstFotos.setMinimumSize(new java.awt.Dimension(100, 150));
        lstFotos.addListSelectionListener(new javax.swing.event.ListSelectionListener() {

                @Override
                public void valueChanged(final javax.swing.event.ListSelectionEvent evt) {
                    lstFotosValueChanged(evt);
                }
            });
        scpFotoList.setViewportView(lstFotos);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        propertiesPanel.add(scpFotoList, gridBagConstraints);

        lblFotoList.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblFotoList.setText(org.openide.util.NbBundle.getMessage(
                LinzCsoRenderer.class,
                "LinzCsoRenderer.lblFotoList.text"));            // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        propertiesPanel.add(lblFotoList, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        panPage1.add(propertiesPanel, gridBagConstraints);

        linksPanel.setLayout(new java.awt.GridBagLayout());

        panLinksHeadInfo.setBackground(new java.awt.Color(51, 51, 51));
        panLinksHeadInfo.setMinimumSize(new java.awt.Dimension(109, 24));
        panLinksHeadInfo.setPreferredSize(new java.awt.Dimension(109, 24));
        panLinksHeadInfo.setLayout(new java.awt.FlowLayout());

        lblLinksHeading.setForeground(new java.awt.Color(255, 255, 255));
        lblLinksHeading.setText(org.openide.util.NbBundle.getMessage(
                LinzCsoRenderer.class,
                "LinzCsoRenderer.lblLinksHeading.text")); // NOI18N
        panLinksHeadInfo.add(lblLinksHeading);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        linksPanel.add(panLinksHeadInfo, gridBagConstraints);

        lblOutfall.setText(org.openide.util.NbBundle.getMessage(
                LinzCsoRenderer.class,
                "LinzCsoRenderer.lblOutfall.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 10);
        linksPanel.add(lblOutfall, gridBagConstraints);

        lblStorageUnit.setText(org.openide.util.NbBundle.getMessage(
                LinzCsoRenderer.class,
                "LinzCsoRenderer.lblStorageUnit.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 10, 10);
        linksPanel.add(lblStorageUnit, gridBagConstraints);

        lblSwmmProject.setText(org.openide.util.NbBundle.getMessage(
                LinzCsoRenderer.class,
                "LinzCsoRenderer.lblSwmmProject.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 10, 10);
        linksPanel.add(lblSwmmProject, gridBagConstraints);

        lblSwmmProjectText.setPreferredSize(new java.awt.Dimension(3, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 10, 10);
        linksPanel.add(lblSwmmProjectText, gridBagConstraints);

        panLinksSpacer.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        linksPanel.add(panLinksSpacer, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        panPage1.add(linksPanel, gridBagConstraints);

        photosPanel.setLayout(new java.awt.GridBagLayout());

        panHeadInfo1.setBackground(new java.awt.Color(51, 51, 51));
        panHeadInfo1.setMinimumSize(new java.awt.Dimension(109, 24));
        panHeadInfo1.setPreferredSize(new java.awt.Dimension(109, 24));
        panHeadInfo1.setLayout(new java.awt.FlowLayout());

        lblHeadingPhotos.setForeground(new java.awt.Color(255, 255, 255));
        lblHeadingPhotos.setText(org.openide.util.NbBundle.getMessage(
                LinzCsoRenderer.class,
                "LinzCsoRenderer.lblHeadingPhotos.text")); // NOI18N
        panHeadInfo1.add(lblHeadingPhotos);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        photosPanel.add(panHeadInfo1, gridBagConstraints);

        panCard.setOpaque(false);
        panCard.setLayout(new java.awt.CardLayout());

        lblBusy.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblBusy.setMaximumSize(new java.awt.Dimension(140, 40));
        lblBusy.setMinimumSize(new java.awt.Dimension(140, 40));
        lblBusy.setPreferredSize(new java.awt.Dimension(140, 40));
        panCard.add(lblBusy, "busy");

        panPreview.setOpaque(false);
        panPreview.setLayout(new java.awt.GridBagLayout());

        lblPicture.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblPicture.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPicture.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lblPicture.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panPreview.add(lblPicture, gridBagConstraints);

        panCard.add(panPreview, "preview");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        photosPanel.add(panCard, gridBagConstraints);

        panButtons.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 6, 0));
        panButtons.setMaximumSize(new java.awt.Dimension(120, 40));
        panButtons.setMinimumSize(new java.awt.Dimension(120, 40));
        panButtons.setOpaque(false);
        panButtons.setPreferredSize(new java.awt.Dimension(120, 40));
        panButtons.setLayout(new java.awt.GridBagLayout());

        panFooterLeft.setMaximumSize(new java.awt.Dimension(20, 40));
        panFooterLeft.setMinimumSize(new java.awt.Dimension(20, 40));
        panFooterLeft.setOpaque(false);
        panFooterLeft.setPreferredSize(new java.awt.Dimension(20, 40));
        panFooterLeft.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 5));

        btnBack.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/sudplan/linz/objectrenderer/arrow-left.png")));          // NOI18N
        btnBack.setBorderPainted(false);
        btnBack.setContentAreaFilled(false);
        btnBack.setEnabled(false);
        btnBack.setFocusPainted(false);
        btnBack.setMaximumSize(new java.awt.Dimension(30, 30));
        btnBack.setMinimumSize(new java.awt.Dimension(30, 30));
        btnBack.setPreferredSize(new java.awt.Dimension(30, 30));
        btnBack.setPressedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/sudplan/linz/objectrenderer/arrow-left-pressed.png")));  // NOI18N
        btnBack.setRolloverIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/sudplan/linz/objectrenderer/arrow-left-selected.png"))); // NOI18N
        btnBack.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnBackActionPerformed(evt);
                }
            });
        panFooterLeft.add(btnBack);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        panButtons.add(panFooterLeft, gridBagConstraints);

        panFooterRight.setMaximumSize(new java.awt.Dimension(20, 40));
        panFooterRight.setMinimumSize(new java.awt.Dimension(20, 40));
        panFooterRight.setOpaque(false);
        panFooterRight.setPreferredSize(new java.awt.Dimension(20, 40));
        panFooterRight.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 5));

        btnForward.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/sudplan/linz/objectrenderer/arrow-right.png")));          // NOI18N
        btnForward.setBorderPainted(false);
        btnForward.setContentAreaFilled(false);
        btnForward.setFocusPainted(false);
        btnForward.setMaximumSize(new java.awt.Dimension(30, 30));
        btnForward.setMinimumSize(new java.awt.Dimension(30, 30));
        btnForward.setPreferredSize(new java.awt.Dimension(30, 30));
        btnForward.setPressedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/sudplan/linz/objectrenderer/arrow-right-pressed.png")));  // NOI18N
        btnForward.setRolloverIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/sudplan/linz/objectrenderer/arrow-right-selected.png"))); // NOI18N
        btnForward.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnForwardActionPerformed(evt);
                }
            });
        panFooterRight.add(btnForward);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        panButtons.add(panFooterRight, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        photosPanel.add(panButtons, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 5);
        panPage1.add(photosPanel, gridBagConstraints);

        add(panPage1, "CARD1");

        panPage2.setOpaque(false);
        panPage2.setLayout(new java.awt.GridBagLayout());

        swmmScenarioPanel.setLayout(new java.awt.GridBagLayout());

        panHeadInfoScenario.setBackground(new java.awt.Color(51, 51, 51));
        panHeadInfoScenario.setMinimumSize(new java.awt.Dimension(109, 24));
        panHeadInfoScenario.setPreferredSize(new java.awt.Dimension(109, 24));
        panHeadInfoScenario.setLayout(new java.awt.FlowLayout());

        lblSwmmScenarios.setForeground(new java.awt.Color(255, 255, 255));
        lblSwmmScenarios.setText(org.openide.util.NbBundle.getMessage(
                LinzCsoRenderer.class,
                "LinzCsoRenderer.lblSwmmScenarios.text")); // NOI18N
        panHeadInfoScenario.add(lblSwmmScenarios);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        swmmScenarioPanel.add(panHeadInfoScenario, gridBagConstraints);

        csoOverflowComparisionPanel.setPreferredSize(new java.awt.Dimension(400, 200));
        csoOverflowComparisionPanel.setLayout(new java.awt.GridLayout(1, 1));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        swmmScenarioPanel.add(csoOverflowComparisionPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.25;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panPage2.add(swmmScenarioPanel, gridBagConstraints);

        etaScenarioPanel.setLayout(new java.awt.GridBagLayout());

        panHeadInfoScenario1.setBackground(new java.awt.Color(51, 51, 51));
        panHeadInfoScenario1.setMinimumSize(new java.awt.Dimension(109, 24));
        panHeadInfoScenario1.setPreferredSize(new java.awt.Dimension(109, 24));
        panHeadInfoScenario1.setLayout(new java.awt.FlowLayout());

        lblEtaScenarios.setForeground(new java.awt.Color(255, 255, 255));
        lblEtaScenarios.setText(org.openide.util.NbBundle.getMessage(
                LinzCsoRenderer.class,
                "LinzCsoRenderer.lblEtaScenarios.text")); // NOI18N
        panHeadInfoScenario1.add(lblEtaScenarios);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        etaScenarioPanel.add(panHeadInfoScenario1, gridBagConstraints);

        scrollPane.setBorder(null);

        csoTotalOverflowComparisionPanel.setPreferredSize(new java.awt.Dimension(400, 200));
        csoTotalOverflowComparisionPanel.setLayout(new java.awt.GridLayout(1, 0));
        scrollPane.setViewportView(csoTotalOverflowComparisionPanel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        etaScenarioPanel.add(scrollPane, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.75;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panPage2.add(etaScenarioPanel, gridBagConstraints);

        add(panPage2, "CARD2");
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  wait  DOCUMENT ME!
     */
    private void showWait(final boolean wait) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("showWait:" + wait);
        }
        if (wait) {
            if (!lblBusy.isBusy()) {
                cardLayout.show(panCard, "busy");
                lblPicture.setIcon(null);
                lblBusy.setBusy(true);
                lstFotos.setEnabled(false);
                btnBack.setEnabled(false);
                btnForward.setEnabled(false);
            }
        } else {
            cardLayout.show(panCard, "preview");
            lblBusy.setBusy(false);
            lstFotos.setEnabled(true);
            defineButtonStatus();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  tooltip  DOCUMENT ME!
     */
    private void indicateError(final String tooltip) {
        LOG.error("indicateError:" + tooltip);
        lblPicture.setIcon(ERROR_ICON);
        lblPicture.setText("Fehler beim Übertragen des Bildes!");
        lblPicture.setToolTipText(tooltip);
    }

    /**
     * DOCUMENT ME!
     */
    private void loadFoto() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("loadFoto: " + lstFotos.getSelectedValue());
        }

        if (lstFotos.getSelectedValue() != null) {
            final String photo = lstFotos.getSelectedValue().toString();
            boolean cacheHit = false;
            if (photo != null) {
                final SoftReference<BufferedImage> cachedImageRef = IMAGE_CACHE.get(photo);
                if (cachedImageRef != null) {
                    final BufferedImage cachedImage = cachedImageRef.get();
                    if (cachedImage != null) {
                        cacheHit = true;
                        image = cachedImage;
                        showWait(true);
                        timer.restart();
                    }
                }
                if (!cacheHit) {
                    CismetThreadPool.execute(new LoadSelectedImageWorker(photo));
                }
            }
        } else {
            LOG.warn("cannot load photo: no photo selected!");
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lstFotosValueChanged(final javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstFotosValueChanged
//        if (isShowing()) {
        if (!evt.getValueIsAdjusting() && listListenerEnabled) {
            loadFoto();
        }
//        }
    }//GEN-LAST:event_lstFotosValueChanged

    /**
     * DOCUMENT ME!
     */
    public void defineButtonStatus() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("defineButtonStatus");
        }
        final int selectedIdx = lstFotos.getSelectedIndex();
        btnBack.setEnabled(selectedIdx > 0);
        btnForward.setEnabled((selectedIdx < (lstFotos.getModel().getSize() - 1)) && (selectedIdx > -1));
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnBackActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        lstFotos.setSelectedIndex(lstFotos.getSelectedIndex() - 1);
    }//GEN-LAST:event_btnBackActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnForwardActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnForwardActionPerformed
        lstFotos.setSelectedIndex(lstFotos.getSelectedIndex() + 1);
    }//GEN-LAST:event_btnForwardActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lblPrevPageMouseClicked(final java.awt.event.MouseEvent evt)//GEN-FIRST:event_lblPrevPageMouseClicked
    {//GEN-HEADEREND:event_lblPrevPageMouseClicked
        btnPrevPageActionPerformed(null);
    }//GEN-LAST:event_lblPrevPageMouseClicked

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnPrevPageActionPerformed(final java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnPrevPageActionPerformed
    {//GEN-HEADEREND:event_btnPrevPageActionPerformed
        ((CardLayout)this.getLayout()).show(this, CARD_1);
        btnPrevPage.setEnabled(false);
        btnNextPage.setEnabled(true);
        lblPrevPage.setEnabled(false);
        lblNextPage.setEnabled(true);
    }//GEN-LAST:event_btnPrevPageActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnNextPageActionPerformed(final java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnNextPageActionPerformed
    {//GEN-HEADEREND:event_btnNextPageActionPerformed

        ((CardLayout)this.getLayout()).show(this, CARD_2);
        btnPrevPage.setEnabled(true);
        btnNextPage.setEnabled(false);
        lblPrevPage.setEnabled(true);
        lblNextPage.setEnabled(false);
    }//GEN-LAST:event_btnNextPageActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lblNextPageMouseClicked(final java.awt.event.MouseEvent evt)//GEN-FIRST:event_lblNextPageMouseClicked
    {//GEN-HEADEREND:event_lblNextPageMouseClicked
        btnNextPageActionPerformed(null);
    }//GEN-LAST:event_lblNextPageMouseClicked

    /**
     * DOCUMENT ME!
     *
     * @param   originalFile  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String generateWebDAVFileName(final File originalFile) {
        final String[] fileNameSplit = originalFile.getName().split("\\.");
        String webFileName = "FOTO-" + System.currentTimeMillis() + "-" + Math.abs(originalFile.getName().hashCode());
        if (fileNameSplit.length > 1) {
            final String ext = fileNameSplit[fileNameSplit.length - 1];
            webFileName += "." + ext;
        }
        return webFileName;
    }

    @Override
    protected void init() {
        if ((cidsBean != null)) {
            this.lblNameText.setText(
                (cidsBean.getProperty("name") != null) ? cidsBean.getProperty("name").toString() : "");
            this.lblDescriptionText.setText(
                (cidsBean.getProperty("description") != null) ? cidsBean.getProperty("description").toString() : "");
            this.lblVolumeText.setText(
                (cidsBean.getProperty("volume") != null) ? cidsBean.getProperty("volume").toString() : "unknown");
            this.lblSwmmProjectText.setText(
                (cidsBean.getProperty("swmm_project_name") != null)
                    ? cidsBean.getProperty("swmm_project_name").toString() : "");

            if (cidsBean.getProperty("photos") != null) {
                final String[] photos = cidsBean.getProperty("photos").toString().split(";");
                final DefaultListModel model = new DefaultListModel();
                for (final String photo : photos) {
                    model.addElement(photo);
                }
                lstFotos.setModel(model);

                if (lstFotos.getModel().getSize() > 0) {
                    lstFotos.setSelectedIndex(0);
                } else {
                    cardLayout.show(panCard, "preview");
                }
            } else {
                LOG.warn("CSO has no photos assigned");
                lstFotos.setModel(new DefaultListModel());
            }

            if (cidsBean.getProperty("swmm_results") != null) {
                final Collection<CidsBean> swmmResults = (Collection)cidsBean.getProperty("swmm_results");
                this.csoOverflowComparisionPanel.setSwmmResults(swmmResults);
                this.csoTotalOverflowComparisionPanel.setSwmmResults(swmmResults);
            }

            defineButtonStatus();

            final GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.weighty = 0.0;
            gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 5);
            gridBagConstraints.fill = GridBagConstraints.NONE;

            final Object outfall = cidsBean.getProperty("outfall");          // NOI18N
            final Object storageUnit = cidsBean.getProperty("storage_unit"); // NOI18N
            final HashMap beansMap = new HashMap(2);
            linksListener = new LinksListener(beansMap);
            final DefaultComboBoxModel comboBoxModel = new DefaultComboBoxModel();

            if (outfall != null) {
                final CidsBean linkBean = (CidsBean)outfall;
                final String key = "OUTFALL::" + linkBean.getProperty("id");
                beansMap.put(key, linkBean);
                final JXHyperlink hyperLink = new JXHyperlink();
                hyperLink.setText((String)linkBean.getProperty("name")); // NOI18N
                hyperLink.setActionCommand(key);
                hyperLink.addActionListener(WeakListeners.create(
                        ActionListener.class,
                        linksListener,
                        hyperLink));

                this.linksPanel.add(hyperLink, gridBagConstraints);
            }

            if (storageUnit != null) {
                final CidsBean linkBean = (CidsBean)storageUnit;
                final String key = "STORAGE_UNIT::" + linkBean.getProperty("id");
                beansMap.put(key, linkBean);
                final JXHyperlink hyperLink = new JXHyperlink();
                hyperLink.setText((String)linkBean.getProperty("name")); // NOI18N
                hyperLink.setActionCommand(key);
                hyperLink.addActionListener(WeakListeners.create(
                        ActionListener.class,
                        linksListener,
                        hyperLink));

                gridBagConstraints.insets = new java.awt.Insets(5, 5, 10, 5);
                gridBagConstraints.gridy++;
                this.linksPanel.add(hyperLink, gridBagConstraints);
            }
        }
    }

    @Override
    public JComponent getTitleComponent() {
        return this.linzCsoTitleComponent;
    }

    @Override
    public void setTitle(final String title) {
        super.setTitle(title);
        // this.lblTitle.setText(title);
        // titleComponent.setTitle(title);
        this.linzCsoTitleComponent.setTitle(title);
    }

    @Override
    public Border getTitleBorder() {
        return null;
    }

    @Override
    public Border getFooterBorder() {
        return null;
    }

    @Override
    public Border getCenterrBorder() {
        return null;
    }

    @Override
    public JComponent getFooterComponent() {
        return panFooter;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    final class ImageResizeWorker extends SwingWorker<ImageIcon, Void> {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new ImageResizeWorker object.
         */
        public ImageResizeWorker() {
            // TODO image im EDT auslesen und final speichern!
            if (image != null) {
                lblPicture.setText("Wird neu skaliert...");
                lstFotos.setEnabled(false);
            }
//            LOG.fatal("RESIZE Image!", new Exception());
        }

        //~ Methods ------------------------------------------------------------

        @Override
        protected ImageIcon doInBackground() throws Exception {
            if (image != null) {
//                if (panButtons.getSize().getWidth() + 10 < panPreview.getSize().getWidth()) {
                // ImageIcon result = new ImageIcon(ImageUtil.adjustScale(image, panPreview, 20, 20));
                final ImageIcon result = new ImageIcon(ImageUtil.adjustScale(image, panCard, 20, 20));
                return result;
//                } else {
//                    return new ImageIcon(image);
//                }
            } else {
                return null;
            }
        }

        @Override
        protected void done() {
            if (!isCancelled()) {
                try {
                    resizeListenerEnabled = false;
                    final ImageIcon result = get();
                    lblPicture.setIcon(result);
                    lblPicture.setText("");
                    lblPicture.setToolTipText(null);
                } catch (InterruptedException ex) {
                    LOG.warn(ex, ex);
                } catch (ExecutionException ex) {
                    LOG.error(ex, ex);
                    lblPicture.setText("Fehler beim Skalieren!");
                } finally {
                    showWait(false);
                    if (currentResizeWorker == this) {
                        currentResizeWorker = null;
                    }
                    resizeListenerEnabled = true;
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    final class LoadSelectedImageWorker extends SwingWorker<BufferedImage, Void> {

        //~ Instance fields ----------------------------------------------------

        private final String file;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new LoadSelectedImageWorker object.
         *
         * @param  toLoad  DOCUMENT ME!
         */
        public LoadSelectedImageWorker(final String toLoad) {
            this.file = toLoad;
            lblPicture.setText("");
            lblPicture.setToolTipText(null);
            showWait(true);
        }

        //~ Methods ------------------------------------------------------------

        @Override
        protected BufferedImage doInBackground() throws Exception {
            if ((file != null) && (file.length() > 0)) {
                return downloadImageFromWebDAV(file);
            }
            return null;
        }

        @Override
        protected void done() {
            try {
                image = get();
                if (image != null) {
                    IMAGE_CACHE.put(file, new SoftReference<BufferedImage>(image));
                    timer.restart();
                } else {
                    indicateError("Bild konnte nicht geladen werden: Unbekanntes Bildformat");
                }
            } catch (InterruptedException ex) {
                image = null;
                LOG.warn(ex, ex);
            } catch (ExecutionException ex) {
                image = null;
                LOG.error(ex, ex);
                String causeMessage = "";
                final Throwable cause = ex.getCause();
                if (cause != null) {
                    causeMessage = cause.getMessage();
                }
                indicateError(causeMessage);
            } finally {
                if (image == null) {
                    showWait(false);
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class LinksListener implements ActionListener {

        //~ Instance fields ----------------------------------------------------

        final HashMap<String, CidsBean> beansMap;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new ScenarioListener object.
         *
         * @param  beansMap  DOCUMENT ME!
         */
        public LinksListener(final HashMap<String, CidsBean> beansMap) {
            this.beansMap = beansMap;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public void actionPerformed(final ActionEvent e) {
            if ((beansMap != null) && beansMap.containsKey(e.getActionCommand())) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("goto metaobject: " + e.getActionCommand());
                }
                ComponentRegistry.getRegistry()
                        .getDescriptionPane()
                        .gotoMetaObject(beansMap.get(e.getActionCommand()).getMetaObject(), null);
            } else {
                LOG.warn("beans map does not contain cids bean '" + e.getActionCommand() + "'");
            }
        }
    }
}
