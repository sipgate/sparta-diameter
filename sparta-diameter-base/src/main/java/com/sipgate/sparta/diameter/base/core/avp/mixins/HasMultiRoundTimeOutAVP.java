package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

/**
 * Interface for Diameter messages that include Multi-Round-Time-Out AVP.
 * <p>
 * This interface provides default implementations for handling the Multi-Round-Time-Out AVP
 * as defined in RFC 6733. The Multi-Round-Time-Out AVP contains the maximum time allowed for a multi-round authentication exchange.
 * </p>
 */
public interface HasMultiRoundTimeOutAVP<T extends HasMultiRoundTimeOutAVP<T>> extends AVPContainer<T> {

    /**
     * Sets the Multi-Round-Time-Out AVP.
     *
     * @param multiRoundTimeOut The multi-round timeout to set.
     */
    default T setMultiRoundTimeOut(final long multiRoundTimeOut) {
        setAVP(AVP.create(new AVPKey(DiameterConstants.AVP_MULTI_ROUND_TIME_OUT, 0), multiRoundTimeOut));
        return self();
    }

    /**
     * Gets the Multi-Round-Time-Out from this message.
     *
     * @return The multi-round timeout, or -1 if not found.
     */
    default long getMultiRoundTimeOut() {
        final AVP multiRoundTimeOutAVP = findAVP(new AVPKey(DiameterConstants.AVP_MULTI_ROUND_TIME_OUT, 0));
        if (multiRoundTimeOutAVP != null) {
            return multiRoundTimeOutAVP.getDataAsLong();
        }
        return -1;
    }
}
