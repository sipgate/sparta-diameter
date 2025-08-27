package com.sipgate.sparta.diameter.core.avp.mixins;

import com.sipgate.sparta.diameter.core.avp.AVP;
import com.sipgate.sparta.diameter.core.DiameterConstants;
import com.sipgate.sparta.diameter.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.core.avp.GroupedAVP;

/**
 * Interface for Diameter messages that include Vendor-Specific-Application-Id AVP.
 * <p>
 * This interface provides default implementations for handling the Vendor-Specific-Application-Id AVP
 * as defined in RFC 6733. The Vendor-Specific-Application-Id AVP is used to advertise support of a vendor-specific Diameter Application.
 * </p>
 */
public interface HasVendorSpecificApplicationIdAVP<T extends HasVendorSpecificApplicationIdAVP<T>> extends AVPContainer {

    /**
     * Sets the Vendor-Specific-Application-Id AVP.
     *
     * @param vendorSpecificApplicationId The vendor-specific application identifier to set.
     */
    default T setVendorSpecificApplicationId(final GroupedAVP vendorSpecificApplicationId) {
        setAVP(vendorSpecificApplicationId);
        return self();
    }

    /**
     * Gets the Vendor-Specific-Application-Id from this message.
     *
     * @return The vendor-specific application identifier, or null if not found.
     */
    default GroupedAVP getVendorSpecificApplicationId() {
        final AVP vendorSpecificApplicationIdAVP = findAVP(DiameterConstants.AVP_VENDOR_SPECIFIC_APPLICATION_ID);
        if (vendorSpecificApplicationIdAVP != null) {
            return (GroupedAVP) vendorSpecificApplicationIdAVP;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    default T self() {
        return (T) this;
    }
}
