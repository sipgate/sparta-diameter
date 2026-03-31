package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

/**
 * Interface for Diameter messages that include Origin-State-Id AVP.
 * <p>
 * This interface provides default implementations for handling the Origin-State-Id AVP
 * as defined in <a href="https://datatracker.ietf.org/doc/html/rfc6733#section-8.16">RFC 6733, Section 8.16</a>.
 * The Origin-State-Id AVP is used to detect and manage peer restarts.
 * </p>
 */
public interface HasOriginStateIdAVP<T extends HasOriginStateIdAVP<T>> extends AVPContainer<T> {

    /**
     * Sets the Origin-State-Id AVP.
     *
     * @param originStateId The origin state identifier to set.
     */
    default T setOriginStateId(final long originStateId) {
        setAVP(AVP.create(new AVPKey(DiameterConstants.AVP_ORIGIN_STATE_ID, 0), originStateId));
        return self();
    }

    /**
     * Gets the Origin-State-Id from this message.
     *
     * @return The origin state identifier, or -1 if not found.
     */
    default int getOriginStateId() {
        final AVP originStateIdAVP = findAVP(new AVPKey(DiameterConstants.AVP_ORIGIN_STATE_ID, 0));
        if (originStateIdAVP != null && originStateIdAVP.getData().length >= 4) {
            return originStateIdAVP.getDataAsInt();
        }
        return -1;
    }
}
