/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.linz;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

import javax.swing.JComponent;

import de.cismet.cids.custom.sudplan.Manager;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cismap.commons.features.Feature;

/**
 * DOCUMENT ME!
 *
 * @author   pascal.dihe@cismet.de
 * @version  $Revision$, $Date$
 */
public final class EtaInputManager implements Manager {

    //~ Instance fields --------------------------------------------------------

    protected transient CidsBean modelInputBean;
    protected transient volatile EtaInputManagerUI ui;

    //~ Methods ----------------------------------------------------------------

    @Override
    public EtaInput getUR() throws IOException {
        final String json = (String)this.modelInputBean.getProperty("ur"); // NOI18N
        final ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(json, EtaInput.class);
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
        return modelInputBean;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        this.modelInputBean = cidsBean;
    }

    @Override
    public JComponent getUI() {
        if (ui == null) {
            synchronized (this) {
                if (ui == null) {
                    ui = new EtaInputManagerUI(this);
                }
            }
        }

        return ui;
    }
}
