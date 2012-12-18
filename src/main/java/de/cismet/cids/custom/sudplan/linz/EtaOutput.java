/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.linz;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.ObjectMapper;

import org.openide.util.Exceptions;

import java.io.IOException;
import java.io.StringWriter;

import java.util.Date;

/**
 * This is the output of the ETA (efficency rates) calculation.
 *
 * @author   pd
 * @version  $Revision$, $Date$
 */
public class EtaOutput {

    //~ Instance fields --------------------------------------------------------

    /**
     * Statistische Niederschlagsintensität (in mm/12h) mit einer Dauerstufe von 720 Minuten (12h) und einer
     * Wiederkehrperiode von 1 Jahr Engl: „statistical rainfall intensity with a duration of 12 h and return period once
     * per year (r720,1)”
     */
    private transient float r720 = -1;
    /**
     * Mindestwirkungsgrad (der Weiterleitung) für gelöste Stoffe (required CSO efficiency for dissolved pollutants),
     * definiert im ÖWAV Regelblatt 19. r720_1 in SWMM Output / ETA Input
     */
    private transient float etaHydRequired = -1;
    /**
     * Mindestwirkungsgrad (der Weiterleitung) für abfiltrierbare Stoffe (required CSO efficiency for particulate
     * pollutants), definiert im ÖWAV Regelblatt 19.
     */
    private transient float etaSedRequired = -1;
    /**
     * Vom Modell berechneter Wirkungsgrad (der Weiterleitung) für gelöste Stoffe (CSO efficiency for dissolved
     * pollutants).
     */
    private transient float etaHydActual = -1;
    /**
     * Vom Modell berechnete Wirkungsgrad (der Weiterleitung) für abfiltrierbare Stoffe (CSO efficiency for particulate
     * pollutants).
     */
    private transient float etaSedActual = -1;
    /** Total overflow volume in system. (m³/a) * 10^6 VQo in SWMM Output / ETA Input */
    private transient float totalOverflowVolume = -1;
    private transient Date created;
    private transient String user;
    private transient int swmmRun = -1;
    private transient String etaRunName;
    private transient int etaRun = -1;

    //~ Methods ----------------------------------------------------------------

    /**
     * Get the value of totalOverflowVolume.
     *
     * @return  the value of totalOverflowVolume
     */
    public float getTotalOverflowVolume() {
        return totalOverflowVolume;
    }

    /**
     * Set the value of totalOverflowVolume.
     *
     * @param  totalOverflowVolume  new value of totalOverflowVolume
     */
    public void setTotalOverflowVolume(final float totalOverflowVolume) {
        this.totalOverflowVolume = totalOverflowVolume;
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
     * Get the value of etaRunName.
     *
     * @return  the value of etaRunName
     */
    public String getEtaRunName() {
        return etaRunName;
    }

    /**
     * Set the value of etaRunName.
     *
     * @param  etaRunName  new value of etaRunName
     */
    public void setEtaRunName(final String etaRunName) {
        this.etaRunName = etaRunName;
    }

    /**
     * Get the value of etaRun.
     *
     * @return  the value of etaRun
     */
    public int getEtaRun() {
        return etaRun;
    }

    /**
     * Set the value of etaRun.
     *
     * @param  etaRun  new value of etaRun
     */
    public void setEtaRun(final int etaRun) {
        this.etaRun = etaRun;
    }

    /**
     * Get the value of r720.
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
     * Get the value of etaHydRequired.
     *
     * @return  the value of etaHydRequired
     */
    public float getEtaHydRequired() {
        return etaHydRequired;
    }

    /**
     * Set the value of etaHydRequired.
     *
     * @param  etaHydRequired  new value of etaHydRequired
     */
    public void setEtaHydRequired(final float etaHydRequired) {
        this.etaHydRequired = etaHydRequired;
    }

    /**
     * Get the value of etaSedRequired.
     *
     * @return  the value of etaSedRequired
     */
    public float getEtaSedRequired() {
        return etaSedRequired;
    }

    /**
     * Set the value of etaSedRequired.
     *
     * @param  etaSedRequired  new value of etaSedRequired
     */
    public void setEtaSedRequired(final float etaSedRequired) {
        this.etaSedRequired = etaSedRequired;
    }

    /**
     * Get the value of etaHydActual.
     *
     * @return  the value of etaHydActual
     */
    public float getEtaHydActual() {
        return etaHydActual;
    }

    /**
     * Set the value of etaHydActual.
     *
     * @param  etaHydActual  new value of etaHydActual
     */
    public void setEtaHydActual(final float etaHydActual) {
        this.etaHydActual = etaHydActual;
    }

    /**
     * Get the value of etaSedActual.
     *
     * @return  the value of etaSedActual
     */
    public float getEtaSedActual() {
        return etaSedActual;
    }

    /**
     * Set the value of etaSedActual.
     *
     * @param  etaSedActual  new value of etaSedActual
     */
    public void setEtaSedActual(final float etaSedActual) {
        this.etaSedActual = etaSedActual;
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

    @JsonIgnore
    @Override
    public String toString() {
        return "ETA Output for ETA Run '" + this.getEtaRunName() + "'";
    }
}
