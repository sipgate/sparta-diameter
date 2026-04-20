package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

/**
 * Interface for Diameter messages that include Failed-AVP AVP.
 * <p>
 * This interface provides default implementations for handling the Failed-AVP AVP
 * as defined in RFC 6733. The Failed-AVP AVP provides debugging information in cases where a request is rejected or not fully processed.
 * </p>
 */
public interface HasFailedAVP extends AVPContainer {

    /**
     * Sets the Failed-AVP AVP.
     *
     * @param failedAVP The failed AVP to set.
     */
    default void setFailedAVP(final GroupedAVP failedAVP) {
        setAVP(failedAVP);
    }

    /**
     * Gets the Failed-AVP from this message.
     *
     * @return The failed AVP, or null if not found.
     */
    default GroupedAVP getFailedAVP() {
        final AVP failedAVP = findAVP(new AVPKey(DiameterConstants.AVP_FAILED_AVP, 0));
        if (failedAVP != null) {
            return (GroupedAVP) failedAVP;
        }
        return null;
    }
}
