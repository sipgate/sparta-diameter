package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Mixin for Diameter messages carrying zero or more Supported-Vendor-Id AVPs.
 * <p>
 * RFC 6733 defines {@code * [ Supported-Vendor-Id ]} in CER/CEA.
 * </p>
 */
public interface HasSupportedVendorIdAVPs<T extends HasSupportedVendorIdAVPs<T>> extends AVPContainer<T> {

    default T addSupportedVendorId(final long supportedVendorId) {
        addAVP(AVP.create(DiameterConstants.AVP_SUPPORTED_VENDOR_ID, supportedVendorId));
        return self();
    }

    default List<Long> getSupportedVendorIds() {
        final List<Long> result = new ArrayList<>();
        for (final AVP avp : findAVPs(DiameterConstants.AVP_SUPPORTED_VENDOR_ID)) {
            result.add(avp.getDataAsUnsignedInt());
        }
        return result;
    }

    default Long getFirstSupportedVendorId() {
        final List<Long> all = getSupportedVendorIds();
        return all.isEmpty() ? null : all.get(0);
    }

    default T addAllSupportedVendorIds(final Collection<Long> supportedVendorIds) {
        for (final long id : supportedVendorIds) {
            addSupportedVendorId(id);
        }
        return self();
    }
}
