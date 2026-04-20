package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

/**
 * Interface for Diameter messages that include Auth-Session-State AVP.
 * <p>
 * This interface provides default implementations for handling the Auth-Session-State AVP
 * as defined in RFC 6733. The Auth-Session-State AVP specifies whether state is maintained for a particular session.
 * </p>
 */
public interface HasAuthSessionStateAVP extends AVPContainer {

    /**
     * Sets the Auth-Session-State AVP.
     *
     * @param authSessionState The authentication session state to set.
     */
    default void setAuthSessionState(final int authSessionState) {
        if (authSessionState != DiameterConstants.AUTH_SESSION_STATE_MAINTAINED
            && authSessionState != DiameterConstants.AUTH_SESSION_STATE_NOT_MAINTAINED) {
            throw new IllegalArgumentException(String.format("Invalid auth session state: %d", authSessionState));
        }

        setAVP(AVP.create(new AVPKey(DiameterConstants.AVP_AUTH_SESSION_STATE, 0), authSessionState));
    }

    /**
     * Gets the Auth-Session-State from this message.
     *
     * @return The authentication session state, or -1 if not found.
     */
    default int getAuthSessionState() {
        final AVP authSessionStateAVP = findAVP(new AVPKey(DiameterConstants.AVP_AUTH_SESSION_STATE, 0));
        if (authSessionStateAVP != null) {
            return authSessionStateAVP.getDataAsInt();
        }
        return -1;
    }
}
