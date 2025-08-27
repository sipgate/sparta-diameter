package com.sipgate.sparta.diameter.core.avp.mixins;

import com.sipgate.sparta.diameter.core.avp.AVP;
import com.sipgate.sparta.diameter.core.DiameterConstants;
import com.sipgate.sparta.diameter.core.avp.AVPContainer;

/**
 * Interface for Diameter messages that include Vendor-Id AVP.
 * <p>
 * This interface provides default implementations for handling the Vendor-Id AVP
 * as defined in RFC 6733. The Vendor-Id AVP contains the IANA-assigned "SMI Network Management Private Enterprise Codes" value.
 * </p>
 */
public interface HasVendorIdAVP<T extends HasVendorIdAVP<T>> extends AVPContainer {

    /**
     * Sets the Vendor-Id AVP.
     *
     * @param vendorId The vendor identifier to set.
     */
    default T setVendorId(final long vendorId) {
        setAVP(AVP.create(DiameterConstants.AVP_VENDOR_ID, vendorId));
        return self();
    }

    /**
     * Gets the Vendor-Id from this message.
     *
     * @return The vendor identifier, or -1 if not found.
     */
    default long getVendorId() {
        final AVP vendorIdAVP = findAVP(DiameterConstants.AVP_VENDOR_ID);
        if (vendorIdAVP != null) {
            return vendorIdAVP.getDataAsLong();
        }
        return -1;
    }

    @SuppressWarnings("unchecked")
    default T self() {
        return (T) this;
    }
}
