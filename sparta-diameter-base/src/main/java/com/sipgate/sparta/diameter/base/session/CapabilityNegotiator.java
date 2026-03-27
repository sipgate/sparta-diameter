package com.sipgate.sparta.diameter.base.session;

import com.sipgate.sparta.diameter.base.core.DiameterConstants;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Computes the capability intersection between two Diameter peers.
 * <p>
 * A common application exists when at least one application identifier appears
 * on both sides across all three AVP types (Auth-Application-Id,
 * Acct-Application-Id, Vendor-Specific-Application-Id), or when either side
 * advertises the relay application (0xFFFFFFFF, per RFC 6733 §5.3).
 * The Vendor-Id inside Vendor-Specific-Application-Id MUST NOT be used
 * during computation (RFC 6733 §5.3).
 * </p>
 */
class CapabilityNegotiator {

    private static final long RELAY_APP_ID = Integer.toUnsignedLong(DiameterConstants.APP_DIAMETER_RELAY);

    /**
     * Returns {@code true} if there is at least one common application between
     * the local capabilities and the remote IDs extracted from the peer's CER/CEA.
     *
     * @param local                      local node capabilities from {@link DiameterNodeConfig}
     * @param remoteAuthIds              Auth-Application-Id values advertised by the remote peer
     * @param remoteAcctIds              Acct-Application-Id values advertised by the remote peer
     * @param remoteVendorSpecificAppIds application IDs extracted from Vendor-Specific-Application-Id
     *                                   AVPs advertised by the remote peer; vendor ID is discarded
     */
    boolean hasCommonApplication(
            final DiameterNodeConfig.Capabilities local,
            final List<Long> remoteAuthIds,
            final List<Long> remoteAcctIds,
            final List<Long> remoteVendorSpecificAppIds) {

        // Local auth pool: bare auth IDs + app IDs from vendor-specific entries
        final Set<Long> localAuthPool = new HashSet<>(local.authApplicationIds());
        for (final DiameterNodeConfig.VendorSpecificApp app : local.vendorSpecificApplications()) {
            localAuthPool.add(app.authApplicationId());
        }
        final Set<Long> localAcctIds = Set.copyOf(local.acctApplicationIds());

        if (localAuthPool.contains(RELAY_APP_ID) || remoteAuthIds.contains(RELAY_APP_ID)) {
            return true;
        }

        for (final long remoteId : remoteAuthIds) {
            if (localAuthPool.contains(remoteId)) {
                return true;
            }
        }

        for (final long remoteId : remoteVendorSpecificAppIds) {
            if (localAuthPool.contains(remoteId)) {
                return true;
            }
        }

        for (final long remoteId : remoteAcctIds) {
            if (localAcctIds.contains(remoteId)) {
                return true;
            }
        }

        return false;
    }

}
