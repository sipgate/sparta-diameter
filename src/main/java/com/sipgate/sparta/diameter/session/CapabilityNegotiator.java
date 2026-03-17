package com.sipgate.sparta.diameter.session;

import com.sipgate.sparta.diameter.core.DiameterConstants;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Computes the capability intersection between two Diameter peers.
 * <p>
 * A common application exists when at least one Auth-Application-Id or
 * Acct-Application-Id appears on both sides, or when either side advertises
 * the relay application (0xFFFFFFFF, per RFC 6733 §5.3).
 * </p>
 */
class CapabilityNegotiator {

    private static final long RELAY_APP_ID = Integer.toUnsignedLong(DiameterConstants.APP_DIAMETER_RELAY);

    /**
     * Returns {@code true} if there is at least one common application between
     * the local capabilities and the remote IDs extracted from the peer's CER/CEA.
     *
     * @param local          local node capabilities from {@link DiameterNodeConfig}
     * @param remoteAuthIds  Auth-Application-Id values advertised by the remote peer
     * @param remoteAcctIds  Acct-Application-Id values advertised by the remote peer
     */
    boolean hasCommonApplication(
            final DiameterNodeConfig.Capabilities local,
            final List<Long> remoteAuthIds,
            final List<Long> remoteAcctIds) {

        final Set<Long> localAuthIds = toUnsignedLongSet(local.getAuthApplicationIds());
        final Set<Long> localAcctIds = toUnsignedLongSet(local.getAcctApplicationIds());

        if (localAuthIds.contains(RELAY_APP_ID) || remoteAuthIds.contains(RELAY_APP_ID)) {
            return true;
        }

        for (final long remoteId : remoteAuthIds) {
            if (localAuthIds.contains(remoteId)) {
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

    private static Set<Long> toUnsignedLongSet(final List<Integer> ids) {
        final Set<Long> result = new HashSet<>();
        for (final Integer id : ids) {
            result.add(Integer.toUnsignedLong(id));
        }
        return result;
    }
}
