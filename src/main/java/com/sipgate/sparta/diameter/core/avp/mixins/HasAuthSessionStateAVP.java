package com.sipgate.sparta.diameter.core.avp.mixins;

import com.sipgate.sparta.diameter.core.avp.AVP;
import com.sipgate.sparta.diameter.core.DiameterConstants;
import com.sipgate.sparta.diameter.core.avp.AVPContainer;

/**
 * Interface for Diameter messages that include Auth-Session-State AVP.
 * <p>
 * This interface provides default implementations for handling the Auth-Session-State AVP
 * as defined in RFC 6733. The Auth-Session-State AVP specifies whether state is maintained for a particular session.
 * </p>
 */
public interface HasAuthSessionStateAVP<T extends HasAuthSessionStateAVP<T>> extends AVPContainer {

    /**
     * Sets the Auth-Session-State AVP.
     *
     * @param authSessionState The authentication session state to set.
     */
    default T setAuthSessionState(final int authSessionState) {
        setAVP(AVP.create(DiameterConstants.AVP_AUTH_SESSION_STATE, authSessionState));
        return self();
    }

    /**
     * Gets the Auth-Session-State from this message.
     *
     * @return The authentication session state, or -1 if not found.
     */
    default int getAuthSessionState() {
        final AVP authSessionStateAVP = findAVP(DiameterConstants.AVP_AUTH_SESSION_STATE);
        if (authSessionStateAVP != null) {
            return authSessionStateAVP.getDataAsInt();
        }
        return -1;
    }

    @SuppressWarnings("unchecked")
    default T self() {
        return (T) this;
    }
}
