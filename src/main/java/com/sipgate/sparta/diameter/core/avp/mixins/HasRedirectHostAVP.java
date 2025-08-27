package com.sipgate.sparta.diameter.core.avp.mixins;

import com.sipgate.sparta.diameter.core.avp.AVP;
import com.sipgate.sparta.diameter.core.DiameterConstants;
import com.sipgate.sparta.diameter.core.avp.AVPContainer;

/**
 * Interface for Diameter messages that include Redirect-Host AVP.
 * <p>
 * This interface provides default implementations for handling the Redirect-Host AVP
 * as defined in RFC 6733. The Redirect-Host AVP contains the host to which the client should re-send the request.
 * </p>
 */
public interface HasRedirectHostAVP<T extends HasRedirectHostAVP<T>> extends AVPContainer {

    /**
     * Sets the Redirect-Host AVP.
     *
     * @param redirectHost The redirect host to set.
     */
    default T setRedirectHost(final String redirectHost) {
        setAVP(AVP.create(DiameterConstants.AVP_REDIRECT_HOST, redirectHost));
        return self();
    }

    /**
     * Gets the Redirect-Host from this message.
     *
     * @return The redirect host, or null if not found.
     */
    default String getRedirectHost() {
        final AVP redirectHostAVP = findAVP(DiameterConstants.AVP_REDIRECT_HOST);
        if (redirectHostAVP != null) {
            return redirectHostAVP.getDataAsString();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    default T self() {
        return (T) this;
    }
}
