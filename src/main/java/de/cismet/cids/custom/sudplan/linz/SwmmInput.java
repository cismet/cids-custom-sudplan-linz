/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.linz;

import at.ac.ait.enviro.tsapi.timeseries.TimeStamp;

import org.codehaus.jackson.annotate.JsonIgnore;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import de.cismet.cids.custom.sudplan.SMSUtils;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   pascal.dihe@cismet.de
 * @version  $Revision$, $Date$
 */
public final class SwmmInput {

    //~ Static fields/initializers ---------------------------------------------

    public static final String TABLENAME_SWMM_PROJECT = "SWMM_PROJECT"; // NOI18N
    public static final String TABLENAME_MONITOR_STATION = "monitorstation";
    public static final String FK_MONITOR_STATION_KEY = "key";
    public static final SimpleDateFormat UTC_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
    public static final String PROP_TIMESERIES = "timeseries";
    public static final String PROP_INPFILE = "inpFile";
    public static final String PROP_STARTDATE = "startDate";
    public static final String PROP_SWMMPROJECT = "swmmProject";
    public static final String PROP_ENDDATE = "endDate";
    public static final String PROP_FORECAST = "forecast";
    public static final String PROP_TIMESERIESURLS = "timeseriesURLs";
    public static final String PROP_CREATED = "created";
    public static final String PROP_USER = "user";

    static {
        UTC_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    //~ Instance fields --------------------------------------------------------

    private transient List<Integer> timeseries = new ArrayList<Integer>();
    private transient String inpFile;
    private transient Date startDate;
    private transient int swmmProject = -1;
    private transient Date endDate;
    private boolean forecast = false;
    private transient List<String> timeseriesURLs = new ArrayList<String>();
    private transient PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    private transient Date created;
    private transient String user;

    //~ Methods ----------------------------------------------------------------

    /**
     * Get the value of timeseriesURLs.
     *
     * @return  the value of timeseriesURLs
     */
    public List<String> getTimeseriesURLs() {
        return timeseriesURLs;
    }

    /**
     * Set the value of timeseriesURLs.
     *
     * @param  timeseriesURLs  new value of timeseriesURLs
     */
    public void setTimeseriesURLs(final List<String> timeseriesURLs) {
        final List<String> oldTimeseriesURLs = this.timeseriesURLs;
        this.timeseriesURLs = timeseriesURLs;
        propertyChangeSupport.firePropertyChange(PROP_TIMESERIESURLS, oldTimeseriesURLs, timeseriesURLs);
    }

    /**
     * Get the value of timeseriesURLs at specified index.
     *
     * @param   index  DOCUMENT ME!
     *
     * @return  the value of timeseriesURLs at specified index
     */
    public String getTimeseriesURLs(final int index) {
        return this.timeseriesURLs.get(index);
    }

    /**
     * Set the value of timeseriesURLs at specified index.
     *
     * @param  index             DOCUMENT ME!
     * @param  newTimeseriesURL  new value of timeseriesURLs at specified index
     */
    public void setTimeseriesURLs(final int index, final String newTimeseriesURL) {
        final String oldTimeseriesURL = this.timeseriesURLs.get(index);
        this.timeseriesURLs.set(index, newTimeseriesURL);
        propertyChangeSupport.fireIndexedPropertyChange(PROP_TIMESERIESURLS, index, oldTimeseriesURL, newTimeseriesURL);
    }

    /**
     * Get the value of timeseries.
     *
     * @return  the value of timeseries
     */
    public List<Integer> getTimeseries() {
        return timeseries;
    }

    /**
     * Set the value of timeseries.
     *
     * @param  timeseries  new value of timeseries
     */
    public void setTimeseries(final List<Integer> timeseries) {
        final List<Integer> oldTimeseries = this.timeseries;
        this.timeseries = timeseries;
        propertyChangeSupport.firePropertyChange(PROP_TIMESERIES, oldTimeseries, timeseries);
    }

    /**
     * Get the value of timeseries at specified index.
     *
     * @param   index  DOCUMENT ME!
     *
     * @return  the value of timeseries at specified index
     */
    public int getTimeseries(final int index) {
        return this.timeseries.get(index);
    }

    /**
     * Set the value of timeseries at specified index.
     *
     * @param  index          DOCUMENT ME!
     * @param  newTimeseries  new value of timeseries at specified index
     */
    public void setTimeseries(final int index, final int newTimeseries) {
        final int oldTimeseries = this.timeseries.get(index);
        this.timeseries.set(index, newTimeseries);
        propertyChangeSupport.fireIndexedPropertyChange(PROP_TIMESERIES, index, oldTimeseries, newTimeseries);
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
     * DOCUMENT ME!
     *
     * @param  endDate  DOCUMENT ME!
     */
    public void setEndDate(final Date endDate) {
        final Date oldEndDate = this.endDate;
        this.endDate = endDate;
        propertyChangeSupport.firePropertyChange(PROP_ENDDATE, oldEndDate, endDate);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  startDate  DOCUMENT ME!
     */
    public void setStartDate(final Date startDate) {
        final Date oldStartDate = this.startDate;
        this.startDate = startDate;
        propertyChangeSupport.firePropertyChange(PROP_STARTDATE, oldStartDate, startDate);
    }

    /**
     * Get the value of inpFile.
     *
     * @return  the value of inpFile
     */
    public String getInpFile() {
        return inpFile;
    }

    /**
     * Set the value of inpFile.
     *
     * @param  inpFile  new value of inpFile
     */
    public void setInpFile(final String inpFile) {
        final String oldInpFile = this.inpFile;
        this.inpFile = inpFile;
        propertyChangeSupport.firePropertyChange(PROP_INPFILE, oldInpFile, inpFile);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Date getEndDate() {
        return this.endDate;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Date getStartDate() {
        return this.startDate;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public List<CidsBean> fetchTimeseries() {
        assert this.timeseries != null : "timeseries list is null";
        final List<CidsBean> timeseriesBeans = new ArrayList<CidsBean>(this.timeseries.size());
        for (final int timeseriesId : this.timeseries) {
            timeseriesBeans.add(SMSUtils.fetchCidsBean(timeseriesId, SMSUtils.TABLENAME_TIMESERIES));
        }

        return timeseriesBeans;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean fetchSwmmProject() {
        return SMSUtils.fetchCidsBean(this.getSwmmProject(), TABLENAME_SWMM_PROJECT);
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

    /**
     * Get the value of forecast.
     *
     * @return  the value of forecast
     */
    public boolean isForecast() {
        return forecast;
    }

    /**
     * Set the value of forecast.
     *
     * @param  forecast  new value of forecast
     */
    public void setForecast(final boolean forecast) {
        final boolean oldForecast = this.forecast;
        this.forecast = forecast;
        propertyChangeSupport.firePropertyChange(PROP_FORECAST, oldForecast, forecast);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ParseException  DOCUMENT ME!
     */
    @JsonIgnore
    public TimeStamp getStartDateTimestamp() throws ParseException {
        final Date startDateDate = this.getStartDate();
        return new TimeStamp(startDateDate.getTime());
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ParseException  DOCUMENT ME!
     */
    @JsonIgnore
    public TimeStamp getEndDateTimestamp() throws ParseException {
        final Date endDateDate = this.getEndDate();
        return new TimeStamp(endDateDate.getTime());
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
        final Date oldCreated = this.created;
        this.created = created;
        propertyChangeSupport.firePropertyChange(PROP_CREATED, oldCreated, created);
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
        final String oldUser = this.user;
        this.user = user;
        propertyChangeSupport.firePropertyChange(PROP_USER, oldUser, user);
    }

    @JsonIgnore
    @Override
    public String toString() {
        return "SWMM Input for SWMM INP File '" + this.getInpFile() + "'";
    }
}
