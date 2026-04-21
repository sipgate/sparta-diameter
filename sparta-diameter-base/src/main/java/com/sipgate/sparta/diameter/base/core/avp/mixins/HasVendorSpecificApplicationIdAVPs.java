package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Mixin for Diameter messages carrying zero or more Vendor-Specific-Application-Id AVPs.
 * <p>
 * RFC 6733 defines {@code * [ Vendor-Specific-Application-Id ]} in CER/CEA.
 * For messages where Vendor-Specific-Application-Id is optional and singular
 * (e.g. ACR/ACA), use {@link HasVendorSpecificApplicationIdAVP} instead.
 * </p>
 */
public interface HasVendorSpecificApplicationIdAVPs extends AVPContainer {

    default void addVendorSpecificApplicationId(final List<AVP> avps) {
        addAVP(AVP.create(new AVPKey(DiameterConstants.AVP_VENDOR_SPECIFIC_APPLICATION_ID, 0), avps));
    }

    default List<AVPContainer> getVendorSpecificApplicationIds() {
        final List<AVPContainer> result = new ArrayList<>();
        for (final AVP avp : findAVPs(new AVPKey(DiameterConstants.AVP_VENDOR_SPECIFIC_APPLICATION_ID, 0))) {
            if (avp instanceof final GroupedAVP grouped) {
                result.add(grouped);
            }
        }
        return result;
    }

    default AVPContainer getFirstVendorSpecificApplicationId() {
        final var all = getVendorSpecificApplicationIds();
        return all.isEmpty() ? null : all.get(0);
    }

    default void addAllVendorSpecificApplicationIds(final Collection<List<AVP>> vendorSpecificApplicationIds) {
        for (final List<AVP> avps : vendorSpecificApplicationIds) {
            addVendorSpecificApplicationId(avps);
        }
    }
}
