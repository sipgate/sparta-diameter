package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

/**
 * Interface for Diameter messages that include Supported-Vendor-Id AVP.
 * <p>
 * This interface provides default implementations for handling the Supported-Vendor-Id AVP
 * as defined in RFC 6733. The Supported-Vendor-Id AVP is used to advertise support of a vendor-specific Diameter Application.
 * </p>
 */
public interface HasSupportedVendorIdAVP<T extends HasSupportedVendorIdAVP<T>> extends AVPContainer<T> {

    /**
     * Sets the Supported-Vendor-Id AVP.
     *
     * @param supportedVendorId The supported vendor identifier to set.
     */
    default T setSupportedVendorId(final long supportedVendorId) {
        setAVP(AVP.create(new AVPKey(DiameterConstants.AVP_SUPPORTED_VENDOR_ID, 0), supportedVendorId));
        return self();
    }

    /**
     * Gets the Supported-Vendor-Id from this message.
     *
     * @return The supported vendor identifier, or -1 if not found.
     */
    default long getSupportedVendorId() {
        final AVP supportedVendorIdAVP = findAVP(new AVPKey(DiameterConstants.AVP_SUPPORTED_VENDOR_ID, 0));
        if (supportedVendorIdAVP != null) {
            return supportedVendorIdAVP.getDataAsLong();
        }
        return -1;
    }
}
