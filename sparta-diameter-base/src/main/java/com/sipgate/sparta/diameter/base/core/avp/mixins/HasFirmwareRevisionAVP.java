package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

/**
 * Interface for Diameter messages that include Firmware-Revision AVP.
 * <p>
 * This interface provides default implementations for handling the Firmware-Revision AVP
 * as defined in RFC 6733. The Firmware-Revision AVP is used to inform a peer of the firmware revision of the issuing device.
 * </p>
 */
public interface HasFirmwareRevisionAVP extends AVPContainer {

    /**
     * Sets the Firmware-Revision AVP.
     *
     * @param firmwareRevision The firmware revision to set.
     */
    default void setFirmwareRevision(final long firmwareRevision) {
        setAVP(AVP.create(new AVPKey(DiameterConstants.AVP_FIRMWARE_REVISION, 0), firmwareRevision));
    }

    /**
     * Gets the Firmware-Revision from this message.
     *
     * @return The firmware revision, or -1 if not found.
     */
    default long getFirmwareRevision() {
        final AVP firmwareRevisionAVP = findAVP(new AVPKey(DiameterConstants.AVP_FIRMWARE_REVISION, 0));
        if (firmwareRevisionAVP != null) {
            return firmwareRevisionAVP.getDataAsLong();
        }
        return -1;
    }
}
