/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.linz.converter;

import org.apache.log4j.Logger;

import org.openide.util.NbBundle;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.Collection;
import java.util.SortedMap;

import de.cismet.cids.custom.sudplan.IDFCurve;
import de.cismet.cids.custom.sudplan.converter.ConversionException;
import de.cismet.cids.custom.sudplan.converter.FormatHint;
import de.cismet.cids.custom.sudplan.converter.IDFConverter;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class LinzIDFConverter implements IDFConverter, FormatHint {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(LinzIDFConverter.class);

    //~ Methods ----------------------------------------------------------------

    @Override
    public IDFCurve convertForward(final InputStream from, final String... params) throws ConversionException {
        try {
            final IDFCurve curve = new IDFCurve();
            final BufferedReader r = new BufferedReader(new InputStreamReader(from));
            String line;
            while ((line = r.readLine()) != null) {
                if (line.startsWith("#")) { // NOI18N
                    // comments, ignore
                    continue;
                }

                if (line.trim().isEmpty()) {
                    // separating line, ignore
                    continue;
                }

                final String[] split = line.trim().split("\\s+");      // NOI18N
                if (split.length != 3) {
                    throw new IllegalStateException("illegal format"); // NOI18N
                }

                curve.add(Integer.valueOf(split[0]), Integer.valueOf(split[1]), Double.valueOf(split[2]));
            }

            return curve;
        } catch (final Exception ex) {
            final String message = "cannot convert idf data"; // NOI18N
            LOG.error(message, ex);
            throw new ConversionException(message, ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   to      DOCUMENT ME!
     * @param   params  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ConversionException  DOCUMENT ME!
     */
    @Override
    public InputStream convertBackward(final IDFCurve to, final String... params) throws ConversionException {
        final String lineSep = System.getProperty("line.separator"); // NOI18N
        final char sep = '\t';

        final StringBuilder sb = new StringBuilder("#Duration;Frequency;Intensity"); // NOI18N
        sb.append(lineSep);
        sb.append("#minutes;years;mm/h");                                            // NOI18N
        sb.append(lineSep);

        try {
            final SortedMap<Integer, SortedMap<Integer, Double>> data = to.getData();
            final Collection<Integer> durations = data.keySet();
            final Collection<Integer> frequencies = to.getFrequencies();

            for (final Integer freq : frequencies) {
                for (final Integer dura : durations) {
                    sb.append(dura).append(sep);
                    sb.append(freq).append(sep);
                    sb.append(data.get(dura).get(freq)).append(lineSep);
                }

                sb.append(lineSep).append(lineSep);
            }

            // there may be encoding issues
            return new ByteArrayInputStream(sb.toString().getBytes());
        } catch (final Exception e) {
            final String message = "cannot convert idf data"; // NOI18N
            LOG.error(message, e);
            throw new ConversionException(message, e);
        }
    }

    @Override
    public String toString() {
        return getFormatDisplayName();
    }

    @Override
    public String getFormatName() {
        return "linz-idf-converter"; // NOI18N
    }

    @Override
    public String getFormatDisplayName() {
        return NbBundle.getMessage(LinzIDFConverter.class, "LinzIDFConverter.getFormatDisplayName().description"); // NOI18N
    }

    @Override
    public String getFormatHtmlName() {
        return null;
    }

    @Override
    public String getFormatDescription() {
        return NbBundle.getMessage(LinzIDFConverter.class, "LinzIDFConverter.getFormatDescription().description"); // NOI18N
    }

    @Override
    public String getFormatHtmlDescription() {
        return NbBundle.getMessage(LinzIDFConverter.class, "LinzIDFConverter.getFormatHtmlDescription().description"); // NOI18N
    }

    @Override
    public Object getFormatExample() {
        return NbBundle.getMessage(LinzIDFConverter.class, "LinzIDFConverter.getFormatExample().description"); // NOI18N
    }
}
