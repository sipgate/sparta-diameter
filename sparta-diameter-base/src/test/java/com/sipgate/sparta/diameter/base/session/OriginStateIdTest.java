package com.sipgate.sparta.diameter.base.session;

import com.sipgate.sparta.diameter.base.core.Command;
import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.DiameterMessageFactory;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.IncomingRequest;
import com.sipgate.sparta.diameter.base.messages.CapabilitiesExchangeAnswer;
import com.sipgate.sparta.diameter.base.messages.CapabilitiesExchangeRequest;
import com.sipgate.sparta.diameter.base.transport.DiameterPeer;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoop;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.ScheduledFuture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Origin-State-Id (RFC 6733 §8.16): the session stamps the configured value into
 * CER/CEA, records the peer's value from the received CER/CEA, and detects peer
 * restarts across reconnects (strictly higher value than the last known one).
 */
class OriginStateIdTest {

    private static final InetAddress LOCALHOST;

    static {
        try {
            LOCALHOST = InetAddress.getByName("127.0.0.1");
        } catch (final UnknownHostException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static final DiameterNodeConfig.Capabilities CAPABILITIES = new DiameterNodeConfig.Capabilities(
            Collections.singletonList(5L), Collections.emptyList(), Collections.emptyList(), Collections.emptyList());

    private static final DiameterNodeConfig CONFIG_WITH_STATE_ID = new DiameterNodeConfig(
            "dra.example.com", "example.com", Collections.singletonList(LOCALHOST),
            0L, "sparta", CAPABILITIES,
            DiameterNodeConfig.TWINIT_DEFAULT, DiameterNodeConfig.TC_RECOMMENDED,
            DiameterNodeConfig.REQUEST_TIMEOUT_DEFAULT, 123L);

    @AfterEach
    void clearPeerRestartMemory() {
        DiameterSession.clearPeerRestartMemoryForTests();
    }

    // -------------------------------------------------------------------------
    // Stamping
    // -------------------------------------------------------------------------

    @Test
    void it_stamps_the_configured_origin_state_id_into_the_CER() {
        // GIVEN
        final DiameterInitiatorSession session = new DiameterInitiatorSession(CONFIG_WITH_STATE_ID, (ignored) -> {});

        // WHEN
        final CapabilitiesExchangeRequest.Out cer = session.buildCerForTest();

        // THEN
        assertThat(cer.getOriginStateId()).isEqualTo(123L);
    }

    @Test
    void it_omits_the_origin_state_id_from_the_CER_when_not_configured() {
        // GIVEN — the value 0 means "do not allow peers to infer termination from it"
        // (RFC 6733 §8.16); default constructors create such a config
        final DiameterNodeConfig config = new DiameterNodeConfig(
                "dra.example.com", "example.com", Collections.singletonList(LOCALHOST),
                0L, "sparta", CAPABILITIES);
        final DiameterInitiatorSession session = new DiameterInitiatorSession(config, (ignored) -> {});

        // WHEN
        final CapabilitiesExchangeRequest.Out cer = session.buildCerForTest();

        // THEN
        assertThat(cer.getOriginStateId()).isEqualTo(-1L);
    }

    @Test
    void it_stamps_the_configured_origin_state_id_into_the_CEA() throws Exception {
        // GIVEN
        final DiameterResponderSession session = new DiameterResponderSession(CONFIG_WITH_STATE_ID);

        // WHEN
        final CapabilitiesExchangeAnswer.Out cea = session.buildCeaForTest();

        // THEN
        assertThat(cea.getOriginStateId()).isEqualTo(123L);
    }

    // -------------------------------------------------------------------------
    // Peer restart detection
    // -------------------------------------------------------------------------

    @Test
    void it_records_the_remote_origin_state_id_from_a_CER() throws Exception {
        // GIVEN — OPEN responder session
        final DiameterPeer peer = mock(DiameterPeer.class);
        final DiameterResponderSession session = openedSession(peer);

        // WHEN — the CER carries Origin-Host + Origin-State-Id
        session.onMessage(incomingCer("client-a1.example.com", 5L));

        // THEN
        assertThat(DiameterSession.lastKnownRemoteOriginStateId("client-a1.example.com")).isEqualTo(5L);
    }

    @Test
    void it_records_the_remote_origin_state_id_from_a_CEA() throws Exception {
        // GIVEN — initiator in WAIT_I_CEA
        final DiameterPeer peer = mock(DiameterPeer.class);
        final DiameterInitiatorSession session = connectedInitiator(peer);

        // WHEN — the CEA carries Origin-Host + Origin-State-Id
        session.onMessage(incomingCea(peer, "hss-b1.example.com", 77L));

        // THEN
        assertThat(DiameterSession.lastKnownRemoteOriginStateId("hss-b1.example.com")).isEqualTo(77L);
    }

    @Test
    void it_detects_a_peer_restart_when_the_origin_state_id_increases() throws Exception {
        // GIVEN — first connection saw state id 5
        final DiameterPeer peer = mock(DiameterPeer.class);
        final DiameterResponderSession session = openedSession(peer);
        session.onMessage(incomingCer("client-c1.example.com", 5L));

        // WHEN — newer session from the same peer presents a higher id (reconnect
        // after restart; sessions are per-connection instances, ADR-0004)
        session.onMessage(incomingCer("client-c1.example.com", 7L));

        // THEN — restart detected (WARN logged per recordRemoteOriginStateId); memory
        // holds the latest value
        assertThat(DiameterSession.lastKnownRemoteOriginStateId("client-c1.example.com")).isEqualTo(7L);
    }

    @Test
    void it_keeps_a_lower_origin_state_id_without_restart_warning_and_overwrites() throws Exception {
        // GIVEN — first connection saw state id 9
        final DiameterPeer peer = mock(DiameterPeer.class);
        final DiameterResponderSession session = openedSession(peer);
        session.onMessage(incomingCer("client-d1.example.com", 9L));

        // WHEN — misbehaving peer presents a lower id (RFC 6733 §8.16 mandates
        // monotonic increase); recorded without restart warning
        session.onMessage(incomingCer("client-d1.example.com", 3L));

        // THEN — new value recorded, no restart inferred
        assertThat(DiameterSession.lastKnownRemoteOriginStateId("client-d1.example.com")).isEqualTo(3L);
    }

    @Test
    void it_ignores_a_CER_without_origin_state_id() throws Exception {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        final DiameterResponderSession session = openedSession(peer);

        // WHEN — CER with Origin-Host but no Origin-State-Id (mixin returns -1)
        session.onMessage(incomingCer("client-e1.example.com", -1L));

        // THEN — nothing recorded
        assertThat(DiameterSession.lastKnownRemoteOriginStateId("client-e1.example.com")).isEqualTo(-1L);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static DiameterResponderSession openedSession(final DiameterPeer peer) throws Exception {
        stubPeer(peer);
        final DiameterResponderSession session = new DiameterResponderSession(CONFIG_WITH_STATE_ID);
        session.onConnected(peer);
        Mockito.clearInvocations(peer);
        return session;
    }

    private static DiameterInitiatorSession connectedInitiator(final DiameterPeer peer) throws Exception {
        stubPeer(peer);
        final DiameterInitiatorSession session = new DiameterInitiatorSession(CONFIG_WITH_STATE_ID, (ignored) -> {});
        session.onConnected(peer);
        // NOTE: no clearInvocations here — incomingCea() captures the CER's identifiers
        // from this peer to build the matching CEA (RFC 6733 §6.2).
        return session;
    }

    private static void stubPeer(final DiameterPeer peer) {
        final EventLoop eventLoop = mock(EventLoop.class);
        when(peer.eventLoop()).thenReturn(eventLoop);
        when(eventLoop.schedule(any(Runnable.class), anyLong(), any(TimeUnit.class)))
                .thenReturn(mock(ScheduledFuture.class));
        when(peer.send(any(com.sipgate.sparta.diameter.base.core.OutgoingRequest.class),
                any(HopByHopId.class), any(EndToEndId.class)))
                .thenReturn(mock(ChannelFuture.class));
        when(peer.send(any(com.sipgate.sparta.diameter.base.core.OutgoingAnswer.class)))
                .thenAnswer(ignored -> {
                    final ChannelFuture future = mock(ChannelFuture.class);
                    when(future.addListener(any())).thenAnswer(inv -> {
                        ((GenericFutureListener) inv.getArgument(0)).operationComplete(future);
                        return future;
                    });
                    return future;
                });
    }

    private static CapabilitiesExchangeRequest.In incomingCer(final String originHost, final long originStateId)
            throws Exception {
        final CapabilitiesExchangeRequest.Out cerOut = new CapabilitiesExchangeRequest.Out();
        cerOut.addAuthApplicationId(5L);
        if (originHost != null) {
            cerOut.setOriginHost(originHost);
        }
        if (originStateId >= 0) {
            cerOut.setOriginStateId(originStateId);
        }
        return (CapabilitiesExchangeRequest.In) toIncoming(cerOut, new HopByHopId(1), new EndToEndId(2));
    }

    /**
     * Builds a CEA matching the CER the session actually sent: captures the CER's
     * identifiers from the mocked peer (RFC 6733 §6.2 answers echo the request's
     * hop-by-hop/end-to-end identifiers) and round-trips the answer through the parser.
     */
    private static CapabilitiesExchangeAnswer.In incomingCea(final DiameterPeer peer, final String originHost,
                                                             final long originStateId) throws Exception {
        final ArgumentCaptor<HopByHopId> hbh = ArgumentCaptor.forClass(HopByHopId.class);
        final ArgumentCaptor<EndToEndId> e2e = ArgumentCaptor.forClass(EndToEndId.class);
        verify(peer).send(any(com.sipgate.sparta.diameter.base.core.OutgoingRequest.class),
                hbh.capture(), e2e.capture());

        final CapabilitiesExchangeRequest.Out cerOut = new CapabilitiesExchangeRequest.Out();
        cerOut.setOriginHost("peer.example.com");
        final CapabilitiesExchangeRequest.In cer =
                (CapabilitiesExchangeRequest.In) toIncoming(cerOut, hbh.getValue(), e2e.getValue());
        final CapabilitiesExchangeAnswer.Out ceaOut =
                DiameterMessageFactory.createAnswer(cer, DiameterConstants.RES_DIAMETER_SUCCESS);
        ceaOut.setOriginHost(originHost);
        ceaOut.setOriginStateId(originStateId);
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ceaOut.writeTo(new DataOutputStream(baos));
        return (CapabilitiesExchangeAnswer.In) Command.parseMessage(ByteBuffer.wrap(baos.toByteArray()));
    }

    private static IncomingRequest<?> toIncoming(final com.sipgate.sparta.diameter.base.core.OutgoingRequest<?> out,
                                                 final HopByHopId hbh, final EndToEndId e2e) throws Exception {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        out.writeTo(new DataOutputStream(baos), hbh, e2e);
        return (IncomingRequest<?>) Command.parseMessage(ByteBuffer.wrap(baos.toByteArray()));
    }
}
