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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Input for the ETA Calculation is the output of the SWMM Model Run!
 *
 * @author   Pascal Dihé
 * @version  $Revision$, $Date$
 */
public class EtaInput extends SwmmOutput {

    //~ Static fields/initializers ---------------------------------------------

    protected static final transient Logger LOG = Logger.getLogger(EtaInput.class);
    public static final String PROP_SWMMRUN = "swmmRun";

    //~ Instance fields --------------------------------------------------------

    protected transient List<EtaConfiguration> etaConfigurations;
    protected String etaFile;
    /** Total volume of overflow discharge. (VQo) */
    private transient float totalOverflowVolume = -1.0f;
    private final transient PropertyChangeSupport propertyChangeSupport;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new EtaInput object.
     */
    public EtaInput() {
        etaConfigurations = new ArrayList<EtaConfiguration>();
        propertyChangeSupport = new PropertyChangeSupport(this);
    }

    /**
     * Creates a new EtaInput object.
     *
     * @param  swmmOutput  DOCUMENT ME!
     */
    public EtaInput(final SwmmOutput swmmOutput) {
        this();
        this.fromSwmmOutput(swmmOutput);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  swmmOutput  DOCUMENT ME!
     */
    @JsonIgnore
    public final void fromSwmmOutput(final SwmmOutput swmmOutput) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("initialising ETA Input from SWMM Output '" + swmmOutput + "'");
        }
        this.setR720(swmmOutput.getR720());
        this.setSwmmProject(swmmOutput.getSwmmProject());
        this.setSwmmRun(swmmOutput.getSwmmRun());
        this.setSwmmRunName(swmmOutput.getSwmmRunName());
        this.setTotalRunoffVolume(swmmOutput.getTotalRunoffVolume());
        this.setCsoOverflows(swmmOutput.getCsoOverflows());

        if (this.etaConfigurations.isEmpty()) {
            LOG.warn("no ETA Configurations found, creating default ETA Configurations from CSO Overflows");
            for (final CsoOverflow csoOverflow : this.csoOverflows.values()) {
                final EtaConfiguration etaConfiguration = new EtaConfiguration(csoOverflow.getName(),
                        csoOverflow.getCso());
                this.etaConfigurations.add(etaConfiguration);
            }
            Collections.sort(this.etaConfigurations);
            this.resetToDefaults();
        }

        this.computeTotalOverflowVolume();
    }

    /**
     * DOCUMENT ME!
     */
    @JsonIgnore
    public final void computeTotalOverflowVolume() {
        if ((this.csoOverflows != null) && !csoOverflows.isEmpty()
                    && (etaConfigurations != null) && !etaConfigurations.isEmpty()) {
        } else {
            LOG.warn("cannot compute TotalOverflowVolume, csoOverflows or etaConfigurations are empty");
            this.totalOverflowVolume = -1;
            return;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("computing total overflow volume for " + csoOverflows.size() + " CSOs "
                        + "and " + etaConfigurations.size() + " ETA Configurations");
        }
        if (this.csoOverflows.size() != etaConfigurations.size()) {
            LOG.warn("CSO map size missmatch: " + this.getCsoOverflows().size()
                        + " CSOs vs. " + etaConfigurations.size() + " ETA Configurations!");
        }

        this.totalOverflowVolume = 0;
        int i = 0;
        for (final EtaConfiguration etaConfiguration : etaConfigurations) {
            if (etaConfiguration.isEnabled()) {
                final String name = etaConfiguration.getName();
                if (this.csoOverflows.containsKey(name)) {
                    final CsoOverflow csoOverflow = this.csoOverflows.get(name);
                    totalOverflowVolume += csoOverflow.getOverflowVolume();
                    i++;
                } else {
                    LOG.warn("cannot consider Overflow Volume of cso '"
                                + etaConfiguration.getName() + "': not in result list of overflows");
                }
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("ignoring CSO '" + etaConfiguration + "' in computation of total overflow volume");
                }
            }
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug(i + " out of " + getEtaConfigurations().size()
                        + " CSOs considered in total overflow volume (" + totalOverflowVolume + ") calculation");
        }
    }
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public List<EtaConfiguration> getEtaConfigurations() {
        return this.etaConfigurations;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  etaConfigurations  DOCUMENT ME!
     */
    public void setEtaConfigurations(final List<EtaConfiguration> etaConfigurations) {
        this.etaConfigurations = etaConfigurations;
        Collections.sort(this.etaConfigurations);

        if ((this.csoOverflows != null) && !this.csoOverflows.isEmpty()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("etaConfigurations changed, re-computing total overflow volume");
            }
            this.computeTotalOverflowVolume();
        }
    }

    /**
     * Get the value of totalOverflowVolume Summe der entlasteten Mischwassermengen eines Jahres (m³/a) (Total volume of
     * overflow discharge).
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
     * Get the value of etaFile.
     *
     * @return      the value of etaFile
     *
     * @deprecated  DOCUMENT ME!
     */
    @JsonIgnore
    public String getEtaFile() {
        return etaFile;
    }

    /**
     * Set the value of etaFile.
     *
     * @param       etaFile  new value of etaFile
     *
     * @deprecated  DOCUMENT ME!
     */
    @JsonIgnore
    public void setEtaFile(final String etaFile) {
        this.etaFile = etaFile;
    }

    /**
     * Add PropertyChangeListener.
     *
     * @param  listener  DOCUMENT ME!
     */
    @JsonIgnore
    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove PropertyChangeListener.
     *
     * @param  listener  DOCUMENT ME!
     */
    @JsonIgnore
    public void removePropertyChangeListener(final PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    @JsonIgnore
    @Override
    public String toString() {
        return "ETA Input for SWMM Run '" + this.getSwmmRunName() + "'";
    }

    /**
     * DOCUMENT ME!
     */
    @JsonIgnore
    public void resetToDefaults() {
        if ((this.etaConfigurations != null) && !this.etaConfigurations.isEmpty()) {
            if (LOG.isDebugEnabled()) {
                LOG.warn("resetting " + this.etaConfigurations.size() + " ETA Configurations of '" + this
                            + "' to defaults");
            }
            for (final EtaConfiguration etaConfiguration : etaConfigurations) {
                if (etaConfiguration.getName().equalsIgnoreCase("RKL_Ablauf")) {
                    etaConfiguration.setEnabled(false);
                }

                if (etaConfiguration.getName().equalsIgnoreCase("ULKS1")
                            || etaConfiguration.getName().equalsIgnoreCase("AB_Plesching")
                            || etaConfiguration.getName().equalsIgnoreCase("ALKSP1nolink")
                            || etaConfiguration.getName().equalsIgnoreCase("ANFSP1nolink")
                            || etaConfiguration.getName().equalsIgnoreCase("EDBSP1nolink")
                            || etaConfiguration.getName().equalsIgnoreCase("ENNSP1nolink")
                            || etaConfiguration.getName().equalsIgnoreCase("ENNSP2nolink")
                            || etaConfiguration.getName().equalsIgnoreCase("EWDSP1nolink")
                            || etaConfiguration.getName().equalsIgnoreCase("FKDSP1nolink")
                            || etaConfiguration.getName().equalsIgnoreCase("GLWSP1nolink")
                            || etaConfiguration.getName().equalsIgnoreCase("GRSSP2nolink")
                            || etaConfiguration.getName().equalsIgnoreCase("HEMSP1nolink")
                            || etaConfiguration.getName().equalsIgnoreCase("HZDSP1nolink")
                            || etaConfiguration.getName().equalsIgnoreCase("KRTSP1nolink")
                            || etaConfiguration.getName().equalsIgnoreCase("NNKSP1nolink")
                            || etaConfiguration.getName().equalsIgnoreCase("OTHSP1nolink")
                            || etaConfiguration.getName().equalsIgnoreCase("PNASP1nolink")
                            || etaConfiguration.getName().equalsIgnoreCase("RHHB_Weikerlsee3nolink")
                            || etaConfiguration.getName().equalsIgnoreCase("RUEB_Traunnolink")
                            || etaConfiguration.getName().equalsIgnoreCase("SMMSP1nolink")
                            || etaConfiguration.getName().equalsIgnoreCase("STYSP1nolink")
                            || etaConfiguration.getName().equalsIgnoreCase("ULKS1")
                            || etaConfiguration.getName().equalsIgnoreCase("WLDSP1nolink")
                            || etaConfiguration.getName().equalsIgnoreCase("WLDSP2nolink")
                            || etaConfiguration.getName().equalsIgnoreCase("WLGSP1nolink")) {
                    etaConfiguration.setSedimentationEfficency(25.0f);
                } else {
                    etaConfiguration.setSedimentationEfficency(0.0f);
                }
            }
        } else {
            LOG.warn("could not reset ETA Configurations of '" + this + "' to defaults, no ETA Configurations found!");
        }
    }
}
