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

    /**
     * The Auth-Application-Id and Acct-Application-Id values carried inside every
     * Vendor-Specific-Application-Id grouped AVP, in AVP order. The Vendor-Id of the
     * group is informational and is not returned (RFC 6733 §5.3).
     *
     * @return the application identifiers inside the vendor-specific groups; empty when
     *         the message carries none
     */
    default List<Long> getVendorSpecificApplicationIdValues() {
        final List<Long> result = new ArrayList<>();
        for (final AVPContainer container : getVendorSpecificApplicationIds()) {
            final AVP authApp = container.findAVP(new AVPKey(DiameterConstants.AVP_AUTH_APPLICATION_ID, 0));
            if (authApp != null) {
                result.add(authApp.getDataAsUnsignedInt());
            }
            final AVP acctApp = container.findAVP(new AVPKey(DiameterConstants.AVP_ACCT_APPLICATION_ID, 0));
            if (acctApp != null) {
                result.add(acctApp.getDataAsUnsignedInt());
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
