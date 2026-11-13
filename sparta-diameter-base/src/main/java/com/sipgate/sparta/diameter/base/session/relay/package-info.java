/**
 * Relay support for the session layer (RFC 6733 §6.1.9 request forwarding and
 * §6.2.2 answer forwarding, plus §6.1.3/§6.1.7 loop detection).
 *
 * <h2>Why there is no {@code DiameterRelaySession} class</h2>
 *
 * The original feature sketch ({@code specs/relay-support}) placed a
 * {@code DiameterRelaySession} here. Implementation showed that relay state is
 * inherently per-connection state, so the relay entry points live on
 * {@link com.sipgate.sparta.diameter.base.session.DiameterSession} itself and are
 * inherited by both concrete session classes:
 *
 * <ul>
 *   <li>The outbound hop-by-hop identifier must be unique on the egress connection
 *       (RFC 6733 §3) — it is minted by the egress session's per-connection
 *       {@code DiameterIdentifiers}.</li>
 *   <li>The answer arrives on the egress connection, so the answer dispatch
 *       ({@code complete}) that must perform the translation lookup runs on the
 *       egress session.</li>
 *   <li>Request tracking (timeout task on the peer's event loop, duration meters,
 *       pending map) reuses the exact machinery of {@code sendAndTrack}.</li>
 * </ul>
 *
 * <p>A dedicated session subtype would either duplicate the initiator/responder
 * capability state machines (CER/CEA handshake, watchdog, reconnect) or be an empty
 * marker class. Both concrete session classes can act as the egress side of a relay
 * hop; the application calls
 * {@code egressSession.relay(upstreamSession, received, forward)}.</p>
 *
 * <p>This package holds the relay-specific value types instead:
 * {@link com.sipgate.sparta.diameter.base.session.relay.RelayTranslation},
 * {@link com.sipgate.sparta.diameter.base.session.relay.RelayTranslationStore},
 * {@link com.sipgate.sparta.diameter.base.session.relay.RelayedAnswer},
 * {@link com.sipgate.sparta.diameter.base.session.relay.RelayFailedException}, and
 * {@link com.sipgate.sparta.diameter.base.session.relay.LoopDetector}.</p>
 */
package com.sipgate.sparta.diameter.base.session.relay;
