package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

import java.util.List;

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
     * @param avps The child AVPs of the Failed-AVP grouped AVP.
     */
    default void setFailedAVP(final List<AVP> avps) {
        setAVP(AVP.create(new AVPKey(DiameterConstants.AVP_FAILED_AVP, 0), avps));
    }

    /**
     * Gets the Failed-AVP from this message.
     *
     * @return The failed AVP, or null if not found.
     */
    default AVPContainer getFailedAVP() {
        final var avp = findAVP(new AVPKey(DiameterConstants.AVP_FAILED_AVP, 0));
        return avp instanceof final GroupedAVP grouped ? grouped : null;
    }
}
