package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

/**
 * Interface for Diameter messages that include Redirect-Host AVP.
 * <p>
 * This interface provides default implementations for handling the Redirect-Host AVP
 * as defined in RFC 6733. The Redirect-Host AVP contains the host to which the client should re-send the request.
 * </p>
 */
public interface HasRedirectHostAVP extends AVPContainer {

    /**
     * Sets the Redirect-Host AVP.
     *
     * @param redirectHost The redirect host to set.
     */
    default void setRedirectHost(final String redirectHost) {
        setAVP(AVP.create(new AVPKey(DiameterConstants.AVP_REDIRECT_HOST, 0), redirectHost));
    }

    /**
     * Gets the Redirect-Host from this message.
     *
     * @return The redirect host, or null if not found.
     */
    default String getRedirectHost() {
        final AVP redirectHostAVP = findAVP(new AVPKey(DiameterConstants.AVP_REDIRECT_HOST, 0));
        if (redirectHostAVP != null) {
            return redirectHostAVP.getDataAsString();
        }
        return null;
    }
}
