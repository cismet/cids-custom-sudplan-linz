/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.linz;

import de.cismet.cids.custom.sudplan.DefaultRunInfo;

/**
 * DOCUMENT ME!
 *
 * @author   pascal
 * @version  $Revision$, $Date$
 */
public class EtaRunInfo extends DefaultRunInfo {

    //~ Instance fields --------------------------------------------------------

    private int swmmRunId = -1;

    //~ Methods ----------------------------------------------------------------

    /**
     * Get the value of swmmRunId.
     *
     * @return  the value of swmmRunId
     */
    public int getSwmmRunId() {
        return swmmRunId;
    }

    /**
     * Set the value of swmmRunId.
     *
     * @param  swmmRunId  new value of swmmRunId
     */
    public void setSwmmRunId(final int swmmRunId) {
        this.swmmRunId = swmmRunId;
    }
}
