package com.sipgate.sparta.diameter.core.avp.mixins;

import com.sipgate.sparta.diameter.core.avp.AVP;
import com.sipgate.sparta.diameter.core.DiameterConstants;
import com.sipgate.sparta.diameter.core.avp.AVPContainer;

/**
 * Interface for Diameter messages that include Origin-Host AVP.
 * <p>
 * This interface provides default implementations for handling the Origin-Host AVP
 * as defined in RFC 6733. The Origin-Host AVP identifies the endpoint that originated the Diameter message.
 * </p>
 */
public interface HasOriginHostAVP<T extends HasOriginHostAVP<T>> extends AVPContainer<T> {

    /**
     * Sets the Origin-Host AVP.
     *
     * @param originHost The origin host identifier to set.
     */
    default T setOriginHost(final String originHost) {
        setAVP(AVP.create(DiameterConstants.AVP_ORIGIN_HOST, originHost));
        return self();
    }

    /**
     * Gets the Origin-Host from this message.
     *
     * @return The origin host identifier, or null if not found.
     */
    default String getOriginHost() {
        final AVP originHostAVP = findAVP(DiameterConstants.AVP_ORIGIN_HOST);
        if (originHostAVP != null) {
            return originHostAVP.getDataAsString();
        }
        return null;
    }
}
