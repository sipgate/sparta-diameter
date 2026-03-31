package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

/**
 * Interface for Diameter messages that include Vendor-Specific-Application-Id AVP.
 * <p>
 * This interface provides default implementations for handling the Vendor-Specific-Application-Id AVP
 * as defined in RFC 6733. The Vendor-Specific-Application-Id AVP is used to advertise support of a vendor-specific Diameter Application.
 * </p>
 */
public interface HasVendorSpecificApplicationIdAVP<T extends HasVendorSpecificApplicationIdAVP<T>> extends AVPContainer<T> {

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
        final AVP vendorSpecificApplicationIdAVP = findAVP(new AVPKey(DiameterConstants.AVP_VENDOR_SPECIFIC_APPLICATION_ID, 0));
        if (vendorSpecificApplicationIdAVP != null) {
            return (GroupedAVP) vendorSpecificApplicationIdAVP;
        }
        return null;
    }
}
