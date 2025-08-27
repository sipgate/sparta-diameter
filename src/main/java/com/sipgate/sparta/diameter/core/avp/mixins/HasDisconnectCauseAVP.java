package com.sipgate.sparta.diameter.core.avp.mixins;

import com.sipgate.sparta.diameter.core.avp.AVP;
import com.sipgate.sparta.diameter.core.DiameterConstants;
import com.sipgate.sparta.diameter.core.avp.AVPContainer;

/**
 * Interface for Diameter messages that include Disconnect-Cause AVP.
 * <p>
 * This interface provides default implementations for handling the Disconnect-Cause AVP
 * as defined in RFC 6733. The Disconnect-Cause AVP is used to inform the peer of the reason for the disconnection.
 * </p>
 */
public interface HasDisconnectCauseAVP<T extends HasDisconnectCauseAVP<T>> extends AVPContainer {

    /**
     * Sets the Disconnect-Cause AVP.
     *
     * @param disconnectCause The disconnect cause to set.
     */
    default T setDisconnectCause(final int disconnectCause) {
        setAVP(AVP.create(DiameterConstants.AVP_DISCONNECT_CAUSE, disconnectCause));
        return self();
    }

    /**
     * Gets the Disconnect-Cause from this message.
     *
     * @return The disconnect cause, or -1 if not found.
     */
    default int getDisconnectCause() {
        final AVP disconnectCauseAVP = findAVP(DiameterConstants.AVP_DISCONNECT_CAUSE);
        if (disconnectCauseAVP != null) {
            return disconnectCauseAVP.getDataAsInt();
        }
        return -1;
    }

    @SuppressWarnings("unchecked")
    default T self() {
        return (T) this;
    }
}
