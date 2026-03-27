package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
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
public interface HasVendorSpecificApplicationIdAVPs<T extends HasVendorSpecificApplicationIdAVPs<T>> extends AVPContainer<T> {

    default T addVendorSpecificApplicationId(final GroupedAVP vendorSpecificApplicationId) {
        addAVP(vendorSpecificApplicationId);
        return self();
    }

    default List<GroupedAVP> getVendorSpecificApplicationIds() {
        final List<GroupedAVP> result = new ArrayList<>();
        for (final AVP avp : findAVPs(DiameterConstants.AVP_VENDOR_SPECIFIC_APPLICATION_ID)) {
            result.add((GroupedAVP) avp);
        }
        return result;
    }

    default GroupedAVP getFirstVendorSpecificApplicationId() {
        final List<GroupedAVP> all = getVendorSpecificApplicationIds();
        return all.isEmpty() ? null : all.get(0);
    }

    default T addAllVendorSpecificApplicationIds(final Collection<GroupedAVP> vendorSpecificApplicationIds) {
        for (final GroupedAVP avp : vendorSpecificApplicationIds) {
            addVendorSpecificApplicationId(avp);
        }
        return self();
    }
}
