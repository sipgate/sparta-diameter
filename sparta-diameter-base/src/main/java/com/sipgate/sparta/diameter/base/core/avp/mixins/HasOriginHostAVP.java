package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

/**
 * Interface for Diameter messages that include Origin-Host AVP.
 * <p>
 * This interface provides default implementations for handling the Origin-Host AVP
 * as defined in RFC 6733. The Origin-Host AVP identifies the endpoint that originated the Diameter message.
 * </p>
 */
public interface HasOriginHostAVP extends AVPContainer {

    /**
     * Sets the Origin-Host AVP.
     *
     * @param originHost The origin host identifier to set.
     */
    default void setOriginHost(final String originHost) {
        setAVP(AVP.create(new AVPKey(DiameterConstants.AVP_ORIGIN_HOST, 0), originHost));
    }

    /**
     * Gets the Origin-Host from this message.
     *
     * @return The origin host identifier, or null if not found.
     */
    default String getOriginHost() {
        final AVP originHostAVP = findAVP(new AVPKey(DiameterConstants.AVP_ORIGIN_HOST, 0));
        if (originHostAVP != null) {
            return originHostAVP.getDataAsString();
        }
        return null;
    }
}
