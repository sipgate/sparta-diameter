package com.sipgate.sparta.diameter.base.session.relay;

import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.IncomingRequest;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasRouteRecordAVPs;
import com.sipgate.sparta.diameter.base.transport.DiameterPeer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalLong;

/**
 * Loop detection and avoidance for relay and proxy agents (RFC 6733 §6.1.3 reactive
 * detection and §6.1.7 predictive avoidance).
 *
 * <p>Both mechanisms operate on the Route-Record AVPs already present in the inbound
 * message — they must run <em>before</em> the relay appends its own Route-Record.
 * Both are skipped for non-proxiable requests (P-bit clear): those are never relayed
 * (RFC 6733 §3, P-bit definition), so route records on them are not loop evidence.</p>
 *
 * <p>Identities are DiameterIdentity FQDNs and compared case-insensitively.</p>
 *
 * <p>Route-Records are read via {@link HasRouteRecordAVPs}, which the proxiable base
 * request types (ASR, RAR, STR, ACR) implement; requests without that capability
 * are treated as having no route records.</p>
 */
public final class LoopDetector {

    private LoopDetector() {
        // Static utility - no instantiation
    }

    /**
     * Reactive loop detection (RFC 6733 §6.1.3): checks whether this agent's own
     * identity already appears in a Route-Record of the received request. A relay or
     * proxy agent MUST answer with {@code DIAMETER_LOOP_DETECTED (3005)} instead of
     * forwarding in that case.
     *
     * <p>Skipped when the P-bit is clear (the request is never relayed) and when the
     * request carries no Route-Records.</p>
     *
     * @param request       the received proxiable request
     * @param ownOriginHost this node's Origin-Host identity
     * @return {@link OptionalLong#of(long)} with
     *         {@link DiameterConstants#RES_DIAMETER_LOOP_DETECTED} when a loop is
     *         detected; {@link OptionalLong#empty()} otherwise
     * @throws NullPointerException if an argument is {@code null}
     */
    public static OptionalLong checkReceived(final IncomingRequest<?> request, final String ownOriginHost) {
        Objects.requireNonNull(request, "request must not be null");
        Objects.requireNonNull(ownOriginHost, "ownOriginHost must not be null");
        if (!request.isProxiable()) {
            return OptionalLong.empty();
        }
        for (final String routeRecord : routeRecords(request)) {
            if (ownOriginHost.equalsIgnoreCase(routeRecord)) {
                return OptionalLong.of(DiameterConstants.RES_DIAMETER_LOOP_DETECTED);
            }
        }
        return OptionalLong.empty();
    }

    /**
     * Predictive loop avoidance (RFC 6733 §6.1.7): excludes candidate next hops whose
     * identity already appears in a Route-Record of the request. If a candidate peer's
     * identity is found in a Route-Record, that route MUST be excluded; if all
     * candidates are eliminated, the caller SHOULD return
     * {@code DIAMETER_UNABLE_TO_DELIVER (3002)} (this method signals that by
     * returning an empty map).
     *
     * <p>{@code DiameterPeer} does not expose the negotiated remote Origin-Host, so the
     * caller supplies the candidates as a map from peer identity (the Origin-Host the
     * candidate advertises) to the peer connection. The returned map preserves the
     * iteration order of {@code candidatesByIdentity}.</p>
     *
     * <p>Skipped (all candidates returned unchanged) when the P-bit is clear.</p>
     *
     * @param request               the received proxiable request
     * @param candidatesByIdentity candidate peers keyed by their Origin-Host identity
     * @return the candidates not present in any Route-Record, in input order; empty
     *         when every candidate is excluded (caller answers 3002)
     * @throws NullPointerException if an argument is {@code null}
     */
    public static Map<String, DiameterPeer> filterCandidates(
            final IncomingRequest<?> request, final Map<String, DiameterPeer> candidatesByIdentity) {
        Objects.requireNonNull(request, "request must not be null");
        Objects.requireNonNull(candidatesByIdentity, "candidatesByIdentity must not be null");
        final Map<String, DiameterPeer> remaining = new LinkedHashMap<>();
        if (!request.isProxiable()) {
            remaining.putAll(candidatesByIdentity);
            return remaining;
        }
        final List<String> routeRecords = routeRecords(request);
        for (final Map.Entry<String, DiameterPeer> candidate : candidatesByIdentity.entrySet()) {
            if (!contains(routeRecords, candidate.getKey())) {
                remaining.put(candidate.getKey(), candidate.getValue());
            }
        }
        return remaining;
    }

    private static boolean contains(final List<String> routeRecords, final String identity) {
        for (final String routeRecord : routeRecords) {
            if (routeRecord.equalsIgnoreCase(identity)) {
                return true;
            }
        }
        return false;
    }

    private static List<String> routeRecords(final IncomingRequest<?> request) {
        if (!(request instanceof final HasRouteRecordAVPs routed)) {
            return List.of();
        }
        final List<String> records = routed.getRouteRecords();
        return records == null ? List.of() : records;
    }

    /**
     * Convenience for callers that need the candidate list, not the identity mapping.
     *
     * @param filtered the result of {@link #filterCandidates(IncomingRequest, Map)}
     * @return the remaining candidate peers in the same order
     */
    public static List<DiameterPeer> peersOf(final Map<String, DiameterPeer> filtered) {
        return new ArrayList<>(filtered.values());
    }
}
