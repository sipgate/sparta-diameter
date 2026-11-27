package com.sipgate.sparta.diameter.base.core.avp.mixins;

import java.util.HashSet;
import java.util.Set;

/**
 * Mixin for messages that advertise applications during the capabilities exchange
 * (CER/CEA).
 * <p>
 * RFC 6733 §5.3 distributes the advertised application identifiers over
 * Auth-Application-Id, Acct-Application-Id and Vendor-Specific-Application-Id. Consumers
 * that only need "can this peer handle application X" want the union, not the three
 * separate lists. The Vendor-Id inside Vendor-Specific-Application-Id is informational
 * and is not part of the result (RFC 6733 §5.3).
 * </p>
 */
public interface HasAdvertisedApplicationIds
        extends HasAuthApplicationIdAVPs, HasAcctApplicationIdAVPs, HasVendorSpecificApplicationIdAVPs {

    /**
     * All application identifiers advertised by this message: the top-level
     * Auth-Application-Id and Acct-Application-Id values plus the identifiers inside
     * every Vendor-Specific-Application-Id.
     *
     * @return an immutable set of the advertised application identifiers; empty when
     *         the message advertises none
     */
    default Set<Long> getAdvertisedApplicationIds() {
        final Set<Long> ids = new HashSet<>();
        ids.addAll(getAuthApplicationIds());
        ids.addAll(getAcctApplicationIds());
        ids.addAll(getVendorSpecificApplicationIdValues());
        return Set.copyOf(ids);
    }
}
