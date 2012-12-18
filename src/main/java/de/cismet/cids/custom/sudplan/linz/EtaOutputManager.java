/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.linz;

import org.apache.log4j.Logger;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

import java.util.Collection;

import javax.swing.JComponent;

import de.cismet.cids.custom.sudplan.Manager;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.CidsBeanCollectionStore;

import de.cismet.cismap.commons.features.Feature;

/**
 * DOCUMENT ME!
 *
 * @author   pascal.dihe@cismet.de
 * @version  $Revision$, $Date$
 */
public class EtaOutputManager implements Manager, CidsBeanCollectionStore {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(EtaOutputManager.class);

    //~ Instance fields --------------------------------------------------------

    protected transient CidsBean modelOutputBean;
    protected transient volatile EtaOutputManagerUI ui;
    protected transient Collection<CidsBean> cidsBeans;

    //~ Methods ----------------------------------------------------------------

    @Override
    public EtaOutput getUR() throws IOException {
        final String json = (String)modelOutputBean.getProperty("ur"); // NOI18N
        final ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(json, EtaOutput.class);
    }

    @Override
    public void finalise() throws IOException {
        // not needed
    }

    @Override
    public Feature getFeature() throws IOException {
        return null;
    }

    @Override
    public CidsBean getCidsBean() {
        return modelOutputBean;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        this.modelOutputBean = cidsBean;
    }

    @Override
    public JComponent getUI() {
        if (ui == null) {
            synchronized (this) {
                if (ui == null) {
                    ui = new EtaOutputManagerUI(this);
                }
            }
        }
        return ui;
    }

    @Override
    public Collection<CidsBean> getCidsBeans() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("get cids beans");
        }
        return cidsBeans;
    }

    @Override
    public void setCidsBeans(final Collection<CidsBean> beans) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("set cids beans: " + beans.size());
        }
        this.cidsBeans = beans;
    }
}
