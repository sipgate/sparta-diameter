package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

/**
 * Interface for Diameter messages that include Acct-Interim-Interval AVP.
 * <p>
 * This interface provides default implementations for handling the Acct-Interim-Interval AVP
 * as defined in RFC 6733. The Acct-Interim-Interval AVP is used to indicate the number of seconds between each interim accounting record.
 * </p>
 */
public interface HasAcctInterimIntervalAVP extends AVPContainer {

    /**
     * Sets the Acct-Interim-Interval AVP.
     *
     * @param acctInterimInterval The accounting interim interval to set.
     */
    default void setAcctInterimInterval(final long acctInterimInterval) {
        setAVP(AVP.create(new AVPKey(DiameterConstants.AVP_ACCT_INTERIM_INTERVAL, 0), acctInterimInterval));
    }

    /**
     * Gets the Acct-Interim-Interval from this message.
     *
     * @return The accounting interim interval, or -1 if not found.
     */
    default long getAcctInterimInterval() {
        final AVP acctInterimIntervalAVP = findAVP(new AVPKey(DiameterConstants.AVP_ACCT_INTERIM_INTERVAL, 0));
        if (acctInterimIntervalAVP != null) {
            return acctInterimIntervalAVP.getDataAsLong();
        }
        return -1;
    }
}
