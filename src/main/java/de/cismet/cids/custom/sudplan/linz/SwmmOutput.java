/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.linz;

import org.apache.log4j.Logger;

import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.cismet.cids.custom.sudplan.SMSUtils;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   pascal
 * @version  $Revision$, $Date$
 */
public class SwmmOutput {

    //~ Static fields/initializers ---------------------------------------------

    protected static final transient Logger LOG = Logger.getLogger(SwmmOutput.class);
    public static final String TABLENAME_LINZ_CSO = "LINZ_CSO"; // NOI18N

    //~ Instance fields --------------------------------------------------------

    protected transient Map<String, CsoOverflow> csoOverflows = new HashMap<String, CsoOverflow>();
    protected transient Date created;
    protected transient String user;
    protected transient int swmmRun = -1;
    protected transient int swmmProject = -1;
    protected transient String swmmRunName;
    /** Statistical rainfall intensity with a duration of 12 h and return period once per year (r720,1). */
    protected transient float r720 = -1.0f;
    /** Total volume of surface runoff (Wet Weather Inflow), VQr. */
    protected transient float totalRunoffVolume = -1.0f;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new SwmmOutput object.
     */
    public SwmmOutput() {
    }

    //~ Methods ----------------------------------------------------------------

    // private transient csoParameters
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Map<String, CsoOverflow> getCsoOverflows() {
        return this.csoOverflows;
    }

    /**
     * Get the value of swmmRun.
     *
     * @return  the value of swmmRun
     */
    public int getSwmmRun() {
        return swmmRun;
    }

    /**
     * Set the value of swmmRun.
     *
     * @param  swmmRun  new value of swmmRun
     */
    public void setSwmmRun(final int swmmRun) {
        this.swmmRun = swmmRun;
    }

    /**
     * Get the value of swmmRunName.
     *
     * @return  the value of swmmRunName
     */
    public String getSwmmRunName() {
        return swmmRunName;
    }

    /**
     * Set the value of swmmRunName.
     *
     * @param  swmmRunName  new value of swmmRunName
     */
    public void setSwmmRunName(final String swmmRunName) {
        this.swmmRunName = swmmRunName;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  csoOverflows  DOCUMENT ME!
     */
    public void setCsoOverflows(final Map<String, CsoOverflow> csoOverflows) {
        this.csoOverflows = csoOverflows;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public List<CidsBean> fetchCsos() {
        assert this.csoOverflows != null : "csoOverflows list is null";
        final List<CidsBean> csoOverflowBeans = new ArrayList<CidsBean>(this.csoOverflows.size());
        for (final CsoOverflow csoOverflow : this.csoOverflows.values()) {
            csoOverflowBeans.add(SMSUtils.fetchCidsBean(csoOverflow.getCso(), TABLENAME_LINZ_CSO));
        }

        return csoOverflowBeans;
    }

    /**
     * Get the value of created.
     *
     * @return  the value of created
     */
    public Date getCreated() {
        return created;
    }

    /**
     * Set the value of created.
     *
     * @param  created  new value of created
     */
    public void setCreated(final Date created) {
        this.created = created;
    }

    /**
     * Get the value of user.
     *
     * @return  the value of user
     */
    public String getUser() {
        return user;
    }

    /**
     * Set the value of user.
     *
     * @param  user  new value of user
     */
    public void setUser(final String user) {
        this.user = user;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  projectId  DOCUMENT ME!
     */
    public void setSwmmProject(final int projectId) {
        this.swmmProject = projectId;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getSwmmProject() {
        return this.swmmProject;
    }

    /**
     * Get the value of r720 Niederschlagshöhe in mm bei einer Regendauer von 12 Stunden (720 Minuten) mit einer
     * Wiederkehrzeit von 1 Jahr.
     *
     * @return  the value of r720
     */
    public float getR720() {
        return r720;
    }

    /**
     * Set the value of r720.
     *
     * @param  r720  new value of r720
     */
    public void setR720(final float r720) {
        this.r720 = r720;
    }

    /**
     * Get the value of totalRunoffVolume Summe der Regenabflussmengen eines Jahres (m³/a) (Total volume of surface
     * runoff).
     *
     * @return  the value of totalRunoffVolume
     */
    public float getTotalRunoffVolume() {
        return totalRunoffVolume;
    }

    /**
     * Set the value of totalRunoffVolume.
     *
     * @param  totalRunoffVolume  new value of totalRunoffVolume
     */
    public void setTotalRunoffVolume(final float totalRunoffVolume) {
        this.totalRunoffVolume = totalRunoffVolume;
    }

    @JsonIgnore
    @Override
    public String toString() {
        return "SWMM Output for SWMM Run '" + this.getSwmmRunName() + "'";
    }

    /**
     * Synchronizes the overflow results (per cso) with the local ids of the cso objects. The id is retrieved from the
     * eta cso configuration.
     *
     * @param       etaConfigurations  csoOverflows DOCUMENT ME!
     *
     * @deprecated  DOCUMENT ME!
     */
    @JsonIgnore
    public void synchronizeCsoIds(final List<EtaConfiguration> etaConfigurations) {
        if ((this.csoOverflows != null) && !this.csoOverflows.isEmpty()) {
            if (this.csoOverflows.size() == etaConfigurations.size()) {
                for (final EtaConfiguration etaConfiguration : etaConfigurations) {
                    final String name = etaConfiguration.getName();
                    if (this.csoOverflows.containsKey(name)) {
                        this.csoOverflows.get(name).setCso(etaConfiguration.getCso());
                    } else {
                        LOG.warn("cso '" + name + "' not found in local cso map!");
                    }
                }
            } else {
                LOG.warn("CSO map size missmatch: " + this.csoOverflows.size()
                            + " vs. " + etaConfigurations.size());
            }
        } else {
            LOG.warn("target cso map empty!");
        }
    }
}
