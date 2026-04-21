package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

import java.util.List;

/**
 * Interface for Diameter messages that include Vendor-Specific-Application-Id AVP.
 * <p>
 * This interface provides default implementations for handling the Vendor-Specific-Application-Id AVP
 * as defined in RFC 6733. The Vendor-Specific-Application-Id AVP is used to advertise support of a vendor-specific Diameter Application.
 * </p>
 */
public interface HasVendorSpecificApplicationIdAVP extends AVPContainer {

    /**
     * Sets the Vendor-Specific-Application-Id AVP.
     *
     * @param avps The child AVPs of the Vendor-Specific-Application-Id grouped AVP.
     */
    default void setVendorSpecificApplicationId(final List<AVP> avps) {
        setAVP(AVP.create(new AVPKey(DiameterConstants.AVP_VENDOR_SPECIFIC_APPLICATION_ID, 0), avps));
    }

    /**
     * Gets the Vendor-Specific-Application-Id from this message.
     *
     * @return The vendor-specific application identifier, or null if not found.
     */
    default AVPContainer getVendorSpecificApplicationId() {
        final var avp = findAVP(new AVPKey(DiameterConstants.AVP_VENDOR_SPECIFIC_APPLICATION_ID, 0));
        return avp instanceof final GroupedAVP grouped ? grouped : null;
    }
}
