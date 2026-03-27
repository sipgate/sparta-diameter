package com.sipgate.sparta.diameter.base.session;

import com.sipgate.sparta.diameter.base.core.Command;
import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.DiameterMessageFactory;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.IncomingAnswer;
import com.sipgate.sparta.diameter.base.core.IncomingRequest;
import com.sipgate.sparta.diameter.base.core.OutgoingAnswer;
import com.sipgate.sparta.diameter.base.core.OutgoingRequest;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;
import com.sipgate.sparta.diameter.base.messages.CapabilitiesExchangeAnswer;
import com.sipgate.sparta.diameter.base.messages.CapabilitiesExchangeRequest;
import com.sipgate.sparta.diameter.base.messages.DeviceWatchdogAnswer;
import com.sipgate.sparta.diameter.base.messages.DeviceWatchdogRequest;
import com.sipgate.sparta.diameter.base.messages.DisconnectPeerAnswer;
import com.sipgate.sparta.diameter.base.messages.DisconnectPeerRequest;
import com.sipgate.sparta.diameter.base.messages.ReAuthAnswer;
import com.sipgate.sparta.diameter.base.messages.ReAuthRequest;
import com.sipgate.sparta.diameter.base.transport.DiameterPeer;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoop;
import io.netty.util.concurrent.ScheduledFuture;
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
import java.util.List;
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
                    Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList()));

    private static final DiameterNodeConfig CONFIG_WITH_AUTH_APP = new DiameterNodeConfig(
            "hss.example.com",
            "example.com",
            Collections.singletonList(LOCALHOST),
            0L,
            "sparta",
            new DiameterNodeConfig.Capabilities(
                    Collections.singletonList(5L), Collections.emptyList(), Collections.emptyList(), Collections.emptyList()));

    // 3GPP SGd: appId 16777313, vendor 10415
    private static final long SGD_APP_ID = 16777313L;
    private static final long VENDOR_3GPP = 10415L;

    private static final DiameterNodeConfig CONFIG_WITH_VENDOR_SPECIFIC_APP = new DiameterNodeConfig(
            "hss.example.com",
            "example.com",
            Collections.singletonList(LOCALHOST),
            0L,
            "sparta",
            new DiameterNodeConfig.Capabilities(
                    Collections.emptyList(),
                    Collections.emptyList(),
                    Collections.singletonList(VENDOR_3GPP),
                    Collections.singletonList(new DiameterNodeConfig.VendorSpecificApp(VENDOR_3GPP, SGD_APP_ID))));

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
    void it_transitions_to_R_OPEN_on_CER_with_common_application() throws Exception {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        stubSend(peer);
        stubEventLoop(peer);
        final DiameterResponderSession session = new DiameterResponderSession(CONFIG_WITH_AUTH_APP);
        session.onConnected(peer);
        final CapabilitiesExchangeRequest.In cer = buildIncomingCer(5L);

        // WHEN
        session.onMessage(peer, cer);

        // THEN
        assertThat(session.getPeerState()).isEqualTo(PeerState.R_OPEN);
    }

    @Test
    void it_sends_CEA_with_success_on_common_application() throws Exception {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        stubSend(peer);
        stubEventLoop(peer);
        final DiameterResponderSession session = new DiameterResponderSession(CONFIG_WITH_AUTH_APP);
        session.onConnected(peer);
        final CapabilitiesExchangeRequest.In cer = buildIncomingCer(5L);

        // WHEN
        session.onMessage(peer, cer);

        // THEN
        final ArgumentCaptor<CapabilitiesExchangeAnswer.Out> captor =
                ArgumentCaptor.forClass(CapabilitiesExchangeAnswer.Out.class);
        verify(peer).send(captor.capture());
        assertThat(captor.getValue().getResultCode()).isEqualTo(DiameterConstants.RES_DIAMETER_SUCCESS);
    }

    @Test
    void it_transitions_to_CLOSED_on_CER_with_no_common_application() throws Exception {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        stubSend(peer);
        when(peer.close()).thenReturn(mock(ChannelFuture.class));
        final DiameterResponderSession session = new DiameterResponderSession(CONFIG_WITH_AUTH_APP);
        session.onConnected(peer);
        final CapabilitiesExchangeRequest.In cer = buildIncomingCer(99L);

        // WHEN
        session.onMessage(peer, cer);

        // THEN
        assertThat(session.getPeerState()).isEqualTo(PeerState.CLOSED);
    }

    @Test
    void it_sends_CEA_with_no_common_application_result_code_on_no_match() throws Exception {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        stubSend(peer);
        when(peer.close()).thenReturn(mock(ChannelFuture.class));
        final DiameterResponderSession session = new DiameterResponderSession(CONFIG_WITH_AUTH_APP);
        session.onConnected(peer);
        final CapabilitiesExchangeRequest.In cer = buildIncomingCer(99L);

        // WHEN
        session.onMessage(peer, cer);

        // THEN
        final ArgumentCaptor<CapabilitiesExchangeAnswer.Out> captor =
                ArgumentCaptor.forClass(CapabilitiesExchangeAnswer.Out.class);
        verify(peer).send(captor.capture());
        assertThat(captor.getValue().getResultCode()).isEqualTo(DiameterConstants.RES_DIAMETER_NO_COMMON_APPLICATION);
    }

    @Test
    void it_closes_peer_on_no_common_application() throws Exception {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        stubSend(peer);
        when(peer.close()).thenReturn(mock(ChannelFuture.class));
        final DiameterResponderSession session = new DiameterResponderSession(CONFIG_WITH_AUTH_APP);
        session.onConnected(peer);
        final CapabilitiesExchangeRequest.In cer = buildIncomingCer(99L);

        // WHEN
        session.onMessage(peer, cer);

        // THEN
        verify(peer).close();
    }

    @Test
    void it_transitions_to_R_OPEN_on_CER_with_matching_vendor_specific_app() throws Exception {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        stubSend(peer);
        stubEventLoop(peer);
        final DiameterResponderSession session = new DiameterResponderSession(CONFIG_WITH_VENDOR_SPECIFIC_APP);
        session.onConnected(peer);
        final CapabilitiesExchangeRequest.In cer = buildIncomingCerWithVendorSpecificAppId(VENDOR_3GPP, SGD_APP_ID);

        // WHEN
        session.onMessage(peer, cer);

        // THEN
        assertThat(session.getPeerState()).isEqualTo(PeerState.R_OPEN);
    }

    @Test
    void it_sends_CEA_success_on_CER_with_matching_vendor_specific_app() throws Exception {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        stubSend(peer);
        stubEventLoop(peer);
        final DiameterResponderSession session = new DiameterResponderSession(CONFIG_WITH_VENDOR_SPECIFIC_APP);
        session.onConnected(peer);
        final CapabilitiesExchangeRequest.In cer = buildIncomingCerWithVendorSpecificAppId(VENDOR_3GPP, SGD_APP_ID);

        // WHEN
        session.onMessage(peer, cer);

        // THEN
        final ArgumentCaptor<CapabilitiesExchangeAnswer.Out> captor =
                ArgumentCaptor.forClass(CapabilitiesExchangeAnswer.Out.class);
        verify(peer).send(captor.capture());
        assertThat(captor.getValue().getResultCode()).isEqualTo(DiameterConstants.RES_DIAMETER_SUCCESS);
    }

    @Test
    void it_ignores_vendor_id_in_vendor_specific_app_during_negotiation() throws Exception {
        // GIVEN: local configured with vendor 10415, remote sends same app ID but different vendor ID
        final DiameterPeer peer = mock(DiameterPeer.class);
        stubSend(peer);
        stubEventLoop(peer);
        final DiameterResponderSession session = new DiameterResponderSession(CONFIG_WITH_VENDOR_SPECIFIC_APP);
        session.onConnected(peer);
        final long differentVendorId = 99999L;
        final CapabilitiesExchangeRequest.In cer = buildIncomingCerWithVendorSpecificAppId(differentVendorId, SGD_APP_ID);

        // WHEN
        session.onMessage(peer, cer);

        // THEN: vendor ID is irrelevant per RFC 6733 §5.3 — still a match
        assertThat(session.getPeerState()).isEqualTo(PeerState.R_OPEN);
    }

    @Test
    void it_sends_no_common_application_when_only_vendor_specific_app_and_no_remote_match() throws Exception {
        // GIVEN: local has only a vendor-specific app, remote sends no matching app
        final DiameterPeer peer = mock(DiameterPeer.class);
        stubSend(peer);
        when(peer.close()).thenReturn(mock(ChannelFuture.class));
        final DiameterResponderSession session = new DiameterResponderSession(CONFIG_WITH_VENDOR_SPECIFIC_APP);
        session.onConnected(peer);
        final CapabilitiesExchangeRequest.In cer = buildIncomingCer(99L);

        // WHEN
        session.onMessage(peer, cer);

        // THEN
        final ArgumentCaptor<CapabilitiesExchangeAnswer.Out> captor =
                ArgumentCaptor.forClass(CapabilitiesExchangeAnswer.Out.class);
        verify(peer).send(captor.capture());
        assertThat(captor.getValue().getResultCode()).isEqualTo(DiameterConstants.RES_DIAMETER_NO_COMMON_APPLICATION);
    }

    @Test
    void it_emits_vendor_specific_app_id_avp_in_cer() throws Exception {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        stubSend(peer);
        stubEventLoop(peer);
        final DiameterResponderSession session = new DiameterResponderSession(CONFIG_WITH_VENDOR_SPECIFIC_APP);
        session.onConnected(peer);
        final CapabilitiesExchangeRequest.In cer = buildIncomingCerWithVendorSpecificAppId(VENDOR_3GPP, SGD_APP_ID);
        session.onMessage(peer, cer);

        // THEN: the CEA sent back contains one Vendor-Specific-Application-Id grouped AVP
        final ArgumentCaptor<CapabilitiesExchangeAnswer.Out> captor =
                ArgumentCaptor.forClass(CapabilitiesExchangeAnswer.Out.class);
        verify(peer).send(captor.capture());
        final List<GroupedAVP> vsAppIds = captor.getValue().getVendorSpecificApplicationIds();
        assertThat(vsAppIds).hasSize(1);
        final GroupedAVP grouped = vsAppIds.get(0);
        assertThat(grouped.findAVP(DiameterConstants.AVP_VENDOR_ID).getDataAsUnsignedInt()).isEqualTo(VENDOR_3GPP);
        assertThat(grouped.findAVP(DiameterConstants.AVP_AUTH_APPLICATION_ID).getDataAsUnsignedInt()).isEqualTo(SGD_APP_ID);
    }

    // -------------------------------------------------------------------------
    // Handler binding
    // -------------------------------------------------------------------------

    @Test
    void it_throws_when_registering_a_second_handler_for_the_same_command_code() {
        // GIVEN
        final DiameterResponderSession session = new DiameterResponderSession(CONFIG);
        session.setHandler(ReAuthRequest.In.class, req -> new CompletableFuture<ReAuthAnswer.Out>());

        // WHEN / THEN
        assertThatThrownBy(() -> session.setHandler(ReAuthRequest.In.class, req -> new CompletableFuture<ReAuthAnswer.Out>()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void it_dispatches_inbound_request_to_registered_handler() throws Exception {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        stubSend(peer);
        final DiameterResponderSession session = openedSession(peer);
        final ReAuthRequest.In rar = (ReAuthRequest.In)
                DiameterMessageFactory.createForParsing(DiameterConstants.CMD_RE_AUTH, 0, true, new HopByHopId(10), new EndToEndId(20), false);
        final ReAuthAnswer.Out answer = DiameterMessageFactory.createAnswer(rar, DiameterConstants.RES_DIAMETER_SUCCESS);
        session.setHandler(ReAuthRequest.In.class, req -> CompletableFuture.completedFuture(answer));

        // WHEN
        session.onMessage(peer, rar);

        // THEN
        verify(peer).send(answer);
    }

    @Test
    void it_sends_COMMAND_UNSUPPORTED_for_unhandled_request() throws Exception {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        stubSend(peer);
        final DiameterResponderSession session = openedSession(peer);
        final ReAuthRequest.In rar = (ReAuthRequest.In)
                DiameterMessageFactory.createForParsing(DiameterConstants.CMD_RE_AUTH, 0, true, new HopByHopId(10), new EndToEndId(20), false);

        // WHEN
        session.onMessage(peer, rar);

        // THEN
        final ArgumentCaptor<ReAuthAnswer.Out> captor = ArgumentCaptor.forClass(ReAuthAnswer.Out.class);
        verify(peer).send(captor.capture());
        assertThat(captor.getValue().getResultCode())
                .isEqualTo(DiameterConstants.RES_DIAMETER_COMMAND_UNSUPPORTED);
    }

    @Test
    void it_sends_UNABLE_TO_COMPLY_when_handler_future_fails() throws Exception {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        stubSend(peer);
        final DiameterResponderSession session = openedSession(peer);
        final CompletableFuture<ReAuthAnswer.Out> failing = new CompletableFuture<>();
        failing.completeExceptionally(new RuntimeException("handler error"));
        session.setHandler(ReAuthRequest.In.class, req -> failing);
        final ReAuthRequest.In rar = (ReAuthRequest.In)
                DiameterMessageFactory.createForParsing(DiameterConstants.CMD_RE_AUTH, 0, true, new HopByHopId(10), new EndToEndId(20), false);

        // WHEN
        session.onMessage(peer, rar);

        // THEN
        final ArgumentCaptor<ReAuthAnswer.Out> captor = ArgumentCaptor.forClass(ReAuthAnswer.Out.class);
        verify(peer).send(captor.capture());
        assertThat(captor.getValue().getResultCode())
                .isEqualTo(DiameterConstants.RES_DIAMETER_UNABLE_TO_COMPLY);
    }

    // -------------------------------------------------------------------------
    // DWR/DWA watchdog
    // -------------------------------------------------------------------------

    @Test
    void it_transitions_watchdog_to_OKAY_on_entering_R_OPEN() throws Exception {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        final DiameterResponderSession session = openedSession(peer);

        // WHEN / THEN - watchdog is OKAY after CER exchange
        assertThat(session.getWatchdogState()).isEqualTo(WatchdogState.OKAY);
    }

    @Test
    void it_sends_DWA_when_DWR_received_in_R_OPEN() throws Exception {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        stubSend(peer);
        final DiameterResponderSession session = openedSession(peer);
        final DeviceWatchdogRequest.In dwr = (DeviceWatchdogRequest.In)
                DiameterMessageFactory.createForParsing(DiameterConstants.CMD_DEVICE_WATCHDOG, 0, true, new HopByHopId(77), new EndToEndId(88), false);

        // WHEN
        session.onMessage(peer, dwr);

        // THEN
        final ArgumentCaptor<DeviceWatchdogAnswer.Out> captor =
                ArgumentCaptor.forClass(DeviceWatchdogAnswer.Out.class);
        verify(peer).send(captor.capture());
        assertThat(captor.getValue().hopByHopId()).isEqualTo(new HopByHopId(77));
    }

    // -------------------------------------------------------------------------
    // DPR/DPA - graceful shutdown
    // -------------------------------------------------------------------------

    @Test
    void it_transitions_to_CLOSING_when_stop_is_called() throws Exception {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        stubSend(peer);
        final DiameterResponderSession session = openedSession(peer);

        // WHEN
        session.stop();

        // THEN
        assertThat(session.getPeerState()).isEqualTo(PeerState.CLOSING);
    }

    @Test
    void it_sends_DPR_when_stop_is_called() throws Exception {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        stubSend(peer);
        final DiameterResponderSession session = openedSession(peer);

        // WHEN
        session.stop();

        // THEN
        verify(peer).send(any(DisconnectPeerRequest.Out.class), any(HopByHopId.class), any(EndToEndId.class));
    }

    @Test
    void it_rejects_send_in_CLOSING_state() throws Exception {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        stubSend(peer);
        final DiameterResponderSession session = openedSession(peer);
        session.stop();

        // WHEN
        final CapabilitiesExchangeRequest.Out req =
                new CapabilitiesExchangeRequest.Out();
        final CompletableFuture<CapabilitiesExchangeAnswer.In> future = session.send(req);

        // THEN
        assertThat(future).isCompletedExceptionally();
    }

    @Test
    void it_transitions_to_CLOSED_when_DPA_arrives_in_CLOSING() throws Exception {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        stubSend(peer);
        when(peer.close()).thenReturn(mock(ChannelFuture.class));
        final DiameterResponderSession session = openedSession(peer);
        session.stop();
        final DisconnectPeerAnswer.In dpa = captureAndBuildDpa(peer);

        // WHEN
        session.onMessage(peer, dpa);

        // THEN
        assertThat(session.getPeerState()).isEqualTo(PeerState.CLOSED);
    }

    @Test
    void it_transitions_to_CLOSING_when_DPR_received_in_R_OPEN() throws Exception {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        stubSend(peer);
        when(peer.close()).thenReturn(mock(ChannelFuture.class));
        final DiameterResponderSession session = openedSession(peer);
        final DisconnectPeerRequest.In dpr = (DisconnectPeerRequest.In)
                DiameterMessageFactory.createForParsing(DiameterConstants.CMD_DISCONNECT_PEER, 0, true, new HopByHopId(10), new EndToEndId(20), false);

        // WHEN
        session.onMessage(peer, dpr);

        // THEN
        assertThat(session.getPeerState()).isEqualTo(PeerState.CLOSING);
    }

    @Test
    void it_sends_DPA_when_DPR_received_in_R_OPEN() throws Exception {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        stubSend(peer);
        when(peer.close()).thenReturn(mock(ChannelFuture.class));
        final DiameterResponderSession session = openedSession(peer);
        final DisconnectPeerRequest.In dpr = (DisconnectPeerRequest.In)
                DiameterMessageFactory.createForParsing(DiameterConstants.CMD_DISCONNECT_PEER, 0, true, new HopByHopId(10), new EndToEndId(20), false);

        // WHEN
        session.onMessage(peer, dpr);

        // THEN
        final ArgumentCaptor<DisconnectPeerAnswer.Out> captor =
                ArgumentCaptor.forClass(DisconnectPeerAnswer.Out.class);
        verify(peer).send(captor.capture());
        assertThat(captor.getValue().hopByHopId()).isEqualTo(new HopByHopId(10));
    }

    @Test
    void it_transitions_to_CLOSED_on_disconnect_after_inbound_DPR() throws Exception {
        // GIVEN: peer sends DPR, we respond with DPA and close
        final DiameterPeer peer = mock(DiameterPeer.class);
        stubSend(peer);
        when(peer.close()).thenReturn(mock(ChannelFuture.class));
        final DiameterResponderSession session = openedSession(peer);
        final DisconnectPeerRequest.In dpr = (DisconnectPeerRequest.In)
                DiameterMessageFactory.createForParsing(DiameterConstants.CMD_DISCONNECT_PEER, 0, true, new HopByHopId(10), new EndToEndId(20), false);
        session.onMessage(peer, dpr);

        // WHEN - channel closes after DPA was sent
        session.onDisconnected(peer);

        // THEN
        assertThat(session.getPeerState()).isEqualTo(PeerState.CLOSED);
    }

    @Test
    void it_closes_peer_when_DPR_received_in_R_OPEN() throws Exception {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        stubSend(peer);
        when(peer.close()).thenReturn(mock(ChannelFuture.class));
        final DiameterResponderSession session = openedSession(peer);
        final DisconnectPeerRequest.In dpr = (DisconnectPeerRequest.In)
                DiameterMessageFactory.createForParsing(DiameterConstants.CMD_DISCONNECT_PEER, 0, true, new HopByHopId(10), new EndToEndId(20), false);

        // WHEN
        session.onMessage(peer, dpr);

        // THEN
        verify(peer).close();
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static DiameterResponderSession openedSession(final DiameterPeer peer) throws Exception {
        stubSend(peer);
        stubEventLoop(peer);
        final DiameterResponderSession session = new DiameterResponderSession(CONFIG_WITH_AUTH_APP);
        session.onConnected(peer);
        final CapabilitiesExchangeRequest.In cer = buildIncomingCer(5L);
        session.onMessage(peer, cer);
        Mockito.clearInvocations(peer);
        return session;
    }

    private static CapabilitiesExchangeRequest.In buildIncomingCer(final long authAppId) throws Exception {
        final CapabilitiesExchangeRequest.Out cerOut =
                new CapabilitiesExchangeRequest.Out();
        cerOut.addAuthApplicationId(authAppId);
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        cerOut.writeTo(new DataOutputStream(baos), new HopByHopId(1), new EndToEndId(2));
        return (CapabilitiesExchangeRequest.In) Command.parseMessage(ByteBuffer.wrap(baos.toByteArray()));
    }

    private static CapabilitiesExchangeRequest.In buildIncomingCerWithVendorSpecificAppId(
            final long vendorId, final long authAppId) throws Exception {
        final CapabilitiesExchangeRequest.Out cerOut = new CapabilitiesExchangeRequest.Out();
        cerOut.addVendorSpecificApplicationId(
                (GroupedAVP) AVP.create(DiameterConstants.AVP_VENDOR_SPECIFIC_APPLICATION_ID, List.of(
                        AVP.create(DiameterConstants.AVP_VENDOR_ID, vendorId),
                        AVP.create(DiameterConstants.AVP_AUTH_APPLICATION_ID, authAppId)
                )));
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        cerOut.writeTo(new DataOutputStream(baos), new HopByHopId(1), new EndToEndId(2));
        return (CapabilitiesExchangeRequest.In) Command.parseMessage(ByteBuffer.wrap(baos.toByteArray()));
    }

    /**
     * After {@code session.stop()} sends a DPR, capture the hop-by-hop/end-to-end
     * identifiers and build a matching incoming DPA.
     */
    @SuppressWarnings("unchecked")
    private static DisconnectPeerAnswer.In captureAndBuildDpa(final DiameterPeer peer) throws Exception {
        final ArgumentCaptor<HopByHopId> hbh = ArgumentCaptor.forClass(HopByHopId.class);
        final ArgumentCaptor<EndToEndId> e2e = ArgumentCaptor.forClass(EndToEndId.class);
        verify(peer).send(any(DisconnectPeerRequest.Out.class), hbh.capture(), e2e.capture());
        Mockito.clearInvocations(peer);

        final DisconnectPeerRequest.In fakeDpr = (DisconnectPeerRequest.In)
                DiameterMessageFactory.createForParsing(DiameterConstants.CMD_DISCONNECT_PEER, 0, true, hbh.getValue(), e2e.getValue(), false);
        final DisconnectPeerAnswer.Out dpaOut =
                DiameterMessageFactory.createAnswer(fakeDpr, DiameterConstants.RES_DIAMETER_SUCCESS);
        return toIncomingAnswer(dpaOut);
    }

    @SuppressWarnings("unchecked")
    private static <A extends IncomingAnswer> A toIncomingAnswer(final OutgoingAnswer answer) throws Exception {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        answer.writeTo(new DataOutputStream(baos));
        return (A) Command.parseMessage(ByteBuffer.wrap(baos.toByteArray()));
    }

    @SuppressWarnings("unchecked")
    private static void stubSend(final DiameterPeer peer) {
        when(peer.send(any(OutgoingAnswer.class))).thenReturn(mock(ChannelFuture.class));
        when(peer.send(any(OutgoingRequest.class), any(HopByHopId.class), any(EndToEndId.class)))
                .thenReturn(mock(ChannelFuture.class));
    }

    @SuppressWarnings("unchecked")
    private static void stubEventLoop(final DiameterPeer peer) {
        final EventLoop eventLoop = mock(EventLoop.class);
        when(peer.eventLoop()).thenReturn(eventLoop);
        when(eventLoop.schedule(any(Runnable.class), anyLong(), any(TimeUnit.class)))
                .thenReturn(mock(ScheduledFuture.class));
    }
}
