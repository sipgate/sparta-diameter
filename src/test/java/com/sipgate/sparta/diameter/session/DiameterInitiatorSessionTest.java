package com.sipgate.sparta.diameter.session;

import com.sipgate.sparta.diameter.core.DiameterConstants;
import com.sipgate.sparta.diameter.messages.rfc6733.CapabilitiesExchangeAnswer;
import com.sipgate.sparta.diameter.messages.rfc6733.CapabilitiesExchangeRequest;
import com.sipgate.sparta.diameter.transport.DiameterPeer;
import io.netty.channel.ChannelFuture;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.UncheckedIOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DiameterInitiatorSessionTest {

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

    @Test
    void it_starts_with_closed_peer_state() {
        // GIVEN / WHEN
        final DiameterInitiatorSession session = new DiameterInitiatorSession(CONFIG, () -> {});

        // THEN
        assertThat(session.getPeerState()).isEqualTo(PeerState.CLOSED);
    }

    @Test
    void it_starts_with_initial_watchdog_state() {
        // GIVEN / WHEN
        final DiameterInitiatorSession session = new DiameterInitiatorSession(CONFIG, () -> {});

        // THEN
        assertThat(session.getWatchdogState()).isEqualTo(WatchdogState.INITIAL);
    }

    @Test
    void it_transitions_to_closed_and_down_on_disconnect() {
        // GIVEN
        final DiameterInitiatorSession session = new DiameterInitiatorSession(CONFIG, () -> {});

        // WHEN
        session.onDisconnected(null);

        // THEN
        assertThat(session.getPeerState()).isEqualTo(PeerState.CLOSED);
        assertThat(session.getWatchdogState()).isEqualTo(WatchdogState.DOWN);
    }

    @Test
    void it_sends_a_CER_on_connected() {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        when(peer.send(any())).thenReturn(mock(ChannelFuture.class));
        final DiameterInitiatorSession session = new DiameterInitiatorSession(CONFIG, () -> {});

        // WHEN
        session.onConnected(peer);

        // THEN
        verify(peer).send(any(CapabilitiesExchangeRequest.class));
    }

    @Test
    void it_sets_origin_host_in_the_CER() {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        when(peer.send(any())).thenReturn(mock(ChannelFuture.class));
        final DiameterInitiatorSession session = new DiameterInitiatorSession(CONFIG, () -> {});

        // WHEN
        session.onConnected(peer);

        // THEN
        final ArgumentCaptor<CapabilitiesExchangeRequest> captor =
                ArgumentCaptor.forClass(CapabilitiesExchangeRequest.class);
        verify(peer).send(captor.capture());
        assertThat(captor.getValue().getOriginHost()).isEqualTo("hss.example.com");
    }

    @Test
    void it_transitions_to_WAIT_I_CEA_on_connected() {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        when(peer.send(any())).thenReturn(mock(ChannelFuture.class));
        final DiameterInitiatorSession session = new DiameterInitiatorSession(CONFIG, () -> {});

        // WHEN
        session.onConnected(peer);

        // THEN
        assertThat(session.getPeerState()).isEqualTo(PeerState.WAIT_I_CEA);
    }

    @Test
    void it_transitions_to_I_OPEN_on_successful_CEA() {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        when(peer.send(any())).thenReturn(mock(ChannelFuture.class));
        final DiameterInitiatorSession session = new DiameterInitiatorSession(CONFIG, () -> {});
        session.onConnected(peer);
        final CapabilitiesExchangeAnswer cea = CapabilitiesExchangeAnswer.create(1, 2);
        cea.setResultCode(DiameterConstants.RES_DIAMETER_SUCCESS);

        // WHEN
        session.onMessage(peer, cea);

        // THEN
        assertThat(session.getPeerState()).isEqualTo(PeerState.I_OPEN);
    }

    @Test
    void it_transitions_to_CLOSED_on_failed_CEA() {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        when(peer.send(any())).thenReturn(mock(ChannelFuture.class));
        when(peer.close()).thenReturn(mock(ChannelFuture.class));
        final DiameterInitiatorSession session = new DiameterInitiatorSession(CONFIG, () -> {});
        session.onConnected(peer);
        final CapabilitiesExchangeAnswer cea = CapabilitiesExchangeAnswer.create(1, 2);
        cea.setResultCode(DiameterConstants.RES_DIAMETER_NO_COMMON_APPLICATION);

        // WHEN
        session.onMessage(peer, cea);

        // THEN
        assertThat(session.getPeerState()).isEqualTo(PeerState.CLOSED);
    }

    @Test
    void it_closes_peer_on_failed_CEA() {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        when(peer.send(any())).thenReturn(mock(ChannelFuture.class));
        when(peer.close()).thenReturn(mock(ChannelFuture.class));
        final DiameterInitiatorSession session = new DiameterInitiatorSession(CONFIG, () -> {});
        session.onConnected(peer);
        final CapabilitiesExchangeAnswer cea = CapabilitiesExchangeAnswer.create(1, 2);
        cea.setResultCode(DiameterConstants.RES_DIAMETER_NO_COMMON_APPLICATION);

        // WHEN
        session.onMessage(peer, cea);

        // THEN
        verify(peer).close();
    }
}
