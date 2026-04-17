package com.sipgate.sparta.diameter.base.session;

import com.sipgate.sparta.diameter.base.DiameterException;
import com.sipgate.sparta.diameter.base.core.Command;
import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.DiameterMessageFactory;
import com.sipgate.sparta.diameter.base.core.DiameterResultCodeException;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.ErrorAnswer;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.IncomingAnswer;
import com.sipgate.sparta.diameter.base.core.IncomingRequest;
import com.sipgate.sparta.diameter.base.core.OutgoingAnswer;
import com.sipgate.sparta.diameter.base.core.OutgoingRequest;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.AVPParseException;
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
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.ScheduledFuture;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.UncheckedIOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
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
                    Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList()));

    // -------------------------------------------------------------------------
    // Initial state
    // -------------------------------------------------------------------------

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
        final DiameterPeer peer = mock(DiameterPeer.class);
        stubEventLoop(peer);
        final DiameterInitiatorSession session = new DiameterInitiatorSession(CONFIG, () -> {});

        // WHEN
        session.onDisconnected(peer);

        // THEN
        assertThat(session.getPeerState()).isEqualTo(PeerState.CLOSED);
        assertThat(session.getWatchdogState()).isEqualTo(WatchdogState.DOWN);
    }

    // -------------------------------------------------------------------------
    // CER/CEA
    // -------------------------------------------------------------------------

    @Test
    void it_sends_a_CER_on_connected() {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        stubSend(peer);
        stubEventLoop(peer);
        final DiameterInitiatorSession session = new DiameterInitiatorSession(CONFIG, () -> {});

        // WHEN
        session.onConnected(peer);

        // THEN
        verify(peer).send(any(CapabilitiesExchangeRequest.Out.class), any(HopByHopId.class), any(EndToEndId.class));
    }

    @Test
    void it_sets_origin_host_in_the_CER() {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        stubSend(peer);
        stubEventLoop(peer);
        final DiameterInitiatorSession session = new DiameterInitiatorSession(CONFIG, () -> {});

        // WHEN
        session.onConnected(peer);

        // THEN
        final ArgumentCaptor<CapabilitiesExchangeRequest.Out> captor =
                ArgumentCaptor.forClass(CapabilitiesExchangeRequest.Out.class);
        verify(peer).send(captor.capture(), any(HopByHopId.class), any(EndToEndId.class));
        assertThat(captor.getValue().getOriginHost()).isEqualTo("hss.example.com");
    }

    @Test
    void it_transitions_to_WAIT_I_CEA_on_connected() {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        stubSend(peer);
        stubEventLoop(peer);
        final DiameterInitiatorSession session = new DiameterInitiatorSession(CONFIG, () -> {});

        // WHEN
        session.onConnected(peer);

        // THEN
        assertThat(session.getPeerState()).isEqualTo(PeerState.WAIT_I_CEA);
    }

    @Test
    void it_transitions_to_CLOSED_when_CER_write_fails() {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        final ChannelFuture writeFail = failedWriteFuture();
        when(peer.send(any(OutgoingAnswer.class))).thenReturn(mock(ChannelFuture.class));
        when(peer.send(any(OutgoingRequest.class), any(HopByHopId.class), any(EndToEndId.class)))
                .thenReturn(writeFail);
        when(peer.close()).thenReturn(mock(ChannelFuture.class));
        stubEventLoop(peer);
        final DiameterInitiatorSession session = new DiameterInitiatorSession(CONFIG, () -> {});

        // WHEN
        session.onConnected(peer);

        // THEN
        assertThat(session.getPeerState()).isEqualTo(PeerState.CLOSED);
    }

    @Test
    void it_transitions_to_I_OPEN_on_successful_CEA() throws Exception {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        stubSend(peer);
        stubEventLoop(peer);
        final DiameterInitiatorSession session = new DiameterInitiatorSession(CONFIG, () -> {});
        session.onConnected(peer);
        final CapabilitiesExchangeAnswer.In cea = captureAndBuildCea(peer, DiameterConstants.RES_DIAMETER_SUCCESS);

        // WHEN
        session.onMessage(cea);

        // THEN
        assertThat(session.getPeerState()).isEqualTo(PeerState.I_OPEN);
    }

    @Test
    void it_ignores_CEA_with_wrong_hop_by_hop_id() throws Exception {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        stubSend(peer);
        stubEventLoop(peer);
        final DiameterInitiatorSession session = new DiameterInitiatorSession(CONFIG, () -> {});
        session.onConnected(peer);
        final HopByHopId cerHbH = capturedCerHopByHop(peer);
        final CapabilitiesExchangeAnswer.In wrongCea = (CapabilitiesExchangeAnswer.In)
                DiameterMessageFactory.createForParsing(DiameterConstants.CMD_CAPABILITIES_EXCHANGE,
                        0, false, true, false, new HopByHopId(cerHbH.value() + 1), new EndToEndId(2), false);

        // WHEN
        session.onMessage(wrongCea);

        // THEN: still waiting — rogue CEA had no effect
        assertThat(session.getPeerState()).isEqualTo(PeerState.WAIT_I_CEA);
    }

    @Test
    void it_transitions_to_CLOSED_on_failed_CEA() throws Exception {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        stubSend(peer);
        when(peer.close()).thenReturn(mock(ChannelFuture.class));
        stubEventLoop(peer);
        final DiameterInitiatorSession session = new DiameterInitiatorSession(CONFIG, () -> {});
        session.onConnected(peer);
        final CapabilitiesExchangeAnswer.In cea =
                captureAndBuildCea(peer, DiameterConstants.RES_DIAMETER_NO_COMMON_APPLICATION);

        // WHEN
        session.onMessage(cea);

        // THEN
        assertThat(session.getPeerState()).isEqualTo(PeerState.CLOSED);
    }

    @Test
    void it_closes_peer_on_failed_CEA() throws Exception {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        stubSend(peer);
        when(peer.close()).thenReturn(mock(ChannelFuture.class));
        stubEventLoop(peer);
        final DiameterInitiatorSession session = new DiameterInitiatorSession(CONFIG, () -> {});
        session.onConnected(peer);
        final CapabilitiesExchangeAnswer.In cea =
                captureAndBuildCea(peer, DiameterConstants.RES_DIAMETER_NO_COMMON_APPLICATION);

        // WHEN
        session.onMessage(cea);

        // THEN
        verify(peer).close();
        assertThat(session.getPeerState()).isEqualTo(PeerState.CLOSED);
    }

    // -------------------------------------------------------------------------
    // send() — pending-request map
    // -------------------------------------------------------------------------

    @Test
    void it_returns_failed_future_when_not_in_OPEN_state() {
        // GIVEN
        final DiameterInitiatorSession session = new DiameterInitiatorSession(CONFIG, () -> {});
        final CapabilitiesExchangeRequest.Out request =
                new CapabilitiesExchangeRequest.Out();

        // WHEN
        final CompletableFuture<CapabilitiesExchangeAnswer.In> future = session.send(request);

        // THEN
        assertThat(future).isCompletedExceptionally();
    }

    @Test
    void it_sends_request_to_peer_when_in_OPEN_state() throws Exception {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        stubSend(peer);
        final DiameterInitiatorSession session = openedSession(peer);
        final CapabilitiesExchangeRequest.Out request =
                new CapabilitiesExchangeRequest.Out();

        // WHEN
        session.send(request);

        // THEN
        verify(peer).send(eq(request), any(HopByHopId.class), any(EndToEndId.class));
    }

    @Test
    void it_completes_future_when_matching_answer_arrives() throws Exception {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        stubSend(peer);
        final DiameterInitiatorSession session = openedSession(peer);
        final CapabilitiesExchangeRequest.Out request =
                new CapabilitiesExchangeRequest.Out();
        final CompletableFuture<CapabilitiesExchangeAnswer.In> future = session.send(request);
        final CapabilitiesExchangeAnswer.In answer = captureAndBuildCeaForRequest(peer);

        // WHEN
        session.onMessage(answer);

        // THEN
        assertThat(future).isCompletedWithValue(answer);
    }

    @Test
    void it_ignores_answer_with_unknown_hop_by_hop_id() throws Exception {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        stubSend(peer);
        final DiameterInitiatorSession session = openedSession(peer);
        final CapabilitiesExchangeRequest.Out request =
                new CapabilitiesExchangeRequest.Out();
        final CompletableFuture<CapabilitiesExchangeAnswer.In> future = session.send(request);
        final CapabilitiesExchangeAnswer.In wrongAnswer = (CapabilitiesExchangeAnswer.In)
                DiameterMessageFactory.createForParsing(DiameterConstants.CMD_CAPABILITIES_EXCHANGE,
                        0, false, true, false, new HopByHopId(99), new EndToEndId(99), false);

        // WHEN
        session.onMessage(wrongAnswer);

        // THEN
        assertThat(future).isNotDone();
    }

    @Test
    void it_completes_future_exceptionally_with_DiameterErrorAnswerException_on_error_answer() throws Exception {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        stubSend(peer);
        final DiameterInitiatorSession session = openedSession(peer);
        final CapabilitiesExchangeRequest.Out request = new CapabilitiesExchangeRequest.Out();
        final CompletableFuture<CapabilitiesExchangeAnswer.In> future = session.send(request);
        final ArgumentCaptor<HopByHopId> hbhCaptor = ArgumentCaptor.forClass(HopByHopId.class);
        final ArgumentCaptor<EndToEndId> e2eCaptor = ArgumentCaptor.forClass(EndToEndId.class);
        verify(peer).send(any(CapabilitiesExchangeRequest.Out.class), hbhCaptor.capture(), e2eCaptor.capture());
        final var cer = (IncomingRequest<?, ?>) DiameterMessageFactory.createForParsing(
                DiameterConstants.CMD_CAPABILITIES_EXCHANGE, 0, true, true, false,
                hbhCaptor.getValue(), e2eCaptor.getValue(), false);
        final var errorOut = DiameterMessageFactory.createErrorAnswer(cer, DiameterConstants.RES_DIAMETER_UNABLE_TO_COMPLY);
        final ErrorAnswer.In errorIn = toIncomingAnswer(errorOut);

        // WHEN
        session.onMessage(errorIn);

        // THEN
        final var cause = future.handle((a, ex) -> ex).join();
        assertThat(cause).isInstanceOf(DiameterErrorAnswerException.class);
        assertThat(((DiameterErrorAnswerException) cause).getAnswer()).isSameAs(errorIn);
    }

    @Test
    void it_fails_future_when_write_fails() throws Exception {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        final ChannelFuture writeFail = failedWriteFuture();
        // first call (CER) succeeds; second call (request) fails at write
        when(peer.send(any(OutgoingAnswer.class))).thenReturn(mock(ChannelFuture.class));
        when(peer.send(any(OutgoingRequest.class), any(HopByHopId.class), any(EndToEndId.class)))
                .thenReturn(mock(ChannelFuture.class), writeFail);
        stubEventLoop(peer);
        final DiameterInitiatorSession session = openedSession(peer);
        final CapabilitiesExchangeRequest.Out request =
                new CapabilitiesExchangeRequest.Out();

        // WHEN
        final CompletableFuture<CapabilitiesExchangeAnswer.In> future = session.send(request);

        // THEN
        assertThat(future).isCompletedExceptionally();
    }

    @Test
    void it_fails_pending_futures_on_disconnect() throws Exception {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        stubSend(peer);
        final DiameterInitiatorSession session = openedSession(peer);
        final CapabilitiesExchangeRequest.Out request =
                new CapabilitiesExchangeRequest.Out();
        final CompletableFuture<CapabilitiesExchangeAnswer.In> future = session.send(request);

        // WHEN
        session.onDisconnected(peer);

        // THEN
        assertThat(future).isCompletedExceptionally();
    }

    // -------------------------------------------------------------------------
    // Answer timeout
    // -------------------------------------------------------------------------

    @Test
    @SuppressWarnings("unchecked")
    void it_fails_future_with_TimeoutException_after_request_timeout() throws Exception {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        stubSend(peer);

        final EventLoop eventLoop = mock(EventLoop.class);
        when(peer.eventLoop()).thenReturn(eventLoop);
        final AtomicReference<Runnable> capturedTask = new AtomicReference<>();
        when(eventLoop.schedule(any(Runnable.class), anyLong(), any(TimeUnit.class)))
                .thenAnswer(inv -> {
                    capturedTask.set(inv.getArgument(0));
                    return mock(ScheduledFuture.class);
                });

        final DiameterInitiatorSession session = new DiameterInitiatorSession(CONFIG, () -> {});
        session.onConnected(peer);
        final CapabilitiesExchangeAnswer.In cea = captureAndBuildCea(peer, DiameterConstants.RES_DIAMETER_SUCCESS);
        session.onMessage(cea);
        Mockito.clearInvocations(peer);

        final CapabilitiesExchangeRequest.Out request =
                new CapabilitiesExchangeRequest.Out();
        final CompletableFuture<CapabilitiesExchangeAnswer.In> future = session.send(request);

        // WHEN — simulate the timeout task firing
        capturedTask.get().run();

        // THEN
        assertThatThrownBy(future::join)
                .isInstanceOf(CompletionException.class)
                .hasCauseInstanceOf(TimeoutException.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    void it_cancels_timeout_when_answer_arrives() throws Exception {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        stubSend(peer);

        final EventLoop eventLoop = mock(EventLoop.class);
        when(peer.eventLoop()).thenReturn(eventLoop);
        final ScheduledFuture<?> cerTimeout = mock(ScheduledFuture.class);
        final ScheduledFuture<?> twTimer = mock(ScheduledFuture.class);
        final ScheduledFuture<?> requestTimeout = mock(ScheduledFuture.class);
        when(eventLoop.schedule(any(Runnable.class), anyLong(), any(TimeUnit.class)))
                .thenReturn((ScheduledFuture) cerTimeout, (ScheduledFuture) twTimer, (ScheduledFuture) requestTimeout);

        final DiameterInitiatorSession session = new DiameterInitiatorSession(CONFIG, () -> {});
        session.onConnected(peer);
        final CapabilitiesExchangeAnswer.In cea = captureAndBuildCea(peer, DiameterConstants.RES_DIAMETER_SUCCESS);
        session.onMessage(cea);
        Mockito.clearInvocations(peer);

        final CapabilitiesExchangeRequest.Out request =
                new CapabilitiesExchangeRequest.Out();
        session.send(request);
        final CapabilitiesExchangeAnswer.In answer = captureAndBuildCeaForRequest(peer);

        // WHEN
        session.onMessage(answer);

        // THEN
        verify(requestTimeout).cancel(false);
    }

    @Test
    @SuppressWarnings("unchecked")
    void it_cancels_all_timeouts_on_disconnect() throws Exception {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        stubSend(peer);

        final EventLoop eventLoop = mock(EventLoop.class);
        when(peer.eventLoop()).thenReturn(eventLoop);
        final ScheduledFuture<?> cerTimeout = mock(ScheduledFuture.class);
        final ScheduledFuture<?> twTimer = mock(ScheduledFuture.class);
        final ScheduledFuture<?> requestTimeout = mock(ScheduledFuture.class);
        when(eventLoop.schedule(any(Runnable.class), anyLong(), any(TimeUnit.class)))
                .thenReturn((ScheduledFuture) cerTimeout, (ScheduledFuture) twTimer, (ScheduledFuture) requestTimeout);

        final DiameterInitiatorSession session = new DiameterInitiatorSession(CONFIG, () -> {});
        session.onConnected(peer);
        final CapabilitiesExchangeAnswer.In cea = captureAndBuildCea(peer, DiameterConstants.RES_DIAMETER_SUCCESS);
        session.onMessage(cea);
        Mockito.clearInvocations(peer);

        final CapabilitiesExchangeRequest.Out request =
                new CapabilitiesExchangeRequest.Out();
        session.send(request);

        // WHEN
        session.onDisconnected(peer);

        // THEN
        verify(requestTimeout).cancel(false);
    }

    // -------------------------------------------------------------------------
    // Handler binding
    // -------------------------------------------------------------------------

    @Test
    void it_throws_when_registering_a_second_handler_for_the_same_command_code() {
        // GIVEN
        final DiameterInitiatorSession session = new DiameterInitiatorSession(CONFIG, () -> {});
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
        final DiameterInitiatorSession session = openedSession(peer);
        final ReAuthRequest.In rar = (ReAuthRequest.In)
                DiameterMessageFactory.createForParsing(DiameterConstants.CMD_RE_AUTH, 0, true, true, false, new HopByHopId(10), new EndToEndId(20), false);
        final ReAuthAnswer.Out answer = DiameterMessageFactory.createAnswer(rar, DiameterConstants.RES_DIAMETER_SUCCESS);
        session.setHandler(ReAuthRequest.In.class, req -> CompletableFuture.completedFuture(answer));

        // WHEN
        session.onMessage(rar);

        // THEN
        verify(peer).send(answer);
    }

    @Test
    void it_sends_COMMAND_UNSUPPORTED_for_unhandled_request() throws Exception {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        stubSend(peer);
        final DiameterInitiatorSession session = openedSession(peer);
        final ReAuthRequest.In rar = (ReAuthRequest.In)
                DiameterMessageFactory.createForParsing(DiameterConstants.CMD_RE_AUTH, 0, true, true, false, new HopByHopId(10), new EndToEndId(20), false);

        // WHEN
        session.onMessage(rar);

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
        final DiameterInitiatorSession session = openedSession(peer);
        final CompletableFuture<ReAuthAnswer.Out> failing = new CompletableFuture<>();
        failing.completeExceptionally(new RuntimeException("handler error"));
        session.setHandler(ReAuthRequest.In.class, req -> failing);
        final ReAuthRequest.In rar = (ReAuthRequest.In)
                DiameterMessageFactory.createForParsing(DiameterConstants.CMD_RE_AUTH, 0, true, true, false, new HopByHopId(10), new EndToEndId(20), false);

        // WHEN
        session.onMessage(rar);

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
    void it_transitions_watchdog_to_OKAY_on_entering_I_OPEN() throws Exception {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        stubSend(peer);
        final List<Runnable> tasks = new ArrayList<>();
        stubEventLoop(peer, tasks);
        final DiameterInitiatorSession session = new DiameterInitiatorSession(CONFIG, () -> {});
        session.onConnected(peer);
        final CapabilitiesExchangeAnswer.In cea = captureAndBuildCea(peer, DiameterConstants.RES_DIAMETER_SUCCESS);

        // WHEN
        session.onMessage(cea);

        // THEN
        assertThat(session.getWatchdogState()).isEqualTo(WatchdogState.OKAY);
    }

    @Test
    void it_sends_DWR_when_Tw_timer_fires() throws Exception {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        stubSend(peer);
        final List<Runnable> tasks = new ArrayList<>();
        stubEventLoop(peer, tasks);
        openedSession(peer, tasks);

        // WHEN — fire Tw timer (index 1, after the CER timeout at index 0)
        tasks.get(1).run();

        // THEN
        final ArgumentCaptor<DeviceWatchdogRequest.Out> captor =
                ArgumentCaptor.forClass(DeviceWatchdogRequest.Out.class);
        verify(peer).send(captor.capture(), any(HopByHopId.class), any(EndToEndId.class));
        assertThat(captor.getValue()).isInstanceOf(DeviceWatchdogRequest.Out.class);
    }

    @Test
    void it_transitions_to_SUSPECT_when_Tw_fires_with_unanswered_DWR() throws Exception {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        stubSend(peer);
        final List<Runnable> tasks = new ArrayList<>();
        stubEventLoop(peer, tasks);
        final DiameterInitiatorSession session = openedSession(peer, tasks);

        // Fire first Tw — sends DWR (pending)
        tasks.get(1).run();

        // WHEN — fire rescheduled Tw while DWR is still unanswered
        tasks.get(2).run();

        // THEN
        assertThat(session.getWatchdogState()).isEqualTo(WatchdogState.SUSPECT);
    }

    @Test
    void it_transitions_to_DOWN_and_closes_peer_when_Tw_fires_in_SUSPECT() throws Exception {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        stubSend(peer);
        when(peer.close()).thenReturn(mock(ChannelFuture.class));
        final List<Runnable> tasks = new ArrayList<>();
        stubEventLoop(peer, tasks);
        final DiameterInitiatorSession session = openedSession(peer, tasks);

        // Fire first Tw — sends DWR (pending)
        tasks.get(1).run();
        // Fire second Tw — transitions to SUSPECT, reschedules
        tasks.get(2).run();

        // WHEN — fire third Tw while still in SUSPECT
        tasks.get(3).run();

        // THEN
        assertThat(session.getWatchdogState()).isEqualTo(WatchdogState.DOWN);
        assertThat(session.getPeerState()).isEqualTo(PeerState.CLOSED);
        verify(peer).close();
    }

    @Test
    void it_sends_DWA_when_DWR_received_in_I_OPEN() throws Exception {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        stubSend(peer);
        final List<Runnable> tasks = new ArrayList<>();
        stubEventLoop(peer, tasks);
        final DiameterInitiatorSession session = openedSession(peer, tasks);
        final DeviceWatchdogRequest.In dwr = (DeviceWatchdogRequest.In)
                DiameterMessageFactory.createForParsing(DiameterConstants.CMD_DEVICE_WATCHDOG, 0, true, true, false, new HopByHopId(77), new EndToEndId(88), false);

        // WHEN
        session.onMessage(dwr);

        // THEN — a DWA was sent back
        final ArgumentCaptor<DeviceWatchdogAnswer.Out> captor =
                ArgumentCaptor.forClass(DeviceWatchdogAnswer.Out.class);
        verify(peer).send(captor.capture());
        assertThat(captor.getValue().hopByHopId()).isEqualTo(new HopByHopId(77));
    }

    @Test
    void it_rejects_handler_registration_for_DWR_once_watchdog_is_active() throws Exception {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        stubSend(peer);
        final List<Runnable> tasks = new ArrayList<>();
        stubEventLoop(peer, tasks);
        final DiameterInitiatorSession session = openedSession(peer, tasks);

        // WHEN / THEN — session occupies the DWR slot internally on entering I_OPEN
        assertThatThrownBy(() -> session.setHandler(DeviceWatchdogRequest.In.class, req -> new CompletableFuture<DeviceWatchdogAnswer.Out>()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void it_transitions_from_SUSPECT_to_OKAY_when_DWA_arrives() throws Exception {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        stubSend(peer);
        final List<Runnable> tasks = new ArrayList<>();
        stubEventLoop(peer, tasks);
        final DiameterInitiatorSession session = openedSession(peer, tasks);

        // Fire first Tw — sends DWR, capture its hop-by-hop
        tasks.get(1).run();
        final ArgumentCaptor<HopByHopId> dwrHbH = ArgumentCaptor.forClass(HopByHopId.class);
        final ArgumentCaptor<EndToEndId> dwrE2E = ArgumentCaptor.forClass(EndToEndId.class);
        verify(peer).send(any(DeviceWatchdogRequest.Out.class), dwrHbH.capture(), dwrE2E.capture());
        Mockito.clearInvocations(peer);

        // Fire second Tw — transitions to SUSPECT
        tasks.get(2).run();
        assertThat(session.getWatchdogState()).isEqualTo(WatchdogState.SUSPECT);

        // WHEN — DWA arrives with matching hop-by-hop
        final DeviceWatchdogAnswer.In dwa = buildMatchingDwa(dwrHbH.getValue(), dwrE2E.getValue());
        session.onMessage(dwa);

        // THEN
        assertThat(session.getWatchdogState()).isEqualTo(WatchdogState.OKAY);
    }

    @Test
    void it_transitions_from_SUSPECT_to_OKAY_when_other_message_proves_link_alive() throws Exception {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        stubSend(peer);
        final List<Runnable> tasks = new ArrayList<>();
        stubEventLoop(peer, tasks);
        final DiameterInitiatorSession session = openedSession(peer, tasks);

        // Fire first Tw — sends DWR (pending)
        tasks.get(1).run();
        // Fire second Tw — transitions to SUSPECT
        tasks.get(2).run();
        assertThat(session.getWatchdogState()).isEqualTo(WatchdogState.SUSPECT);

        // WHEN — an unrelated answer proves the link alive (cancels pending DWR → OKAY)
        final CapabilitiesExchangeAnswer.In unrelatedAnswer = (CapabilitiesExchangeAnswer.In)
                DiameterMessageFactory.createForParsing(DiameterConstants.CMD_CAPABILITIES_EXCHANGE,
                        0, false, true, false, new HopByHopId(99), new EndToEndId(99), false);
        session.onMessage(unrelatedAnswer);

        // THEN
        assertThat(session.getWatchdogState()).isEqualTo(WatchdogState.OKAY);
    }

    @Test
    @SuppressWarnings("unchecked")
    void it_cancels_Tw_timer_on_message_received() throws Exception {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        stubSend(peer);

        final EventLoop eventLoop = mock(EventLoop.class);
        when(peer.eventLoop()).thenReturn(eventLoop);
        final ScheduledFuture<?> cerTimeout = mock(ScheduledFuture.class);
        final ScheduledFuture<?> twTimer = mock(ScheduledFuture.class);
        final ScheduledFuture<?> twTimer2 = mock(ScheduledFuture.class);
        when(eventLoop.schedule(any(Runnable.class), anyLong(), any(TimeUnit.class)))
                .thenReturn(
                        (ScheduledFuture) cerTimeout,
                        (ScheduledFuture) twTimer,
                        (ScheduledFuture) twTimer2);

        final DiameterInitiatorSession session = new DiameterInitiatorSession(CONFIG, () -> {});
        session.onConnected(peer);
        final CapabilitiesExchangeAnswer.In cea = captureAndBuildCea(peer, DiameterConstants.RES_DIAMETER_SUCCESS);
        session.onMessage(cea);
        Mockito.clearInvocations(peer);

        // WHEN — any message arrives in I_OPEN
        final ReAuthRequest.In rar = (ReAuthRequest.In)
                DiameterMessageFactory.createForParsing(DiameterConstants.CMD_RE_AUTH, 0, true, true, false, new HopByHopId(10), new EndToEndId(20), false);
        session.onMessage(rar);

        // THEN — the Tw timer was cancelled and rescheduled
        verify(twTimer).cancel(false);
    }

    // -------------------------------------------------------------------------
    // DPR/DPA - graceful shutdown
    // -------------------------------------------------------------------------

    @Test
    void it_closes_immediately_when_stop_is_called() throws Exception {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        stubSend(peer);
        when(peer.close()).thenReturn(mock(ChannelFuture.class));
        final DiameterInitiatorSession session = openedSession(peer);

        // WHEN
        session.stop();

        // THEN
        assertThat(session.getPeerState()).isEqualTo(PeerState.CLOSED);
        verify(peer).close();
        verify(peer, never()).send(any(DisconnectPeerRequest.Out.class), any(HopByHopId.class), any(EndToEndId.class));
    }

    @Test
    void it_transitions_to_CLOSING_when_stopGracefully_is_called() throws Exception {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        stubSend(peer);
        final DiameterInitiatorSession session = openedSession(peer);

        // WHEN
        session.stopGracefully();

        // THEN
        assertThat(session.getPeerState()).isEqualTo(PeerState.CLOSING);
    }

    @Test
    void it_sends_DPR_when_stopGracefully_is_called() throws Exception {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        stubSend(peer);
        final DiameterInitiatorSession session = openedSession(peer);

        // WHEN
        session.stopGracefully();

        // THEN
        verify(peer).send(any(DisconnectPeerRequest.Out.class), any(HopByHopId.class), any(EndToEndId.class));
    }

    @Test
    void it_sets_disconnect_cause_DCC_DO_NOT_WANT_TO_TALK_TO_YOU_in_DPR_when_stopGracefully_is_called() throws Exception {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        stubSend(peer);
        final DiameterInitiatorSession session = openedSession(peer);

        // WHEN
        session.stopGracefully();

        // THEN
        final ArgumentCaptor<DisconnectPeerRequest.Out> captor =
                ArgumentCaptor.forClass(DisconnectPeerRequest.Out.class);
        verify(peer).send(captor.capture(), any(HopByHopId.class), any(EndToEndId.class));
        assertThat(captor.getValue().getDisconnectCause())
                .isEqualTo(DiameterConstants.DCC_DO_NOT_WANT_TO_TALK_TO_YOU);
    }

    @Test
    void it_sends_DPR_with_REBOOTING_when_closeGracefully_is_called() throws Exception {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        stubSend(peer);
        final DiameterInitiatorSession session = openedSession(peer);

        // WHEN
        session.closeGracefully();

        // THEN
        final ArgumentCaptor<DisconnectPeerRequest.Out> captor =
                ArgumentCaptor.forClass(DisconnectPeerRequest.Out.class);
        verify(peer).send(captor.capture(), any(HopByHopId.class), any(EndToEndId.class));
        assertThat(captor.getValue().getDisconnectCause())
                .isEqualTo(DiameterConstants.DCC_REBOOTING);
    }

    @Test
    void it_rejects_send_in_CLOSING_state() throws Exception {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        stubSend(peer);
        final DiameterInitiatorSession session = openedSession(peer);
        session.stopGracefully();

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
        final DiameterInitiatorSession session = openedSession(peer);
        session.stopGracefully();
        final DisconnectPeerAnswer.In dpa = captureAndBuildDpa(peer);

        // WHEN
        session.onMessage(dpa);

        // THEN
        assertThat(session.getPeerState()).isEqualTo(PeerState.CLOSED);
    }

    @Test
    void it_closes_peer_when_DPA_arrives_in_CLOSING() throws Exception {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        stubSend(peer);
        when(peer.close()).thenReturn(mock(ChannelFuture.class));
        final DiameterInitiatorSession session = openedSession(peer);
        session.stopGracefully();
        final DisconnectPeerAnswer.In dpa = captureAndBuildDpa(peer);

        // WHEN
        session.onMessage(dpa);

        // THEN
        verify(peer).close();
        assertThat(session.getPeerState()).isEqualTo(PeerState.CLOSED);
    }

    @Test
    void it_does_nothing_when_stop_is_called_in_non_open_state() {
        // GIVEN
        final DiameterInitiatorSession session = new DiameterInitiatorSession(CONFIG, () -> {});

        // WHEN - session is in CLOSED state
        session.stop();

        // THEN
        assertThat(session.getPeerState()).isEqualTo(PeerState.CLOSED);
    }

    // -------------------------------------------------------------------------
    // Reconnect (Tc timer)
    // -------------------------------------------------------------------------

    @Test
    void it_schedules_Tc_timer_on_unexpected_disconnect() throws Exception {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        stubSend(peer);
        final List<Runnable> tasks = new ArrayList<>();
        stubEventLoop(peer, tasks);
        final DiameterInitiatorSession session = openedSession(peer, tasks);

        // WHEN
        session.onDisconnected(peer);

        // THEN — a Tc timer task was scheduled (after the CER timeout and Tw timer)
        assertThat(tasks).hasSizeGreaterThanOrEqualTo(3);
    }

    @Test
    void it_calls_reconnect_when_Tc_timer_fires() throws Exception {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        stubSend(peer);
        final List<Runnable> tasks = new ArrayList<>();
        stubEventLoop(peer, tasks);
        openedSession(peer, tasks);

        final var reconnectCalled = new java.util.concurrent.atomic.AtomicBoolean(false);
        final DiameterInitiatorSession session = new DiameterInitiatorSession(CONFIG, () -> reconnectCalled.set(true));
        session.onConnected(peer);
        final CapabilitiesExchangeAnswer.In cea = captureAndBuildCea(peer, DiameterConstants.RES_DIAMETER_SUCCESS);
        session.onMessage(cea);
        org.mockito.Mockito.clearInvocations(peer);

        // trigger unexpected disconnect
        session.onDisconnected(peer);
        final Runnable tcTask = tasks.get(tasks.size() - 1);

        // WHEN — Tc timer fires
        tcTask.run();

        // THEN
        assertThat(reconnectCalled).isTrue();
    }

    @Test
    void it_does_not_schedule_Tc_timer_after_stop() throws Exception {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        stubSend(peer);
        when(peer.close()).thenReturn(mock(ChannelFuture.class));
        final List<Runnable> tasks = new ArrayList<>();
        stubEventLoop(peer, tasks);
        final DiameterInitiatorSession session = openedSession(peer, tasks);
        session.stop();
        final int taskCountAfterStop = tasks.size();

        // WHEN — transport closes
        session.onDisconnected(peer);

        // THEN — no additional Tc timer was scheduled
        assertThat(tasks).hasSize(taskCountAfterStop);
    }

    @Test
    void it_does_not_schedule_Tc_timer_after_graceful_shutdown() throws Exception {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        stubSend(peer);
        when(peer.close()).thenReturn(mock(ChannelFuture.class));
        final List<Runnable> tasks = new ArrayList<>();
        stubEventLoop(peer, tasks);
        final DiameterInitiatorSession session = openedSession(peer, tasks);
        session.stopGracefully();
        final int taskCountAfterStop = tasks.size();

        // WHEN — transport closes after graceful DPR
        session.onDisconnected(peer);

        // THEN — no additional Tc timer was scheduled
        assertThat(tasks).hasSize(taskCountAfterStop);
    }

    @Test
    void it_schedules_Tc_timer_after_closeGracefully() throws Exception {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        stubSend(peer);
        when(peer.close()).thenReturn(mock(ChannelFuture.class));
        final List<Runnable> tasks = new ArrayList<>();
        stubEventLoop(peer, tasks);
        final DiameterInitiatorSession session = openedSession(peer, tasks);
        session.closeGracefully();
        final int taskCountAfterClose = tasks.size();

        // WHEN — transport closes after DPR/DPA
        session.onDisconnected(peer);

        // THEN — Tc timer was scheduled for reconnect
        assertThat(tasks).hasSizeGreaterThan(taskCountAfterClose);
    }

    @Test
    @SuppressWarnings("unchecked")
    void it_cancels_Tc_timer_when_stop_is_called_while_reconnect_is_pending() throws Exception {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        stubSend(peer);

        final EventLoop eventLoop = mock(EventLoop.class);
        when(peer.eventLoop()).thenReturn(eventLoop);

        final ScheduledFuture<?> tcTimer = mock(ScheduledFuture.class);
        //noinspection rawtypes
        when(eventLoop.schedule(any(Runnable.class), anyLong(), any(TimeUnit.class)))
                .thenReturn(
                    mock(ScheduledFuture.class),
                    mock(ScheduledFuture.class),
                    (ScheduledFuture) tcTimer);

        final DiameterInitiatorSession session = new DiameterInitiatorSession(CONFIG, () -> {});
        session.onConnected(peer);
        final CapabilitiesExchangeAnswer.In cea = captureAndBuildCea(peer, DiameterConstants.RES_DIAMETER_SUCCESS);
        session.onMessage(cea);
        org.mockito.Mockito.clearInvocations(peer);

        // simulate unexpected disconnect → schedules Tc timer
        session.onDisconnected(peer);

        // WHEN — user calls stop() while Tc timer is pending
        session.stop();

        // THEN
        verify(tcTimer).cancel(false);
    }

    @Test
    void it_transitions_to_CLOSED_after_DPR_received_in_I_OPEN() throws Exception {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        stubSend(peer);
        when(peer.close()).thenReturn(mock(ChannelFuture.class));
        final DiameterInitiatorSession session = openedSession(peer);
        final DisconnectPeerRequest.In dpr = (DisconnectPeerRequest.In)
                DiameterMessageFactory.createForParsing(DiameterConstants.CMD_DISCONNECT_PEER, 0, true, true, false, new HopByHopId(10), new EndToEndId(20), false);

        // WHEN
        session.onMessage(dpr);

        // THEN
        assertThat(session.getPeerState()).isEqualTo(PeerState.CLOSED);
    }

    @Test
    void it_sends_DPA_when_DPR_received_in_I_OPEN() throws Exception {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        stubSend(peer);
        when(peer.close()).thenReturn(mock(ChannelFuture.class));
        final DiameterInitiatorSession session = openedSession(peer);
        final DisconnectPeerRequest.In dpr = (DisconnectPeerRequest.In)
                DiameterMessageFactory.createForParsing(DiameterConstants.CMD_DISCONNECT_PEER, 0, true, true, false, new HopByHopId(10), new EndToEndId(20), false);

        // WHEN
        session.onMessage(dpr);

        // THEN
        final ArgumentCaptor<DisconnectPeerAnswer.Out> captor =
                ArgumentCaptor.forClass(DisconnectPeerAnswer.Out.class);
        verify(peer).send(captor.capture());
        assertThat(captor.getValue().hopByHopId()).isEqualTo(new HopByHopId(10));
    }

    @Test
    void it_closes_peer_when_DPR_received_in_I_OPEN() throws Exception {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        stubSend(peer);
        when(peer.close()).thenReturn(mock(ChannelFuture.class));
        final DiameterInitiatorSession session = openedSession(peer);
        final DisconnectPeerRequest.In dpr = (DisconnectPeerRequest.In)
                DiameterMessageFactory.createForParsing(DiameterConstants.CMD_DISCONNECT_PEER, 0, true, true, false, new HopByHopId(10), new EndToEndId(20), false);

        // WHEN
        session.onMessage(dpr);

        // THEN
        verify(peer).close();
        assertThat(session.getPeerState()).isEqualTo(PeerState.CLOSED);
    }

    @Test
    void it_calls_reconnect_when_Tc_timer_fires_after_tcp_never_connected() {
        // GIVEN — onConnected never fires; simulates TCP connect failure where
        // DiameterPeerHandler constructs a peer from ctx.channel() for channelInactive
        final DiameterPeer peer = mock(DiameterPeer.class);
        final List<Runnable> tasks = new ArrayList<>();
        stubEventLoop(peer, tasks);

        final var reconnectCalled = new AtomicBoolean(false);
        final DiameterInitiatorSession session = new DiameterInitiatorSession(CONFIG, () -> reconnectCalled.set(true));

        // WHEN — channelInactive fires without a prior channelActive
        session.onDisconnected(peer);
        tasks.get(tasks.size() - 1).run();

        // THEN
        assertThat(reconnectCalled).isTrue();
    }

    // -------------------------------------------------------------------------
    // onParseError
    // -------------------------------------------------------------------------

    @Test
    void it_sends_error_answer_with_Failed_AVP_on_AVPParseException() throws Exception {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        stubSend(peer);
        final DiameterInitiatorSession session = openedSession(peer);
        final AVP offending = AVP.createRaw(new AVPKey(999, 0), true, true, false, new byte[0]);
        final AVPParseException cause = new AVPParseException(
                DiameterConstants.RES_DIAMETER_AVP_UNSUPPORTED,
                280, true, 0, new HopByHopId(1), new EndToEndId(2), offending, null);

        // WHEN
        session.onParseError(peer, cause);

        // THEN
        final ArgumentCaptor<ErrorAnswer.Out> captor = ArgumentCaptor.forClass(ErrorAnswer.Out.class);
        verify(peer).send(captor.capture());
        assertThat(captor.getValue().getFailedAVP()).isNotNull();
        assertThat(captor.getValue().getFailedAVP().getAVPs()).containsExactly(offending);
        verify(peer, never()).close();
    }

    @Test
    void it_sends_error_answer_and_does_not_schedule_Tc_timer_on_DiameterResultCodeException() throws Exception {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        stubSend(peer);
        when(peer.close()).thenReturn(mock(ChannelFuture.class));
        final List<Runnable> tasks = new ArrayList<>();
        stubEventLoop(peer, tasks);
        final DiameterInitiatorSession session = openedSession(peer, tasks);
        final DiameterResultCodeException cause = new DiameterResultCodeException(
                DiameterConstants.RES_DIAMETER_UNSUPPORTED_VERSION,
                280, false, 0, new HopByHopId(1), new EndToEndId(2), null);

        // WHEN
        session.onParseError(peer, cause);
        final int taskCountAfterParseError = tasks.size();
        session.onDisconnected(peer);

        // THEN — stop() was called: no Tc timer scheduled, reconnect suppressed
        verify(peer).send(any(ErrorAnswer.Out.class));
        assertThat(session.getPeerState()).isEqualTo(PeerState.CLOSED);
        assertThat(tasks).hasSize(taskCountAfterParseError);
    }

    @Test
    void it_closes_peer_and_schedules_Tc_timer_on_base_DiameterException() throws Exception {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        stubSend(peer);
        when(peer.close()).thenReturn(mock(ChannelFuture.class));
        final List<Runnable> tasks = new ArrayList<>();
        stubEventLoop(peer, tasks);
        final DiameterInitiatorSession session = openedSession(peer, tasks);
        final DiameterException cause = new DiameterException("corrupt byte stream");
        final int taskCountBeforeError = tasks.size();

        // WHEN — corrupt stream: raw close, no shuttingDown set
        session.onParseError(peer, cause);
        session.onDisconnected(peer);

        // THEN — peer closed, Tc timer scheduled for reconnect
        verify(peer).close();
        assertThat(session.getPeerState()).isEqualTo(PeerState.CLOSED);
        verify(peer, never()).send(any(OutgoingAnswer.class));
        assertThat(tasks).hasSizeGreaterThan(taskCountBeforeError);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static DiameterInitiatorSession openedSession(final DiameterPeer peer) throws Exception {
        stubEventLoop(peer);
        final DiameterInitiatorSession session = new DiameterInitiatorSession(CONFIG, () -> {});
        session.onConnected(peer);
        final CapabilitiesExchangeAnswer.In cea = captureAndBuildCea(peer, DiameterConstants.RES_DIAMETER_SUCCESS);
        session.onMessage(cea);
        Mockito.clearInvocations(peer);
        return session;
    }

    /**
     * Opens a session and wires a task list so each scheduled runnable can be
     * retrieved by index:
     * <ul>
     *   <li>0 — CER request timeout</li>
     *   <li>1 — first Tw timer (scheduled on entering I_OPEN)</li>
     *   <li>2 — Tw timer rescheduled after first DWR sent</li>
     *   <li>3 — Tw timer rescheduled after SUSPECT transition</li>
     * </ul>
     */
    private static DiameterInitiatorSession openedSession(
            final DiameterPeer peer, final List<Runnable> tasks) throws Exception {
        final DiameterInitiatorSession session = new DiameterInitiatorSession(CONFIG, () -> {});
        session.onConnected(peer);
        final CapabilitiesExchangeAnswer.In cea = captureAndBuildCea(peer, DiameterConstants.RES_DIAMETER_SUCCESS);
        session.onMessage(cea);
        Mockito.clearInvocations(peer);
        return session;
    }

    /**
     * Captures the HopByHopId used for the CER (does NOT clear mock invocations).
     * Use when you only need the hop-by-hop value.
     */
    private static HopByHopId capturedCerHopByHop(final DiameterPeer peer) {
        final ArgumentCaptor<HopByHopId> hbh = ArgumentCaptor.forClass(HopByHopId.class);
        verify(peer).send(any(CapabilitiesExchangeRequest.Out.class), hbh.capture(), any(EndToEndId.class));
        Mockito.clearInvocations(peer);
        return hbh.getValue();
    }

    /**
     * After the session sends a CER, captures its identifiers, creates a matching
     * {@code CapabilitiesExchangeAnswer.In} with the given result code, and clears
     * mock invocations on the peer.
     */
    @SuppressWarnings("unchecked")
    private static CapabilitiesExchangeAnswer.In captureAndBuildCea(
            final DiameterPeer peer, final long resultCode) throws Exception {
        final ArgumentCaptor<HopByHopId> hbh = ArgumentCaptor.forClass(HopByHopId.class);
        final ArgumentCaptor<EndToEndId> e2e = ArgumentCaptor.forClass(EndToEndId.class);
        verify(peer).send(any(CapabilitiesExchangeRequest.Out.class), hbh.capture(), e2e.capture());
        Mockito.clearInvocations(peer);

        final CapabilitiesExchangeRequest.In fakeCer = (CapabilitiesExchangeRequest.In)
                DiameterMessageFactory.createForParsing(DiameterConstants.CMD_CAPABILITIES_EXCHANGE,
                        0, true, true, false, hbh.getValue(), e2e.getValue(), false);
        final CapabilitiesExchangeAnswer.Out ceaOut = DiameterMessageFactory.createAnswer(fakeCer, resultCode);
        return toIncomingAnswer(ceaOut);
    }

    /**
     * After the session sends a CER (application request, not the capability exchange one),
     * captures identifiers and builds a matching incoming CEA.
     * Expects the most recently sent OutgoingRequest to be a CapabilitiesExchangeRequest.Out.
     */
    @SuppressWarnings("unchecked")
    private static CapabilitiesExchangeAnswer.In captureAndBuildCeaForRequest(
            final DiameterPeer peer) throws Exception {
        final ArgumentCaptor<HopByHopId> hbh = ArgumentCaptor.forClass(HopByHopId.class);
        final ArgumentCaptor<EndToEndId> e2e = ArgumentCaptor.forClass(EndToEndId.class);
        verify(peer).send(any(CapabilitiesExchangeRequest.Out.class), hbh.capture(), e2e.capture());
        Mockito.clearInvocations(peer);

        final CapabilitiesExchangeRequest.In fakeCer = (CapabilitiesExchangeRequest.In)
                DiameterMessageFactory.createForParsing(DiameterConstants.CMD_CAPABILITIES_EXCHANGE,
                        0, true, true, false, hbh.getValue(), e2e.getValue(), false);
        final CapabilitiesExchangeAnswer.Out ceaOut =
                DiameterMessageFactory.createAnswer(fakeCer, DiameterConstants.RES_DIAMETER_SUCCESS);
        return toIncomingAnswer(ceaOut);
    }

    /**
     * After the session sends a DPR, captures its identifiers and builds a matching
     * incoming DPA.
     */
    @SuppressWarnings("unchecked")
    private static DisconnectPeerAnswer.In captureAndBuildDpa(final DiameterPeer peer) throws Exception {
        final ArgumentCaptor<HopByHopId> hbh = ArgumentCaptor.forClass(HopByHopId.class);
        final ArgumentCaptor<EndToEndId> e2e = ArgumentCaptor.forClass(EndToEndId.class);
        verify(peer).send(any(DisconnectPeerRequest.Out.class), hbh.capture(), e2e.capture());
        Mockito.clearInvocations(peer);

        final DisconnectPeerRequest.In fakeDpr = (DisconnectPeerRequest.In)
                DiameterMessageFactory.createForParsing(DiameterConstants.CMD_DISCONNECT_PEER,
                        0, true, true, false, hbh.getValue(), e2e.getValue(), false);
        final DisconnectPeerAnswer.Out dpaOut =
                DiameterMessageFactory.createAnswer(fakeDpr, DiameterConstants.RES_DIAMETER_SUCCESS);
        return toIncomingAnswer(dpaOut);
    }

    /**
     * Builds a {@code DeviceWatchdogAnswer.In} with the given identifiers by
     * creating an Out, serializing it, and parsing back.
     */
    @SuppressWarnings("unchecked")
    private static DeviceWatchdogAnswer.In buildMatchingDwa(
            final HopByHopId hbh, final EndToEndId e2e) throws Exception {
        final DeviceWatchdogRequest.In fakeDwr = (DeviceWatchdogRequest.In)
                DiameterMessageFactory.createForParsing(DiameterConstants.CMD_DEVICE_WATCHDOG, 0, true, true, false, hbh, e2e, false);
        final DeviceWatchdogAnswer.Out dwaOut =
                DiameterMessageFactory.createAnswer(fakeDwr, DiameterConstants.RES_DIAMETER_SUCCESS);
        return toIncomingAnswer(dwaOut);
    }

    @SuppressWarnings("unchecked")
    private static <A extends IncomingAnswer> A toIncomingAnswer(final OutgoingAnswer answer) throws Exception {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        answer.writeTo(new DataOutputStream(baos));
        return (A) Command.parseMessage(ByteBuffer.wrap(baos.toByteArray()));
    }

    @SuppressWarnings("unchecked")
    private static void stubSend(final DiameterPeer peer) {
        when(peer.send(any(OutgoingAnswer.class))).thenAnswer(ignored -> immediatelyCompletedFuture());
        when(peer.send(any(OutgoingRequest.class), any(HopByHopId.class), any(EndToEndId.class)))
                .thenReturn(mock(ChannelFuture.class));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static ChannelFuture immediatelyCompletedFuture() {
        final ChannelFuture future = mock(ChannelFuture.class);
        when(future.addListener(any())).thenAnswer(inv -> {
            ((GenericFutureListener) inv.getArgument(0)).operationComplete(future);
            return future;
        });
        return future;
    }

    @SuppressWarnings("unchecked")
    private static void stubEventLoop(final DiameterPeer peer) {
        final EventLoop eventLoop = mock(EventLoop.class);
        when(peer.eventLoop()).thenReturn(eventLoop);
        when(eventLoop.schedule(any(Runnable.class), anyLong(), any(TimeUnit.class)))
                .thenReturn(mock(ScheduledFuture.class));
    }

    @SuppressWarnings("unchecked")
    private static void stubEventLoop(final DiameterPeer peer, final List<Runnable> capturedTasks) {
        final EventLoop eventLoop = mock(EventLoop.class);
        when(peer.eventLoop()).thenReturn(eventLoop);
        when(eventLoop.schedule(any(Runnable.class), anyLong(), any(TimeUnit.class)))
                .thenAnswer(inv -> {
                    capturedTasks.add(inv.getArgument(0));
                    return mock(ScheduledFuture.class);
                });
    }

    private static ChannelFuture failedWriteFuture() {
        final ChannelFuture future = mock(ChannelFuture.class);
        when(future.isSuccess()).thenReturn(false);
        when(future.cause()).thenReturn(new RuntimeException("write failed"));
        when(future.addListener(any())).thenAnswer(invocation -> {
            final GenericFutureListener<ChannelFuture> listener = invocation.getArgument(0);
            try {
                listener.operationComplete(future);
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
            return future;
        });
        return future;
    }
}
