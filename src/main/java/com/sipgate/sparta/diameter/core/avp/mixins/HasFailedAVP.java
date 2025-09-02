package com.sipgate.sparta.diameter.core.avp.mixins;

import com.sipgate.sparta.diameter.core.avp.AVP;
import com.sipgate.sparta.diameter.core.DiameterConstants;
import com.sipgate.sparta.diameter.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.core.avp.GroupedAVP;

/**
 * Interface for Diameter messages that include Failed-AVP AVP.
 * <p>
 * This interface provides default implementations for handling the Failed-AVP AVP
 * as defined in RFC 6733. The Failed-AVP AVP provides debugging information in cases where a request is rejected or not fully processed.
 * </p>
 */
public interface HasFailedAVP<T extends HasFailedAVP<T>> extends AVPContainer<T> {

    /**
     * Sets the Failed-AVP AVP.
     *
     * @param failedAVP The failed AVP to set.
     */
    default T setFailedAVP(final GroupedAVP failedAVP) {
        setAVP(failedAVP);
        return self();
    }

    /**
     * Gets the Failed-AVP from this message.
     *
     * @return The failed AVP, or null if not found.
     */
    default GroupedAVP getFailedAVP() {
        final AVP failedAVP = findAVP(DiameterConstants.AVP_FAILED_AVP);
        if (failedAVP != null) {
            return (GroupedAVP) failedAVP;
        }
        return null;
    }
}
