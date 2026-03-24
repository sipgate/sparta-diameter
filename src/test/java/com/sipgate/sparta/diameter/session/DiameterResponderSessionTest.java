package com.sipgate.sparta.diameter.session;

import com.sipgate.sparta.diameter.core.DiameterConstants;
import com.sipgate.sparta.diameter.core.DiameterMessageFactory;
import com.sipgate.sparta.diameter.core.avp.AVP;
import com.sipgate.sparta.diameter.messages.rfc6733.CapabilitiesExchangeAnswer;
import com.sipgate.sparta.diameter.messages.rfc6733.CapabilitiesExchangeRequest;
import com.sipgate.sparta.diameter.messages.rfc6733.DeviceWatchdogAnswer;
import com.sipgate.sparta.diameter.messages.rfc6733.DeviceWatchdogRequest;
import com.sipgate.sparta.diameter.messages.rfc6733.DisconnectPeerAnswer;
import com.sipgate.sparta.diameter.messages.rfc6733.DisconnectPeerRequest;
import com.sipgate.sparta.diameter.messages.rfc6733.ReAuthAnswer;
import com.sipgate.sparta.diameter.messages.rfc6733.ReAuthRequest;
import com.sipgate.sparta.diameter.transport.DiameterPeer;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoop;
import io.netty.util.concurrent.ScheduledFuture;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.UncheckedIOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DiameterResponderSessionTest {

    private static final InetAddress LOCALHOST;

    static {
        try {
            LOCALHOST = InetAddress.getByName("127.0.0.1");
        } catch (final UnknownHostException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static final DiameterNodeConfig CONFIG = new DiameterNodeConfig(
            "hss.example.com",
            "example.com",
            Collections.singletonList(LOCALHOST),
            0L,
            "sparta",
            new DiameterNodeConfig.Capabilities(
                    Collections.emptyList(), Collections.emptyList(), Collections.emptyList()));

    private static final DiameterNodeConfig CONFIG_WITH_AUTH_APP = new DiameterNodeConfig(
            "hss.example.com",
            "example.com",
            Collections.singletonList(LOCALHOST),
            0L,
            "sparta",
            new DiameterNodeConfig.Capabilities(
                    Collections.singletonList(5), Collections.emptyList(), Collections.emptyList()));

    @Test
    void it_starts_with_closed_peer_state() {
        // GIVEN / WHEN
        final DiameterResponderSession session = new DiameterResponderSession(CONFIG);

        // THEN
        assertThat(session.getPeerState()).isEqualTo(PeerState.CLOSED);
    }

    @Test
    void it_starts_with_initial_watchdog_state() {
        // GIVEN / WHEN
        final DiameterResponderSession session = new DiameterResponderSession(CONFIG);

        // THEN
        assertThat(session.getWatchdogState()).isEqualTo(WatchdogState.INITIAL);
    }

    @Test
    void it_transitions_to_closed_and_down_on_disconnect() {
        // GIVEN
        final DiameterResponderSession session = new DiameterResponderSession(CONFIG);

        // WHEN
        session.onDisconnected(null);

        // THEN
        assertThat(session.getPeerState()).isEqualTo(PeerState.CLOSED);
        assertThat(session.getWatchdogState()).isEqualTo(WatchdogState.DOWN);
    }

    @Test
    void it_transitions_to_R_OPEN_on_CER_with_common_application() {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        when(peer.send(any())).thenReturn(mock(ChannelFuture.class));
        stubEventLoop(peer);
        final DiameterResponderSession session = new DiameterResponderSession(CONFIG_WITH_AUTH_APP);
        session.onConnected(peer);
        final CapabilitiesExchangeRequest cer = DiameterMessageFactory.create(CapabilitiesExchangeRequest.class, 1, 2);
        cer.addAVP(AVP.create(DiameterConstants.AVP_AUTH_APPLICATION_ID, 5L));

        // WHEN
        session.onMessage(peer, cer);

        // THEN
        assertThat(session.getPeerState()).isEqualTo(PeerState.R_OPEN);
    }

    @Test
    void it_sends_CEA_with_success_on_common_application() {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        when(peer.send(any())).thenReturn(mock(ChannelFuture.class));
        stubEventLoop(peer);
        final DiameterResponderSession session = new DiameterResponderSession(CONFIG_WITH_AUTH_APP);
        session.onConnected(peer);
        final CapabilitiesExchangeRequest cer = DiameterMessageFactory.create(CapabilitiesExchangeRequest.class, 1, 2);
        cer.addAVP(AVP.create(DiameterConstants.AVP_AUTH_APPLICATION_ID, 5L));

        // WHEN
        session.onMessage(peer, cer);

        // THEN
        final ArgumentCaptor<CapabilitiesExchangeAnswer> captor =
                ArgumentCaptor.forClass(CapabilitiesExchangeAnswer.class);
        verify(peer).send(captor.capture());
        assertThat(captor.getValue().getResultCode()).isEqualTo(DiameterConstants.RES_DIAMETER_SUCCESS);
    }

    @Test
    void it_transitions_to_CLOSED_on_CER_with_no_common_application() {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        when(peer.send(any())).thenReturn(mock(ChannelFuture.class));
        when(peer.close()).thenReturn(mock(ChannelFuture.class));
        final DiameterResponderSession session = new DiameterResponderSession(CONFIG_WITH_AUTH_APP);
        session.onConnected(peer);
        final CapabilitiesExchangeRequest cer = DiameterMessageFactory.create(CapabilitiesExchangeRequest.class, 1, 2);
        cer.addAVP(AVP.create(DiameterConstants.AVP_AUTH_APPLICATION_ID, 99L));

        // WHEN
        session.onMessage(peer, cer);

        // THEN
        assertThat(session.getPeerState()).isEqualTo(PeerState.CLOSED);
    }

    @Test
    void it_sends_CEA_with_no_common_application_result_code_on_no_match() {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        when(peer.send(any())).thenReturn(mock(ChannelFuture.class));
        when(peer.close()).thenReturn(mock(ChannelFuture.class));
        final DiameterResponderSession session = new DiameterResponderSession(CONFIG_WITH_AUTH_APP);
        session.onConnected(peer);
        final CapabilitiesExchangeRequest cer = DiameterMessageFactory.create(CapabilitiesExchangeRequest.class, 1, 2);
        cer.addAVP(AVP.create(DiameterConstants.AVP_AUTH_APPLICATION_ID, 99L));

        // WHEN
        session.onMessage(peer, cer);

        // THEN
        final ArgumentCaptor<CapabilitiesExchangeAnswer> captor =
                ArgumentCaptor.forClass(CapabilitiesExchangeAnswer.class);
        verify(peer).send(captor.capture());
        assertThat(captor.getValue().getResultCode()).isEqualTo(DiameterConstants.RES_DIAMETER_NO_COMMON_APPLICATION);
    }

    @Test
    void it_closes_peer_on_no_common_application() {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        when(peer.send(any())).thenReturn(mock(ChannelFuture.class));
        when(peer.close()).thenReturn(mock(ChannelFuture.class));
        final DiameterResponderSession session = new DiameterResponderSession(CONFIG_WITH_AUTH_APP);
        session.onConnected(peer);
        final CapabilitiesExchangeRequest cer = DiameterMessageFactory.create(CapabilitiesExchangeRequest.class, 1, 2);
        cer.addAVP(AVP.create(DiameterConstants.AVP_AUTH_APPLICATION_ID, 99L));

        // WHEN
        session.onMessage(peer, cer);

        // THEN
        verify(peer).close();
    }

    // -------------------------------------------------------------------------
    // Handler binding
    // -------------------------------------------------------------------------

    @Test
    void it_throws_when_registering_a_second_handler_for_the_same_command_code() {
        // GIVEN
        final DiameterResponderSession session = new DiameterResponderSession(CONFIG);
        session.setHandler(ReAuthRequest.class, req -> new CompletableFuture<>());

        // WHEN / THEN
        assertThatThrownBy(() -> session.setHandler(ReAuthRequest.class, req -> new CompletableFuture<>()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void it_dispatches_inbound_request_to_registered_handler() {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        final DiameterResponderSession session = openedSession(peer);
        final ReAuthRequest rar = DiameterMessageFactory.create(ReAuthRequest.class, 10, 20);
        final ReAuthAnswer answer = DiameterMessageFactory.createAnswer(rar, DiameterConstants.RES_DIAMETER_SUCCESS);
        session.setHandler(ReAuthRequest.class, req -> CompletableFuture.completedFuture(answer));

        // WHEN
        session.onMessage(peer, rar);

        // THEN
        verify(peer).send(answer);
    }

    @Test
    void it_sends_COMMAND_UNSUPPORTED_for_unhandled_request() {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        final DiameterResponderSession session = openedSession(peer);

        // WHEN
        session.onMessage(peer, DiameterMessageFactory.create(ReAuthRequest.class, 10, 20));

        // THEN
        final ArgumentCaptor<ReAuthAnswer> captor = ArgumentCaptor.forClass(ReAuthAnswer.class);
        verify(peer).send(captor.capture());
        assertThat(captor.getValue().getResultCode())
                .isEqualTo(DiameterConstants.RES_DIAMETER_COMMAND_UNSUPPORTED);
    }

    @Test
    void it_sends_UNABLE_TO_COMPLY_when_handler_future_fails() {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        final DiameterResponderSession session = openedSession(peer);
        final CompletableFuture<ReAuthAnswer> failing = new CompletableFuture<>();
        failing.completeExceptionally(new RuntimeException("handler error"));
        session.setHandler(ReAuthRequest.class, req -> failing);

        // WHEN
        session.onMessage(peer, DiameterMessageFactory.create(ReAuthRequest.class, 10, 20));

        // THEN
        final ArgumentCaptor<ReAuthAnswer> captor = ArgumentCaptor.forClass(ReAuthAnswer.class);
        verify(peer).send(captor.capture());
        assertThat(captor.getValue().getResultCode())
                .isEqualTo(DiameterConstants.RES_DIAMETER_UNABLE_TO_COMPLY);
    }

    // -------------------------------------------------------------------------
    // DWR/DWA watchdog
    // -------------------------------------------------------------------------

    @Test
    void it_transitions_watchdog_to_OKAY_on_entering_R_OPEN() {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        final DiameterResponderSession session = openedSession(peer);

        // WHEN / THEN - watchdog is OKAY after CER exchange
        assertThat(session.getWatchdogState()).isEqualTo(WatchdogState.OKAY);
    }

    @Test
    void it_sends_DWA_when_DWR_received_in_R_OPEN() {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        final DiameterResponderSession session = openedSession(peer);
        final DeviceWatchdogRequest dwr = DiameterMessageFactory.create(DeviceWatchdogRequest.class, 77, 88);

        // WHEN
        session.onMessage(peer, dwr);

        // THEN
        final ArgumentCaptor<DeviceWatchdogAnswer> captor =
                ArgumentCaptor.forClass(DeviceWatchdogAnswer.class);
        verify(peer).send(captor.capture());
        assertThat(captor.getValue().getHopByHopIdentifier()).isEqualTo(77);
    }

    // -------------------------------------------------------------------------
    // DPR/DPA - graceful shutdown
    // -------------------------------------------------------------------------

    @Test
    void it_transitions_to_CLOSING_when_stop_is_called() {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        final DiameterResponderSession session = openedSession(peer);

        // WHEN
        session.stop();

        // THEN
        assertThat(session.getPeerState()).isEqualTo(PeerState.CLOSING);
    }

    @Test
    void it_sends_DPR_when_stop_is_called() {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        final DiameterResponderSession session = openedSession(peer);

        // WHEN
        session.stop();

        // THEN
        verify(peer).send(any(DisconnectPeerRequest.class));
    }

    @Test
    void it_rejects_send_in_CLOSING_state() {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        final DiameterResponderSession session = openedSession(peer);
        session.stop();

        // WHEN
        final CompletableFuture<CapabilitiesExchangeAnswer> future =
                session.send(DiameterMessageFactory.create(CapabilitiesExchangeRequest.class, 1, 2));

        // THEN
        assertThat(future).isCompletedExceptionally();
    }

    @Test
    void it_transitions_to_CLOSED_when_DPA_arrives_in_CLOSING() {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        when(peer.close()).thenReturn(mock(ChannelFuture.class));
        final DiameterResponderSession session = openedSession(peer);
        session.stop();
        final ArgumentCaptor<DisconnectPeerRequest> dprCaptor =
                ArgumentCaptor.forClass(DisconnectPeerRequest.class);
        verify(peer).send(dprCaptor.capture());
        Mockito.clearInvocations(peer);
        final DisconnectPeerAnswer dpa =
                DiameterMessageFactory.createAnswer(dprCaptor.getValue(), DiameterConstants.RES_DIAMETER_SUCCESS);

        // WHEN
        session.onMessage(peer, dpa);

        // THEN
        assertThat(session.getPeerState()).isEqualTo(PeerState.CLOSED);
    }

    @Test
    void it_transitions_to_CLOSING_when_DPR_received_in_R_OPEN() {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        when(peer.close()).thenReturn(mock(ChannelFuture.class));
        final DiameterResponderSession session = openedSession(peer);

        // WHEN
        session.onMessage(peer, DiameterMessageFactory.create(DisconnectPeerRequest.class, 10, 20));

        // THEN
        assertThat(session.getPeerState()).isEqualTo(PeerState.CLOSING);
    }

    @Test
    void it_sends_DPA_when_DPR_received_in_R_OPEN() {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        when(peer.close()).thenReturn(mock(ChannelFuture.class));
        final DiameterResponderSession session = openedSession(peer);

        // WHEN
        session.onMessage(peer, DiameterMessageFactory.create(DisconnectPeerRequest.class, 10, 20));

        // THEN
        final ArgumentCaptor<DisconnectPeerAnswer> captor =
                ArgumentCaptor.forClass(DisconnectPeerAnswer.class);
        verify(peer).send(captor.capture());
        assertThat(captor.getValue().getHopByHopIdentifier()).isEqualTo(10);
    }

    @Test
    void it_transitions_to_CLOSED_on_disconnect_after_inbound_DPR() {
        // GIVEN: peer sends DPR, we respond with DPA and close
        final DiameterPeer peer = mock(DiameterPeer.class);
        when(peer.close()).thenReturn(mock(ChannelFuture.class));
        final DiameterResponderSession session = openedSession(peer);
        session.onMessage(peer, DiameterMessageFactory.create(DisconnectPeerRequest.class, 10, 20));

        // WHEN - channel closes after DPA was sent
        session.onDisconnected(peer);

        // THEN
        assertThat(session.getPeerState()).isEqualTo(PeerState.CLOSED);
    }

    @Test
    void it_closes_peer_when_DPR_received_in_R_OPEN() {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        when(peer.close()).thenReturn(mock(ChannelFuture.class));
        final DiameterResponderSession session = openedSession(peer);

        // WHEN
        session.onMessage(peer, DiameterMessageFactory.create(DisconnectPeerRequest.class, 10, 20));

        // THEN
        verify(peer).close();
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static DiameterResponderSession openedSession(final DiameterPeer peer) {
        when(peer.send(any())).thenReturn(mock(ChannelFuture.class));
        stubEventLoop(peer);
        final DiameterResponderSession session = new DiameterResponderSession(CONFIG_WITH_AUTH_APP);
        session.onConnected(peer);
        final CapabilitiesExchangeRequest cer = DiameterMessageFactory.create(CapabilitiesExchangeRequest.class, 1, 2);
        cer.addAVP(AVP.create(DiameterConstants.AVP_AUTH_APPLICATION_ID, 5L));
        session.onMessage(peer, cer);
        Mockito.clearInvocations(peer);
        return session;
    }

    @SuppressWarnings("unchecked")
    private static void stubEventLoop(final DiameterPeer peer) {
        final EventLoop eventLoop = mock(EventLoop.class);
        when(peer.eventLoop()).thenReturn(eventLoop);
        when(eventLoop.schedule(any(Runnable.class), anyLong(), any(TimeUnit.class)))
                .thenReturn(mock(ScheduledFuture.class));
    }
}
