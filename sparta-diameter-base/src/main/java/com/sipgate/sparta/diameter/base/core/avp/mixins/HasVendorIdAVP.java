package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

/**
 * Interface for Diameter messages that include Vendor-Id AVP.
 * <p>
 * This interface provides default implementations for handling the Vendor-Id AVP
 * as defined in RFC 6733. The Vendor-Id AVP contains the IANA-assigned "SMI Network Management Private Enterprise Codes" value.
 * </p>
 */
public interface HasVendorIdAVP extends AVPContainer {

    /**
     * Sets the Vendor-Id AVP.
     *
     * @param vendorId The vendor identifier to set.
     */
    default void setVendorId(final long vendorId) {
        setAVP(AVP.create(new AVPKey(DiameterConstants.AVP_VENDOR_ID, 0), vendorId));
    }

    /**
     * Gets the Vendor-Id from this message.
     *
     * @return The vendor identifier, or -1 if not found.
     */
    default long getVendorId() {
        final AVP vendorIdAVP = findAVP(new AVPKey(DiameterConstants.AVP_VENDOR_ID, 0));
        if (vendorIdAVP != null) {
            return vendorIdAVP.getDataAsUnsignedInt();
        }
        return -1;
    }
}
