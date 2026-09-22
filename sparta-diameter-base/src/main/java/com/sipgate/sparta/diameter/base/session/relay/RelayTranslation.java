package com.sipgate.sparta.diameter.base.session.relay;

import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.transport.DiameterPeer;

import java.util.Objects;

/**
 * Bookkeeping for one relayed request (RFC 6733 §6.1.9): what the relay must remember
 * between forwarding a request downstream and restoring the answer upstream
 * (RFC 6733 §6.2.2).
 *
 * <p>Stored by the egress session keyed by the outbound hop-by-hop identifier it
 * minted for the forwarded request. When the downstream answer arrives, the entry
 * supplies the peer the original request came in on ({@code upstreamPeer}) and the
 * hop-by-hop identifier to restore in the answer header ({@code originalHopByHop}).</p>
 *
 * @param upstreamPeer     the peer connection the original request arrived on; the
 *                         restored answer is sent here
 * @param originalHopByHop the hop-by-hop identifier of the original inbound request,
 *                         restored into the answer forwarded upstream
 * @param endToEnd         the end-to-end identifier of the original request; it is
 *                         preserved unchanged on the forwarded request (RFC 6733 §3)
 * @param createdAtMillis  wall-clock creation time, for diagnostics and timeout cleanup
 */
public record RelayTranslation(
        DiameterPeer upstreamPeer,
        HopByHopId originalHopByHop,
        EndToEndId endToEnd,
        long createdAtMillis) {

    public RelayTranslation {
        Objects.requireNonNull(upstreamPeer, "upstreamPeer must not be null");
        Objects.requireNonNull(originalHopByHop, "originalHopByHop must not be null");
        Objects.requireNonNull(endToEnd, "endToEnd must not be null");
    }
}
