package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

/**
 * Interface for Diameter messages that include Disconnect-Cause AVP.
 * <p>
 * This interface provides default implementations for handling the Disconnect-Cause AVP
 * as defined in RFC 6733. The Disconnect-Cause AVP is used to inform the peer of the reason for the disconnection.
 * </p>
 */
public interface HasDisconnectCauseAVP extends AVPContainer {

    /**
     * Sets the Disconnect-Cause AVP.
     *
     * @param disconnectCause The disconnect cause to set.
     */
    default void setDisconnectCause(final int disconnectCause) {
        setAVP(AVP.create(new AVPKey(DiameterConstants.AVP_DISCONNECT_CAUSE, 0), disconnectCause));
    }

    /**
     * Gets the Disconnect-Cause from this message.
     *
     * @return The disconnect cause, or -1 if not found.
     */
    default int getDisconnectCause() {
        final AVP disconnectCauseAVP = findAVP(new AVPKey(DiameterConstants.AVP_DISCONNECT_CAUSE, 0));
        if (disconnectCauseAVP != null) {
            return disconnectCauseAVP.getDataAsInt();
        }
        return -1;
    }
}
