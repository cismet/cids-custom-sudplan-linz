/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.linz.converter;

import at.ac.ait.enviro.sudplan.util.PropertyNames;
import at.ac.ait.enviro.tsapi.timeseries.TimeSeries;
import at.ac.ait.enviro.tsapi.timeseries.TimeStamp;
import at.ac.ait.enviro.tsapi.timeseries.impl.TimeSeriesImpl;

import org.apache.log4j.Logger;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.math.RoundingMode;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Locale;

import de.cismet.cids.custom.sudplan.converter.ConversionException;
import de.cismet.cids.custom.sudplan.converter.Converter;
import de.cismet.cids.custom.sudplan.converter.TimeseriesConverter;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
@ServiceProvider(service = Converter.class)
public final class LinzNetcdfConverter implements TimeseriesConverter {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(LinzNetcdfConverter.class);
    private static final transient String NAN = "nan";
    private static final transient String NEG_INF = "-Inf";
    private static final transient DateFormat DATEFORMAT;
    private static final transient DateFormat TS_DATEFORMAT;
    private static final transient NumberFormat NUMBERFORMAT;

    static {
        NUMBERFORMAT = NumberFormat.getInstance(Locale.US);
        NUMBERFORMAT.setMaximumFractionDigits(1);
        NUMBERFORMAT.setMinimumFractionDigits(1);
        NUMBERFORMAT.setRoundingMode(RoundingMode.HALF_UP);

        DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // NOI18N
        TS_DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

        TS_DATEFORMAT.setTimeZone(UTC_TIME_ZONE);
        DATEFORMAT.setTimeZone(UTC_TIME_ZONE);
    }

    //~ Instance fields --------------------------------------------------------

    private final boolean retrieveAllValues;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new LinzNetcdfConverter object.
     */
    public LinzNetcdfConverter() {
        this(false);
    }

    /**
     * If retrieveAllValues is set to true, the converter will retrieve all values from the source, not only the values
     * from the 2nd column.
     *
     * @param  retrieveAllValues  retrieveAllValues DOCUMENT ME!
     */
    public LinzNetcdfConverter(final boolean retrieveAllValues) {
        this.retrieveAllValues = retrieveAllValues;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   from    DOCUMENT ME!
     * @param   params  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ConversionException  DOCUMENT ME!
     */
    @Override
    public TimeSeries convertForward(final InputStream from, final String... params) throws ConversionException {
        final BufferedReader br;
        try {
            if (params.length != 2) {
                final String message =
                    "Wrong number of parameters, exepected two parameter: offering and observed property";
                LOG.error(message);
                throw new ConversionException(message);
            }

            final String offering = params[0];
            final String offeringMetaData = offering + ".json";
            final String observedProperty = params[1];
            final boolean isEvent = offering.toLowerCase().contains("events");

            LOG.info("importing time series for '" + observedProperty + "' from '" + offering + "' (events="
                        + isEvent + ", allValues=" + this.retrieveAllValues + ")");

            final TimeSeriesImpl timeSeries = new TimeSeriesImpl();
            final JsonFactory jfactory = new JsonFactory();
            final JsonParser jParser = jfactory.createJsonParser(this.getClass().getResourceAsStream(offeringMetaData));
            if (LOG.isDebugEnabled()) {
                LOG.debug("Meta Data loaded from '"
                            + this.getClass().getResource(offeringMetaData) + "'");
            }

            final ObjectMapper mapper = new ObjectMapper();
            final JsonNode rootNode = mapper.readTree(jParser);

            final JsonNode children = rootNode.get("opensdm:children");
            final Iterator<JsonNode> childrenIterator = children.iterator();

            String[] VALUE_KEYS;
            String[] VALUE_JAVA_CLASS_NAMES;
            String[] VALUE_TYPES;
            String[] VALUE_UNITS;
            String[] VALUE_OBSERVED_PROPERTY_URNS;
            String[] DESCRIPTION_KEYS;
            String DESCRIPTION = "no description provided";

            if (this.retrieveAllValues) {
                VALUE_KEYS = new String[children.size() - 1];
                VALUE_JAVA_CLASS_NAMES = new String[children.size() - 1];
                VALUE_TYPES = new String[children.size() - 1];
                VALUE_UNITS = new String[children.size() - 1];
                VALUE_OBSERVED_PROPERTY_URNS = new String[children.size() - 1];
                DESCRIPTION_KEYS = new String[children.size() - 1];
                DESCRIPTION = rootNode.get("opensdm:data").get("cf:comment").asText();
            } else {
                VALUE_KEYS = new String[1];
                VALUE_JAVA_CLASS_NAMES = new String[1];
                VALUE_TYPES = new String[1];
                VALUE_UNITS = new String[1];
                VALUE_OBSERVED_PROPERTY_URNS = new String[1];
                DESCRIPTION_KEYS = new String[1];
            }

            int position = 0;
            while (childrenIterator.hasNext()) {
                final JsonNode child = childrenIterator.next();
                final String variable_name = child.get("opensdm:data").get("nc:variable_name").asText();

                if (this.retrieveAllValues) {
                    int timeSeriesPosition = (position - 1);
                    // id and date
                    if (isEvent) {
                        if (position == 0) {
                            // event id -> TS 2nd value
                            timeSeriesPosition = 1;
                            VALUE_JAVA_CLASS_NAMES[timeSeriesPosition] = String.class.getName();
                            VALUE_TYPES[timeSeriesPosition] = TimeSeries.VALUE_TYPE_STRING;
                        } else if (position == 2) {
                            // evetn edn time -> TS 1st value (String)
                            timeSeriesPosition = 0;
                            VALUE_JAVA_CLASS_NAMES[timeSeriesPosition] = String.class.getName();
                            VALUE_TYPES[timeSeriesPosition] = TimeSeries.VALUE_TYPE_STRING;
                        } else if (position == 1) {
                            // event start time -> TS Timestamp (no value)
                            position++;
                            continue;
                        } else {
                            VALUE_JAVA_CLASS_NAMES[timeSeriesPosition] = Float.class.getName();
                            VALUE_TYPES[timeSeriesPosition] = TimeSeries.VALUE_TYPE_NUMBER;
                        }
                    } else {
                        if (position == 0) {
                            // ignore start date ....
                            position++;
                            continue;
                        }
                        VALUE_JAVA_CLASS_NAMES[timeSeriesPosition] = Float.class.getName();
                        VALUE_TYPES[timeSeriesPosition] = TimeSeries.VALUE_TYPE_NUMBER;
                    }

                    VALUE_KEYS[timeSeriesPosition] = variable_name;
                    VALUE_UNITS[timeSeriesPosition] = (child.get("opensdm:data").get("cf:units") != null)
                        ? child.get("opensdm:data").get("cf:units").asText() : "n/a";
                    VALUE_OBSERVED_PROPERTY_URNS[timeSeriesPosition] = child
                                .get("opensdm:data").get("cf:long_name").asText();
                    DESCRIPTION_KEYS[timeSeriesPosition] = (child.get("opensdm:data").get("cf:description") != null)
                        ? child.get("opensdm:data").get("cf:description").asText()
                        : child.get("opensdm:data").get("cf:long_name").asText();
                } else if (observedProperty.equals(variable_name)) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("variable '" + observedProperty + "' found in meta data at position #" + position);
                    }

                    VALUE_KEYS[0] = variable_name;
                    VALUE_UNITS[0] = child.get("opensdm:data").get("cf:units").asText();
                    VALUE_OBSERVED_PROPERTY_URNS[0] = child.get("opensdm:data").get("cf:long_name").asText();
                    DESCRIPTION_KEYS[0] = child.get("opensdm:data").get("cf:long_name").asText();
                    VALUE_JAVA_CLASS_NAMES[0] = Float.class.getName();
                    VALUE_TYPES[0] = TimeSeries.VALUE_TYPE_NUMBER;
                    DESCRIPTION = child.get("opensdm:data").get("cf:description").asText();
                    break;
                }
                position++;
            }

            if (!this.retrieveAllValues && (position == children.size())) {
                final String message = "observed property '" + observedProperty
                            + "' not found in '" + offering + "'";
                LOG.error(message);
                throw new ConversionException(message);
            }

            timeSeries.setTSProperty(TimeSeries.VALUE_KEYS, VALUE_KEYS);
            timeSeries.setTSProperty(TimeSeries.VALUE_JAVA_CLASS_NAMES, VALUE_JAVA_CLASS_NAMES);
            timeSeries.setTSProperty(TimeSeries.VALUE_TYPES, VALUE_TYPES);
            timeSeries.setTSProperty(TimeSeries.VALUE_UNITS, VALUE_UNITS);
            timeSeries.setTSProperty(TimeSeries.VALUE_OBSERVED_PROPERTY_URNS, VALUE_OBSERVED_PROPERTY_URNS);
            timeSeries.setTSProperty(TimeSeries.DESCRIPTION_KEYS, DESCRIPTION_KEYS);
            timeSeries.setTSProperty(PropertyNames.DESCRIPTION, DESCRIPTION);

            br = new BufferedReader(new InputStreamReader(from));
            String line = br.readLine();

            if (LOG.isDebugEnabled()) {
                LOG.debug("CSV File Header: " + line);
            }

            final long start = System.currentTimeMillis();
            while ((line = br.readLine()) != null) {
                final String[] split = line.split(","); // NOI18N

                if (this.retrieveAllValues) {
                    if (isEvent) {
                        final String event_start = split[1];
                        final String event_end = split[2];

                        DATEFORMAT.setTimeZone(UTC_TIME_ZONE);
                        final Date startDate = DATEFORMAT.parse(event_start);
                        final Date endDate = DATEFORMAT.parse(event_end);

                        timeSeries.setValue(new TimeStamp(startDate), VALUE_KEYS[0], new TimeStamp(endDate).toString());
                        timeSeries.setValue(new TimeStamp(startDate), VALUE_KEYS[1], split[0]);

                        for (int i = 3; i < split.length; i++) {
                            final String value = split[i];

                            final float val = (NAN.equals(value) || NEG_INF.equals(value))
                                ? -1.0f : NUMBERFORMAT.parse(value).floatValue();

                            timeSeries.setValue(new TimeStamp(startDate),
                                VALUE_KEYS[i - 1], val);
                        }
                    } else {
                        final String key = split[0];
                        DATEFORMAT.setTimeZone(UTC_TIME_ZONE);
                        final Date date = DATEFORMAT.parse(key);
                        for (int i = 1; i < split.length; i++) {
                            final String value = split[i];
                            final float val = (NAN.equals(value) || NEG_INF.equals(value))
                                ? -1.0f : NUMBERFORMAT.parse(value).floatValue();

                            timeSeries.setValue(new TimeStamp(date),
                                VALUE_KEYS[i - 1], val);
                        }
                    }
                    // ignore first value = event start date
                } else if (isEvent) {
                    final String value = split[position];

                    final String event_start = split[1];
                    final String event_end = split[2];

                    DATEFORMAT.setTimeZone(UTC_TIME_ZONE);
                    final Date startDate = DATEFORMAT.parse(event_start);
                    final Date endDate = DATEFORMAT.parse(event_end);

                    if (!NAN.equals(value) && !NEG_INF.equals(value)) {
                        final GregorianCalendar calendar = new GregorianCalendar();
                        final float val = NUMBERFORMAT.parse(value).floatValue();
                        calendar.setTime(startDate);
                        calendar.add(Calendar.SECOND, -1);
                        timeSeries.setValue(new TimeStamp(calendar.getTime()), VALUE_KEYS[0], 0.0f);

                        timeSeries.setValue(new TimeStamp(startDate), VALUE_KEYS[0], val);
                        timeSeries.setValue(new TimeStamp(endDate), VALUE_KEYS[0], val);
                        calendar.setTime(endDate);
                        calendar.add(Calendar.SECOND, +1);
                        timeSeries.setValue(new TimeStamp(calendar.getTime()), VALUE_KEYS[0], 0.0f);
                    }
                } else {
                    final String value = split[position];

                    if (!NAN.equals(value) && !NEG_INF.equals(value)) {
                        final String key = split[0];
                        DATEFORMAT.setTimeZone(UTC_TIME_ZONE);
                        final Date date = DATEFORMAT.parse(key);
                        final float val = NUMBERFORMAT.parse(value).floatValue();

                        timeSeries.setValue(new TimeStamp(date), VALUE_KEYS[0], val);
                    }
                }

                if (Thread.currentThread().isInterrupted()) {
                    LOG.warn("execution was interrupted"); // NOI18N
                    return null;
                }
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("reading timeseries took " + (System.currentTimeMillis() - start) + "ms");
            }

            return timeSeries;
        } catch (final Exception ex) {
            final String message = "cannot convert from input stream"; // NOI18N
            LOG.error(message, ex);
            throw new ConversionException(message, ex);
        }
    }

    @Override
    public String toString() {
        return NbBundle.getMessage(
                LinzNetcdfConverter.class,
                "LinzNetcdfConverter.this.name");
    }

    /**
     * DOCUMENT ME!
     *
     * @param   valueTypesObject  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean isEventTimeseries(final Object valueTypesObject) {
        if (valueTypesObject instanceof String) {
            return false;
        } else if (valueTypesObject instanceof String[]) {
            final String[] valueTypes = (String[])valueTypesObject;
            if (valueTypes.length < 3) {
                return false;
            } else if (valueTypes[0].equals(TimeSeries.VALUE_TYPE_STRING)
                        && valueTypes[1].equals(TimeSeries.VALUE_TYPE_STRING)) {
                return true;
            }
        }

        return false;
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
    public InputStream convertBackward(final TimeSeries to, final String... params) throws ConversionException {
        LOG.info("exporting timeseries with " + to.getTimeStampsArray().length
                    + " values (retrieveAllValues=" + this.retrieveAllValues + ")");
        try {
            final Object valueKeyObject = to.getTSProperty(TimeSeries.VALUE_KEYS);
            final String[] valueKeys;
            final boolean isEventTimeseries = this.isEventTimeseries(to.getTSProperty(TimeSeries.VALUE_TYPES));
            if (valueKeyObject instanceof String) {
                valueKeys = new String[] { (String)valueKeyObject };
                if (LOG.isDebugEnabled()) {
                    LOG.debug("found valuekey: " + valueKeys[0]);                             // NOI18N
                }
            } else if (valueKeyObject instanceof String[]) {
                valueKeys = (String[])valueKeyObject;
                if (LOG.isDebugEnabled()) {
                    LOG.debug("found multiple valuekeys: " + valueKeys.length
                                + ", is event timeseries: " + isEventTimeseries);             // NOI18N
                }
            } else {
                throw new IllegalStateException("unknown value key type: " + valueKeyObject); // NOI18N
            }

            final StringBuilder sb = new StringBuilder();
            final String lineSep = System.getProperty("line.separator"); // NOI18N
            final char valueSep = ';';

            if (isEventTimeseries) {
                sb.append("start_time").append(valueSep);
            } else {
                sb.append("timestamp").append(valueSep);
            }

            for (final String valueKey : valueKeys) {
                sb.append(valueKey).append(valueSep);
            }
            sb.append(lineSep);

            final Iterator<TimeStamp> it = to.getTimeStamps().iterator();
            int i = 0;
            while (it.hasNext()) {
                final TimeStamp stamp = it.next();
                DATEFORMAT.setTimeZone(UTC_TIME_ZONE);
                sb.append(DATEFORMAT.format(stamp.asDate())).append(valueSep);

                for (final String valueKey : valueKeys) {
                    final Object value = to.getValue(stamp, valueKey);
                    if (isEventTimeseries && (i == 0)) {
                        // end timestamp
                        TS_DATEFORMAT.setTimeZone(UTC_TIME_ZONE);
                        DATEFORMAT.setTimeZone(UTC_TIME_ZONE);
                        sb.append(DATEFORMAT.format(TS_DATEFORMAT.parse((String)value))).append(valueSep);
                    } else if (isEventTimeseries && (i == 1)) {
                        // event id
                        sb.append(value).append(valueSep);
                    } else if (value instanceof Float) {
                        sb.append(NUMBERFORMAT.format((Float)value)).append(valueSep);
                    } else {
                        sb.append(value).append(valueSep);
                    }

                    i++;
                }
                sb.append(lineSep);
            }
            LOG.info(i + " measurements successfully exported from timeseries '"
                        + to.getTSProperty(PropertyNames.DESCRIPTION) + '\'');
            return new ByteArrayInputStream(sb.toString().getBytes());
        } catch (final Exception e) {
            final String message = "cannot convert timeseries data"; // NOI18N
            LOG.error(message, e);
            throw new ConversionException(message, e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isRetrieveAllValues() {
        return this.retrieveAllValues;
    }
}
