/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.linz;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * DOCUMENT ME!
 *
 * @author   Pascal Dihé
 * @version  $Revision$, $Date$
 */
public class EtaConfiguration implements Comparable<EtaConfiguration> {

    //~ Instance fields --------------------------------------------------------

    protected transient boolean enabled = true;
    protected transient float sedimentationEfficency = 0.0f;
    protected transient String name;
    protected transient int cso = -1;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new EtaConfiguration object.
     */
    public EtaConfiguration() {
    }

    /**
     * Creates a new EtaConfiguration object.
     *
     * @param  csoName  DOCUMENT ME!
     * @param  csoId    DOCUMENT ME!
     */
    public EtaConfiguration(final String csoName, final int csoId) {
        this.name = csoName;
        this.cso = csoId;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Get the value of cso.
     *
     * @return  the value of cso
     */
    public int getCso() {
        return cso;
    }

    /**
     * Set the value of cso.
     *
     * @param  cso  new value of cso
     */
    public void setCso(final int cso) {
        this.cso = cso;
    }

    /**
     * Get the value of name.
     *
     * @return  the value of name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the value of name.
     *
     * @param  name  new value of name
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Get the value of sedimentationEfficency.
     *
     * @return  the value of sedimentationEfficency
     */
    public float getSedimentationEfficency() {
        return sedimentationEfficency;
    }

    /**
     * Set the value of sedimentationEfficency.
     *
     * @param  sedimentationEfficency  new value of sedimentationEfficency
     */
    public void setSedimentationEfficency(final float sedimentationEfficency) {
        this.sedimentationEfficency = sedimentationEfficency;
    }

    /**
     * Get the value of enabled.
     *
     * @return  the value of enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Set the value of enabled.
     *
     * @param  enabled  new value of enabled
     */
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    @JsonIgnore
    public String toString() {
        return this.getName();
    }

    @Override
    @JsonIgnore
    public int compareTo(final EtaConfiguration etaConfiguration) {
        if ((etaConfiguration.getName() == null) && (this.getName() == null)) {
            return 0;
        }
        if (this.getName() == null) {
            return 1;
        }
        if (etaConfiguration.getName() == null) {
            return -1;
        }
        return this.getName().compareTo(etaConfiguration.getName());
    }
}
