package com.sipgate.sparta.diameter.core.avp.mixins;

import com.sipgate.sparta.diameter.core.avp.AVP;
import com.sipgate.sparta.diameter.core.DiameterConstants;
import com.sipgate.sparta.diameter.core.avp.AVPContainer;

/**
 * Interface for Diameter messages that include Firmware-Revision AVP.
 * <p>
 * This interface provides default implementations for handling the Firmware-Revision AVP
 * as defined in RFC 6733. The Firmware-Revision AVP is used to inform a peer of the firmware revision of the issuing device.
 * </p>
 */
public interface HasFirmwareRevisionAVP<T extends HasFirmwareRevisionAVP<T>> extends AVPContainer {

    /**
     * Sets the Firmware-Revision AVP.
     *
     * @param firmwareRevision The firmware revision to set.
     */
    default T setFirmwareRevision(final long firmwareRevision) {
        setAVP(AVP.create(DiameterConstants.AVP_FIRMWARE_REVISION, firmwareRevision));
        return self();
    }

    /**
     * Gets the Firmware-Revision from this message.
     *
     * @return The firmware revision, or -1 if not found.
     */
    default long getFirmwareRevision() {
        final AVP firmwareRevisionAVP = findAVP(DiameterConstants.AVP_FIRMWARE_REVISION);
        if (firmwareRevisionAVP != null) {
            return firmwareRevisionAVP.getDataAsLong();
        }
        return -1;
    }

    @SuppressWarnings("unchecked")
    default T self() {
        return (T) this;
    }
}
