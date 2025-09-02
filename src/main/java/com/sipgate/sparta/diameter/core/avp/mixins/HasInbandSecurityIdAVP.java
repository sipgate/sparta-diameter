package com.sipgate.sparta.diameter.core.avp.mixins;

import com.sipgate.sparta.diameter.core.avp.AVP;
import com.sipgate.sparta.diameter.core.DiameterConstants;
import com.sipgate.sparta.diameter.core.avp.AVPContainer;

/**
 * Interface for Diameter messages that include Inband-Security-Id AVP.
 * <p>
 * This interface provides default implementations for handling the Inband-Security-Id AVP
 * as defined in RFC 6733. The Inband-Security-Id AVP is used to convey security capabilities.
 * </p>
 */
public interface HasInbandSecurityIdAVP<T extends HasInbandSecurityIdAVP<T>> extends AVPContainer<T> {

    /**
     * Sets the Inband-Security-Id AVP.
     *
     * @param inbandSecurityId The inband security identifier to set.
     */
    default T setInbandSecurityId(final long inbandSecurityId) {
        setAVP(AVP.create(DiameterConstants.AVP_INBAND_SECURITY_ID, inbandSecurityId));
        return self();
    }

    /**
     * Gets the Inband-Security-Id from this message.
     *
     * @return The inband security identifier, or -1 if not found.
     */
    default long getInbandSecurityId() {
        final AVP inbandSecurityIdAVP = findAVP(DiameterConstants.AVP_INBAND_SECURITY_ID);
        if (inbandSecurityIdAVP != null) {
            return inbandSecurityIdAVP.getDataAsLong();
        }
        return -1;
    }
}
