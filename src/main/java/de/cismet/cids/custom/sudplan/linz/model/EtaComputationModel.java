/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.linz.model;

import org.apache.log4j.Logger;

import java.util.Date;
import java.util.List;

import de.cismet.cids.custom.sudplan.linz.EtaConfiguration;
import de.cismet.cids.custom.sudplan.linz.EtaInput;
import de.cismet.cids.custom.sudplan.linz.EtaOutput;

/**
 * Local Model for ETA Calculation.
 *
 * @author   Pascal Dihé
 * @author   Jimmy Lauter
 * @version  $Revision$, $Date$
 */
public class EtaComputationModel {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(EtaComputationModel.class);
    private static final float WWTP_SIZE = 500000f;
    private static final float PE_SEP = 10000f;
    private static final float PE_COMBINED = 300000f;
    // Required CSO efficiency on table 1 and 2 at page 12 - Design basis of the WWTP (PE)
    private static final float WWTP_LOW_CASE = 5000.0f;
    private static final float WWTP_HEIGH_CASE = 50000.0f;
    // Required CSO efficiency on table 1 and 2 at page 12 - Rainfall intensity
    private static final float RAIN_INTENSITY_LOW_CASE = 30.0f;
    private static final float RAIN_INTENSITY_HEIGHT_CASE = 50.0f;
    // requirements for dissolved pollutants on table 1 at page 12
    private static final float R720_UNDER_30_LOW_CASE = 50.0f;
    private static final float R720_UNDER_30_HIGHT_CASE = 60.0f;
    private static final float R720_HIGHER_50_LOW_CASE = 40.0f;
    private static final float R720_HIGHER_50_HEIGHT_CASE = 50.0f;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new EtaComputationModel object.
     */
    public EtaComputationModel() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   etaInput  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public EtaOutput computateEta(final EtaInput etaInput) throws Exception {
        // definitions
        final float r720_1 = etaInput.getR720();
        final float wwtp_size = WWTP_SIZE;
        final float PEsep = PE_SEP;
        final float PEcombined = PE_COMBINED;

        // eta configuration conteins the sedomentation efficiency
        final List<EtaConfiguration> etaConfigurations = etaInput.getEtaConfigurations();
        LOG.info("computating ETA values for SWMM Result '" + etaInput.getSwmmRunName() + "'");

        final EtaOutput etaOutput = new EtaOutput();
        etaOutput.setCreated(new Date());
        etaOutput.setSwmmRun(etaInput.getSwmmRun());
        etaOutput.setUser(etaInput.getUser());
        etaOutput.setR720(etaInput.getR720());

        if (etaInput.getTotalOverflowVolume() <= 0) {
            LOG.warn("TotalOverflowVolume not set in ETA inout, trying to compute ....");
            etaInput.computeTotalOverflowVolume();
        }

        etaOutput.setTotalOverflowVolume(etaInput.getTotalOverflowVolume());

        float sum_TotalVolume = 0.0f;
        float sum_SedAFS = 0.0f;

        for (final String rptKey : etaInput.getCsoOverflows().keySet()) {
            for (final EtaConfiguration eta : etaConfigurations) {
                if (rptKey.equalsIgnoreCase(eta.getName())) {
                    if (eta.isEnabled()) {
                        final float totVol = etaInput.getCsoOverflows().get(rptKey).getOverflowVolume();
                        sum_SedAFS += totVol * eta.getSedimentationEfficency();
                        sum_TotalVolume += totVol;
                    } else {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("ignoring CSO '" + eta + "' in ETA computation");
                        }
                    }
                }
            }
        }

        if (sum_TotalVolume != etaInput.getTotalOverflowVolume()) {
            LOG.warn("computed TotalOverflowVolume (" + sum_TotalVolume + ") "
                        + "!= stored TotalOverflowVolume (" + etaInput.getTotalOverflowVolume() + ")");
        }

        final float VQo = etaInput.getTotalOverflowVolume();
        float VQr;

        if (etaInput.getTotalRunoffVolume() > 1E6f) {
            VQr = etaInput.getTotalRunoffVolume() / 1E6f;
            LOG.warn("TotalRunoffVolume changed from " + etaInput.getTotalRunoffVolume()
                        + " to " + VQr);
        } else {
            VQr = etaInput.getTotalRunoffVolume();
        }

        final float eta_Hyd_actual = ((VQr - VQo) / VQr) * 100.0f;
        final float eta_Sed_actual = eta_Hyd_actual + (sum_SedAFS / VQr);
        if (LOG.isDebugEnabled()) {
            LOG.debug("r720,1=" + etaInput.getR720() + ", \nVQo=" + etaInput.getTotalOverflowVolume()
                        + "*10^6 ltr, \nVQr=" + etaInput.getTotalRunoffVolume() + "*10^6 ltr");
            LOG.debug("eta_Hyd_actual=" + eta_Hyd_actual
                        + ", \neta_Sed_actual=" + eta_Sed_actual);
        }

        // Calculate required efficiency rates

        /**
         * todo comment
         */
        float cso_eff_r720_lower30mm;
        /**
         * todo comment
         */
        float cso_eff_r720_higher50mm;
        /**
         * todo comment
         */
        float eta_gel;
        /**
         * todo comment
         */
        float eta_afs;

        // Table 1 - Page 12
        if (wwtp_size <= WWTP_LOW_CASE) {
            cso_eff_r720_lower30mm = R720_UNDER_30_LOW_CASE;
            cso_eff_r720_higher50mm = R720_HIGHER_50_LOW_CASE;
        } else if (wwtp_size >= WWTP_HEIGH_CASE) {
            cso_eff_r720_lower30mm = R720_UNDER_30_HIGHT_CASE;
            cso_eff_r720_higher50mm = R720_HIGHER_50_HEIGHT_CASE;
        } else {
            cso_eff_r720_lower30mm = R720_UNDER_30_LOW_CASE
                        + ((R720_UNDER_30_HIGHT_CASE - R720_UNDER_30_LOW_CASE)
                            / (WWTP_HEIGH_CASE - WWTP_LOW_CASE)
                            * (wwtp_size - WWTP_LOW_CASE));
            cso_eff_r720_higher50mm = R720_HIGHER_50_LOW_CASE
                        + ((R720_HIGHER_50_HEIGHT_CASE - R720_HIGHER_50_LOW_CASE)
                            / (WWTP_HEIGH_CASE - WWTP_LOW_CASE)
                            * (wwtp_size - WWTP_LOW_CASE));
        }

        if (r720_1 <= RAIN_INTENSITY_LOW_CASE) {
            eta_gel = cso_eff_r720_lower30mm;
        } else if (r720_1 >= RAIN_INTENSITY_HEIGHT_CASE) {
            eta_gel = cso_eff_r720_higher50mm;
        } else {
            eta_gel = cso_eff_r720_higher50mm
                        + ((cso_eff_r720_lower30mm - cso_eff_r720_higher50mm)
                            / (RAIN_INTENSITY_HEIGHT_CASE - RAIN_INTENSITY_LOW_CASE)
                            * (RAIN_INTENSITY_HEIGHT_CASE - r720_1));
        }

        // Table 2 - Page 12
        eta_afs = eta_gel + 15;

        // Increase required efficiency for connected seperate systems
        final float seperateSYS = 5.0f * PEsep / PEcombined;
        if (seperateSYS > 1) {
            if ((eta_gel + seperateSYS) > 65.0f) {
                eta_gel = 65.0f;
            } else {
                eta_gel = eta_gel + seperateSYS;
            }
            if ((eta_afs + seperateSYS) > 80.0f) {
                eta_afs = 80.0f;
            } else {
                eta_afs = eta_afs + seperateSYS;
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("eta_Hyd_required=" + eta_gel
                        + ", \neta_Sed_required=" + eta_afs);
        }

        etaOutput.setEtaHydActual(eta_Hyd_actual);
        etaOutput.setEtaSedActual(eta_Sed_actual);
        etaOutput.setEtaHydRequired(eta_gel);
        etaOutput.setEtaSedRequired(eta_afs);

        return etaOutput;
    }
}
