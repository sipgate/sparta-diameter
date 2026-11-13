package com.sipgate.sparta.diameter.base.session.relay;

import com.sipgate.sparta.diameter.base.core.IncomingAnswer;
import com.sipgate.sparta.diameter.base.core.IncomingRequest;

/**
 * Application hook on the relay answer path (RFC 6733 §6.2.2), invoked by the egress
 * session right before a relayed answer is forwarded upstream. Lets a relay agent read
 * the downstream answer and append AVPs to the outgoing answer (e.g. DOIC
 * {@code OC-Supported-Features}/{@code OC-OLR}) without taking over the forwarding.
 *
 * <p>Registered per session via
 * {@link com.sipgate.sparta.diameter.base.session.DiameterSession#setRelayAnswerHook}.
 * Runs on the egress connection's event loop and must not block. A
 * {@link RuntimeException} thrown by the hook is logged and the answer is forwarded
 * anyway (with whatever the hook appended before failing).</p>
 */
@FunctionalInterface
public interface RelayAnswerHook {

    /**
     * @param downstreamAnswer the answer received from the downstream peer (read-only)
     * @param originalRequest  the request originally received from upstream and relayed
     * @param restoredAnswer   the answer about to be sent upstream (original hop-by-hop
     *                         identifier restored); AVPs may be appended via
     *                         {@code addAVP}/{@code setAVP}
     */
    void beforeForward(IncomingAnswer downstreamAnswer, IncomingRequest<?> originalRequest,
                       RelayedAnswer restoredAnswer);
}
