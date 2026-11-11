package com.sipgate.sparta.diameter.base.session.relay;

import com.sipgate.sparta.diameter.base.core.Command;
import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.IncomingRequest;
import com.sipgate.sparta.diameter.base.core.OutgoingRequest;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.messages.CapabilitiesExchangeRequest;
import com.sipgate.sparta.diameter.base.messages.ReAuthRequest;
import com.sipgate.sparta.diameter.base.transport.DiameterPeer;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class LoopDetectorTest {

    private static final String OWN_HOST = "dra.example.com";

    @Test
    void it_reports_loop_detected_when_own_origin_host_is_in_a_route_record() throws Exception {
        // GIVEN — a proxiable request whose Route-Records contain our own identity
        final IncomingRequest<?> request =
                parseRequest(proxiableRequestWithRouteRecords(OWN_HOST, "hss.example.com"));

        // WHEN
        final var verdict = LoopDetector.checkReceived(request, OWN_HOST);

        // THEN — RFC 6733 §6.1.3: answer 3005 instead of forwarding
        assertThat(verdict).isPresent();
        assertThat(verdict.getAsLong()).isEqualTo(DiameterConstants.RES_DIAMETER_LOOP_DETECTED);
    }

    @Test
    void it_compares_identities_case_insensitively() throws Exception {
        // GIVEN — DiameterIdentity is an FQDN; a peer may record it in another case
        final IncomingRequest<?> request = parseRequest(proxiableRequestWithRouteRecords("DRA.Example.COM"));
        final Map<String, DiameterPeer> candidates = new LinkedHashMap<>();
        candidates.put("dra.example.com", mock(DiameterPeer.class));

        // WHEN
        final var verdict = LoopDetector.checkReceived(request, OWN_HOST);
        final Map<String, DiameterPeer> remaining = LoopDetector.filterCandidates(request, candidates);

        // THEN
        assertThat(verdict).isPresent();
        assertThat(remaining).isEmpty();
    }

    @Test
    void it_reports_no_loop_when_own_origin_host_is_not_in_a_route_record() throws Exception {
        // GIVEN
        final IncomingRequest<?> request = parseRequest(proxiableRequestWithRouteRecords("hss.example.com"));

        // WHEN
        final var verdict = LoopDetector.checkReceived(request, OWN_HOST);

        // THEN
        assertThat(verdict).isEmpty();
    }

    @Test
    void it_skips_loop_detection_when_proxiable_bit_is_clear() throws Exception {
        // GIVEN — a non-proxiable message (CER) that carries a Route-Record naming us;
        // non-proxiable messages are never relayed, so no loop can occur
        final CapabilitiesExchangeRequest.Out cerOut = new CapabilitiesExchangeRequest.Out();
        cerOut.addAVP(routeRecord(OWN_HOST));
        final IncomingRequest<?> request = parseRequest(cerOut);

        // WHEN
        final var verdict = LoopDetector.checkReceived(request, OWN_HOST);

        // THEN
        assertThat(verdict).isEmpty();
    }

    @Test
    void it_excludes_candidates_whose_identity_appears_in_a_route_record() throws Exception {
        // GIVEN
        final IncomingRequest<?> request =
                parseRequest(proxiableRequestWithRouteRecords("hss1.example.com", "hss2.example.com"));
        final DiameterPeer hss1 = mock(DiameterPeer.class);
        final DiameterPeer hss3 = mock(DiameterPeer.class);
        final Map<String, DiameterPeer> candidates = new LinkedHashMap<>();
        candidates.put("hss1.example.com", hss1); // in Route-Records → excluded (§6.1.7)
        candidates.put("hss3.example.com", hss3); // not seen → eligible

        // WHEN
        final Map<String, DiameterPeer> remaining = LoopDetector.filterCandidates(request, candidates);

        // THEN — order preserved
        assertThat(remaining).containsExactly(Map.entry("hss3.example.com", hss3));
    }

    @Test
    void it_excludes_all_candidates_when_every_identity_appears_in_a_route_record() throws Exception {
        // GIVEN
        final IncomingRequest<?> request =
                parseRequest(proxiableRequestWithRouteRecords("hss1.example.com", "hss2.example.com"));
        final Map<String, DiameterPeer> candidates = new LinkedHashMap<>();
        candidates.put("hss1.example.com", mock(DiameterPeer.class));
        candidates.put("hss2.example.com", mock(DiameterPeer.class));

        // WHEN
        final Map<String, DiameterPeer> remaining = LoopDetector.filterCandidates(request, candidates);

        // THEN — empty result signals DIAMETER_UNABLE_TO_DELIVER (3002)
        assertThat(remaining).isEmpty();
    }

    @Test
    void it_returns_candidates_unchanged_when_proxiable_bit_is_clear() throws Exception {
        // GIVEN
        final CapabilitiesExchangeRequest.Out cerOut = new CapabilitiesExchangeRequest.Out();
        cerOut.addAVP(routeRecord(OWN_HOST));
        final IncomingRequest<?> request = parseRequest(cerOut);
        final Map<String, DiameterPeer> candidates = new LinkedHashMap<>();
        candidates.put(OWN_HOST, mock(DiameterPeer.class));

        // WHEN — loop avoidance is skipped for non-proxiable messages
        final Map<String, DiameterPeer> remaining = LoopDetector.filterCandidates(request, candidates);

        // THEN
        assertThat(remaining).isEqualTo(candidates);
    }

    // ---------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------

    private static AVP routeRecord(final String host) {
        return AVP.create(new AVPKey(DiameterConstants.AVP_ROUTE_RECORD, 0), host);
    }

    /**
     * Builds a proxiable request (RAR, P-bit set) with the given Route-Records and
     * round-trips it through the encoder/parser so the loop detector sees a real
     * wire-parsed {@link IncomingRequest}.
     */
    private static ReAuthRequest.Out proxiableRequestWithRouteRecords(final String... routeRecords) {
        final ReAuthRequest.Out rar = new ReAuthRequest.Out();
        for (final String routeRecord : routeRecords) {
            rar.addAVP(routeRecord(routeRecord));
        }
        return rar;
    }

    private static IncomingRequest<?> parseRequest(final OutgoingRequest<?> out) {
        try {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            out.writeTo(new DataOutputStream(baos), new HopByHopId(1), new EndToEndId(2));
            return (IncomingRequest<?>) Command.parseMessage(ByteBuffer.wrap(baos.toByteArray()));
        } catch (final Exception e) {
            throw new UncheckedIOException(new IOException(e));
        }
    }
}
