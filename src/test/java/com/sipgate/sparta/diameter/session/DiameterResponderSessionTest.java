package com.sipgate.sparta.diameter.session;

import com.sipgate.sparta.diameter.core.DiameterConstants;
import com.sipgate.sparta.diameter.core.avp.AVP;
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
        final DiameterResponderSession session = new DiameterResponderSession(CONFIG_WITH_AUTH_APP);
        session.onConnected(peer);
        final CapabilitiesExchangeRequest cer = CapabilitiesExchangeRequest.create(1, 2);
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
        final DiameterResponderSession session = new DiameterResponderSession(CONFIG_WITH_AUTH_APP);
        session.onConnected(peer);
        final CapabilitiesExchangeRequest cer = CapabilitiesExchangeRequest.create(1, 2);
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
        final CapabilitiesExchangeRequest cer = CapabilitiesExchangeRequest.create(1, 2);
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
        final CapabilitiesExchangeRequest cer = CapabilitiesExchangeRequest.create(1, 2);
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
        final CapabilitiesExchangeRequest cer = CapabilitiesExchangeRequest.create(1, 2);
        cer.addAVP(AVP.create(DiameterConstants.AVP_AUTH_APPLICATION_ID, 99L));

        // WHEN
        session.onMessage(peer, cer);

        // THEN
        verify(peer).close();
    }
}
