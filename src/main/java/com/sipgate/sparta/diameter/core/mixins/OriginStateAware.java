package com.sipgate.sparta.diameter.core.mixins;

import com.sipgate.sparta.diameter.core.avp.AVP;
import com.sipgate.sparta.diameter.core.DiameterConstants;

/**
 * Interface for Diameter messages that include Origin-State-Id AVP.
 * <p>
 * This interface provides default implementations for handling the Origin-State-Id AVP
 * as defined in <a href="https://datatracker.ietf.org/doc/html/rfc6733#section-8.16">RFC 6733, Section 8.16</a>.
 * The Origin-State-Id AVP is used to detect and manage peer restarts.
 * </p>
 */
public interface OriginStateAware extends DiameterMessage {

    /**
     * Sets the Origin-State-Id AVP.
     *
     * @param originStateId The origin state identifier to set.
     */
    default void setOriginStateId(final long originStateId) {
        setAVP(AVP.create(DiameterConstants.AVP_ORIGIN_STATE_ID, originStateId));
    }

    /**
     * Gets the Origin-State-Id from this message.
     *
     * @return The origin state identifier, or -1 if not found.
     */
    default int getOriginStateId() {
        final AVP originStateIdAVP = findAVP(DiameterConstants.AVP_ORIGIN_STATE_ID);
        if (originStateIdAVP != null && originStateIdAVP.getData().length >= 4) {
            return originStateIdAVP.getDataAsInt();
        }
        return -1;
    }
}
