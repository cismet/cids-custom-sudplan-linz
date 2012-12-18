/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.linz;

import org.codehaus.jackson.annotate.JsonIgnore;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import de.cismet.cids.custom.sudplan.SMSUtils;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   pd
 * @version  $Revision$, $Date$
 */
public class CsoOverflow implements Comparable<CsoOverflow> {

    //~ Static fields/initializers ---------------------------------------------

    public static final String PROP_SWMMPROJECT = "swmmProject";

    //~ Instance fields --------------------------------------------------------

    protected transient float overflowVolume = 0.0f;
    protected transient float overflowFrequency = 0.0f;
    protected transient float overflowDuration = 0.0f;
    protected transient int cso = -1;
    protected transient String name;
    private int swmmProject = -1;
    private final transient PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new CsoOverflow object.
     */
    public CsoOverflow() {
    }

    /**
     * Creates a new CsoOverflow object.
     *
     * @param  name               DOCUMENT ME!
     * @param  overflowVolume     DOCUMENT ME!
     * @param  overflowFrequency  DOCUMENT ME!
     * @param  overflowDuration   DOCUMENT ME!
     */
    public CsoOverflow(final String name,
            final float overflowVolume,
            final float overflowFrequency,
            final float overflowDuration) {
        this(name, overflowVolume, overflowFrequency, overflowDuration, -1, -1);
    }

    /**
     * Creates a new CsoOverflow object.
     *
     * @param  name               DOCUMENT ME!
     * @param  overflowVolume     DOCUMENT ME!
     * @param  overflowFrequency  DOCUMENT ME!
     * @param  overflowDuration   DOCUMENT ME!
     * @param  cso                DOCUMENT ME!
     * @param  swmmProject        DOCUMENT ME!
     */
    public CsoOverflow(final String name,
            final float overflowVolume,
            final float overflowFrequency,
            final float overflowDuration,
            final int cso,
            final int swmmProject) {
        this.name = name;
        this.overflowVolume = overflowVolume;
        this.overflowFrequency = overflowFrequency;
        this.overflowDuration = overflowDuration;
        this.cso = cso;
        this.swmmProject = swmmProject;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Get the value of overflowVolume.
     *
     * @return  the value of overflowVolume
     */
    public float getOverflowVolume() {
        return overflowVolume;
    }

    /**
     * Set the value of overflowVolume.
     *
     * @param  volume  overflowVolume new value of overflowVolume
     */
    public void setOverflowVolume(final float volume) {
        this.overflowVolume = volume;
    }

    /**
     * Get the value of overflowFrequency.
     *
     * @return  the value of overflowFrequency
     */
    public float getOverflowFrequency() {
        return overflowFrequency;
    }

    /**
     * Set the value of overflowFrequency.
     *
     * @param  frequency  new value of overflowFrequency
     */
    public void setOverflowFrequency(final float frequency) {
        this.overflowFrequency = frequency;
    }

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
     * @param  node  name new value of name
     */
    public void setName(final String node) {
        this.name = node;
    }

    /**
     * Get the value of overflowDuration.
     *
     * @return  the value of overflowDuration
     */
    public float getOverflowDuration() {
        return overflowDuration;
    }

    /**
     * Set the value of overflowDuration.
     *
     * @param  overflowDuration  new value of overflowDuration
     */
    public void setOverflowDuration(final float overflowDuration) {
        this.overflowDuration = overflowDuration;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean fetchCso() {
        return SMSUtils.fetchCidsBean(this.getCso(), SwmmOutput.TABLENAME_LINZ_CSO);
    }

    /**
     * Get the value of swmmProject.
     *
     * @return  the value of swmmProject
     */
    public int getSwmmProject() {
        return swmmProject;
    }

    /**
     * Set the value of swmmProject.
     *
     * @param  swmmProject  new value of swmmProject
     */
    public void setSwmmProject(final int swmmProject) {
        final int oldSwmmProject = this.swmmProject;
        this.swmmProject = swmmProject;
        propertyChangeSupport.firePropertyChange(PROP_SWMMPROJECT, oldSwmmProject, swmmProject);
    }

    /**
     * Add PropertyChangeListener.
     *
     * @param  listener  DOCUMENT ME!
     */
    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove PropertyChangeListener.
     *
     * @param  listener  DOCUMENT ME!
     */
    public void removePropertyChangeListener(final PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    @Override
    @JsonIgnore
    public String toString() {
        return this.getName();
    }

    @Override
    @JsonIgnore
    public int compareTo(final CsoOverflow csoOverflow) {
        if ((csoOverflow.getName() == null) && (this.getName() == null)) {
            return 0;
        }
        if (this.getName() == null) {
            return 1;
        }
        if (csoOverflow.getName() == null) {
            return -1;
        }
        return this.getName().compareTo(csoOverflow.getName());
    }
}
