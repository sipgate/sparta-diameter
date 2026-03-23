package com.sipgate.sparta.diameter.session;

import com.sipgate.sparta.diameter.core.DiameterConstants;
import com.sipgate.sparta.diameter.messages.rfc6733.CapabilitiesExchangeAnswer;
import com.sipgate.sparta.diameter.messages.rfc6733.CapabilitiesExchangeRequest;
import com.sipgate.sparta.diameter.messages.rfc6733.DeviceWatchdogAnswer;
import com.sipgate.sparta.diameter.messages.rfc6733.DeviceWatchdogRequest;
import com.sipgate.sparta.diameter.messages.rfc6733.ReAuthAnswer;
import com.sipgate.sparta.diameter.messages.rfc6733.ReAuthRequest;
import com.sipgate.sparta.diameter.transport.DiameterPeer;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoop;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.ScheduledFuture;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.UncheckedIOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
        final DiameterInitiatorSession session = new DiameterInitiatorSession(CONFIG, () -> {});

        // WHEN
        session.onDisconnected(null);

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
        when(peer.send(any())).thenReturn(mock(ChannelFuture.class));
        stubEventLoop(peer);
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
        stubEventLoop(peer);
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
        when(peer.send(any())).thenReturn(writeFail);
        when(peer.close()).thenReturn(mock(ChannelFuture.class));
        stubEventLoop(peer);
        final DiameterInitiatorSession session = new DiameterInitiatorSession(CONFIG, () -> {});

        // WHEN
        session.onConnected(peer);

        // THEN
        assertThat(session.getPeerState()).isEqualTo(PeerState.CLOSED);
    }

    @Test
    void it_transitions_to_I_OPEN_on_successful_CEA() {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        when(peer.send(any())).thenReturn(mock(ChannelFuture.class));
        stubEventLoop(peer);
        final DiameterInitiatorSession session = new DiameterInitiatorSession(CONFIG, () -> {});
        session.onConnected(peer);
        final int cerHopByHop = capturedCerHopByHop(peer);
        final CapabilitiesExchangeAnswer cea = CapabilitiesExchangeAnswer.create(cerHopByHop, 2);
        cea.setResultCode(DiameterConstants.RES_DIAMETER_SUCCESS);

        // WHEN
        session.onMessage(peer, cea);

        // THEN
        assertThat(session.getPeerState()).isEqualTo(PeerState.I_OPEN);
    }

    @Test
    void it_ignores_CEA_with_wrong_hop_by_hop_id() {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        when(peer.send(any())).thenReturn(mock(ChannelFuture.class));
        stubEventLoop(peer);
        final DiameterInitiatorSession session = new DiameterInitiatorSession(CONFIG, () -> {});
        session.onConnected(peer);
        final int cerHopByHop = capturedCerHopByHop(peer);
        final CapabilitiesExchangeAnswer cea = CapabilitiesExchangeAnswer.create(cerHopByHop + 1, 2);
        cea.setResultCode(DiameterConstants.RES_DIAMETER_SUCCESS);

        // WHEN
        session.onMessage(peer, cea);

        // THEN: still waiting — rogue CEA had no effect
        assertThat(session.getPeerState()).isEqualTo(PeerState.WAIT_I_CEA);
    }

    @Test
    void it_transitions_to_CLOSED_on_failed_CEA() {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        when(peer.send(any())).thenReturn(mock(ChannelFuture.class));
        when(peer.close()).thenReturn(mock(ChannelFuture.class));
        stubEventLoop(peer);
        final DiameterInitiatorSession session = new DiameterInitiatorSession(CONFIG, () -> {});
        session.onConnected(peer);
        final int cerHopByHop = capturedCerHopByHop(peer);
        final CapabilitiesExchangeAnswer cea = CapabilitiesExchangeAnswer.create(cerHopByHop, 2);
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
        stubEventLoop(peer);
        final DiameterInitiatorSession session = new DiameterInitiatorSession(CONFIG, () -> {});
        session.onConnected(peer);
        final int cerHopByHop = capturedCerHopByHop(peer);
        final CapabilitiesExchangeAnswer cea = CapabilitiesExchangeAnswer.create(cerHopByHop, 2);
        cea.setResultCode(DiameterConstants.RES_DIAMETER_NO_COMMON_APPLICATION);

        // WHEN
        session.onMessage(peer, cea);

        // THEN
        verify(peer).close();
    }

    // -------------------------------------------------------------------------
    // send() — pending-request map
    // -------------------------------------------------------------------------

    @Test
    void it_returns_failed_future_when_not_in_OPEN_state() {
        // GIVEN
        final DiameterInitiatorSession session = new DiameterInitiatorSession(CONFIG, () -> {});
        final CapabilitiesExchangeRequest request = CapabilitiesExchangeRequest.create(1, 2);

        // WHEN
        final CompletableFuture<CapabilitiesExchangeAnswer> future = session.send(request);

        // THEN
        assertThat(future).isCompletedExceptionally();
    }

    @Test
    void it_sends_request_to_peer_when_in_OPEN_state() {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        when(peer.send(any())).thenReturn(mock(ChannelFuture.class));
        final DiameterInitiatorSession session = openedSession(peer);
        final CapabilitiesExchangeRequest request = CapabilitiesExchangeRequest.create(42, 43);

        // WHEN
        session.send(request);

        // THEN
        verify(peer).send(request);
    }

    @Test
    void it_completes_future_when_matching_answer_arrives() {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        when(peer.send(any())).thenReturn(mock(ChannelFuture.class));
        final DiameterInitiatorSession session = openedSession(peer);
        final CapabilitiesExchangeRequest request = CapabilitiesExchangeRequest.create(42, 43);
        final CompletableFuture<CapabilitiesExchangeAnswer> future = session.send(request);
        final CapabilitiesExchangeAnswer answer = CapabilitiesExchangeAnswer.create(42, 43);
        answer.setResultCode(DiameterConstants.RES_DIAMETER_SUCCESS);

        // WHEN
        session.onMessage(peer, answer);

        // THEN
        assertThat(future).isCompletedWithValue(answer);
    }

    @Test
    void it_ignores_answer_with_unknown_hop_by_hop_id() {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        when(peer.send(any())).thenReturn(mock(ChannelFuture.class));
        final DiameterInitiatorSession session = openedSession(peer);
        final CapabilitiesExchangeRequest request = CapabilitiesExchangeRequest.create(42, 43);
        final CompletableFuture<CapabilitiesExchangeAnswer> future = session.send(request);
        final CapabilitiesExchangeAnswer answer = CapabilitiesExchangeAnswer.create(99, 99);
        answer.setResultCode(DiameterConstants.RES_DIAMETER_SUCCESS);

        // WHEN
        session.onMessage(peer, answer);

        // THEN
        assertThat(future).isNotDone();
    }

    @Test
    void it_fails_future_when_write_fails() {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        final ChannelFuture writeFail = failedWriteFuture();
        // first call (CER) succeeds; second call (request) fails at write
        when(peer.send(any())).thenReturn(mock(ChannelFuture.class), writeFail);
        stubEventLoop(peer);
        final DiameterInitiatorSession session = openedSession(peer);
        final CapabilitiesExchangeRequest request = CapabilitiesExchangeRequest.create(42, 43);

        // WHEN
        final CompletableFuture<CapabilitiesExchangeAnswer> future = session.send(request);

        // THEN
        assertThat(future).isCompletedExceptionally();
    }

    @Test
    void it_fails_pending_futures_on_disconnect() {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        when(peer.send(any())).thenReturn(mock(ChannelFuture.class));
        final DiameterInitiatorSession session = openedSession(peer);
        final CapabilitiesExchangeRequest request = CapabilitiesExchangeRequest.create(42, 43);
        final CompletableFuture<CapabilitiesExchangeAnswer> future = session.send(request);

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
    void it_fails_future_with_TimeoutException_after_request_timeout() {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        when(peer.send(any())).thenReturn(mock(ChannelFuture.class));

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
        final CapabilitiesExchangeAnswer cea = CapabilitiesExchangeAnswer.create(capturedCerHopByHop(peer), 2);
        cea.setResultCode(DiameterConstants.RES_DIAMETER_SUCCESS);
        session.onMessage(peer, cea);
        Mockito.clearInvocations(peer);

        final CapabilitiesExchangeRequest request = CapabilitiesExchangeRequest.create(42, 43);
        final CompletableFuture<CapabilitiesExchangeAnswer> future = session.send(request);

        // WHEN — simulate the timeout task firing
        capturedTask.get().run();

        // THEN
        assertThatThrownBy(future::join)
                .isInstanceOf(CompletionException.class)
                .hasCauseInstanceOf(TimeoutException.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    void it_cancels_timeout_when_answer_arrives() {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        when(peer.send(any())).thenReturn(mock(ChannelFuture.class));

        final EventLoop eventLoop = mock(EventLoop.class);
        when(peer.eventLoop()).thenReturn(eventLoop);
        final ScheduledFuture<?> cerTimeout = mock(ScheduledFuture.class);
        final ScheduledFuture<?> twTimer = mock(ScheduledFuture.class);
        final ScheduledFuture<?> requestTimeout = mock(ScheduledFuture.class);
        when(eventLoop.schedule(any(Runnable.class), anyLong(), any(TimeUnit.class)))
                .thenReturn((ScheduledFuture) cerTimeout, (ScheduledFuture) twTimer, (ScheduledFuture) requestTimeout);

        final DiameterInitiatorSession session = new DiameterInitiatorSession(CONFIG, () -> {});
        session.onConnected(peer);
        final CapabilitiesExchangeAnswer cea = CapabilitiesExchangeAnswer.create(capturedCerHopByHop(peer), 2);
        cea.setResultCode(DiameterConstants.RES_DIAMETER_SUCCESS);
        session.onMessage(peer, cea);
        Mockito.clearInvocations(peer);

        final CapabilitiesExchangeRequest request = CapabilitiesExchangeRequest.create(42, 43);
        session.send(request);
        final CapabilitiesExchangeAnswer answer = CapabilitiesExchangeAnswer.create(42, 43);
        answer.setResultCode(DiameterConstants.RES_DIAMETER_SUCCESS);

        // WHEN
        session.onMessage(peer, answer);

        // THEN
        verify(requestTimeout).cancel(false);
    }

    @Test
    @SuppressWarnings("unchecked")
    void it_cancels_all_timeouts_on_disconnect() {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        when(peer.send(any())).thenReturn(mock(ChannelFuture.class));

        final EventLoop eventLoop = mock(EventLoop.class);
        when(peer.eventLoop()).thenReturn(eventLoop);
        final ScheduledFuture<?> cerTimeout = mock(ScheduledFuture.class);
        final ScheduledFuture<?> twTimer = mock(ScheduledFuture.class);
        final ScheduledFuture<?> requestTimeout = mock(ScheduledFuture.class);
        when(eventLoop.schedule(any(Runnable.class), anyLong(), any(TimeUnit.class)))
                .thenReturn((ScheduledFuture) cerTimeout, (ScheduledFuture) twTimer, (ScheduledFuture) requestTimeout);

        final DiameterInitiatorSession session = new DiameterInitiatorSession(CONFIG, () -> {});
        session.onConnected(peer);
        final CapabilitiesExchangeAnswer cea = CapabilitiesExchangeAnswer.create(capturedCerHopByHop(peer), 2);
        cea.setResultCode(DiameterConstants.RES_DIAMETER_SUCCESS);
        session.onMessage(peer, cea);
        Mockito.clearInvocations(peer);

        final CapabilitiesExchangeRequest request = CapabilitiesExchangeRequest.create(42, 43);
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
        session.setHandler(ReAuthRequest.class, req -> new CompletableFuture<>());

        // WHEN / THEN
        assertThatThrownBy(() -> session.setHandler(ReAuthRequest.class, req -> new CompletableFuture<>()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void it_dispatches_inbound_request_to_registered_handler() {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        when(peer.send(any())).thenReturn(mock(ChannelFuture.class));
        final DiameterInitiatorSession session = openedSession(peer);
        final ReAuthAnswer answer = ReAuthAnswer.create(10, 20);
        answer.setResultCode(DiameterConstants.RES_DIAMETER_SUCCESS);
        session.setHandler(ReAuthRequest.class, req -> CompletableFuture.completedFuture(answer));

        // WHEN
        session.onMessage(peer, ReAuthRequest.create(10, 20));

        // THEN
        verify(peer).send(answer);
    }

    @Test
    void it_sends_COMMAND_UNSUPPORTED_for_unhandled_request() {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        when(peer.send(any())).thenReturn(mock(ChannelFuture.class));
        final DiameterInitiatorSession session = openedSession(peer);

        // WHEN
        session.onMessage(peer, ReAuthRequest.create(10, 20));

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
        when(peer.send(any())).thenReturn(mock(ChannelFuture.class));
        final DiameterInitiatorSession session = openedSession(peer);
        final CompletableFuture<ReAuthAnswer> failing = new CompletableFuture<>();
        failing.completeExceptionally(new RuntimeException("handler error"));
        session.setHandler(ReAuthRequest.class, req -> failing);

        // WHEN
        session.onMessage(peer, ReAuthRequest.create(10, 20));

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
    void it_transitions_watchdog_to_OKAY_on_entering_I_OPEN() {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        when(peer.send(any())).thenReturn(mock(ChannelFuture.class));
        final List<Runnable> tasks = new ArrayList<>();
        stubEventLoop(peer, tasks);
        final DiameterInitiatorSession session = new DiameterInitiatorSession(CONFIG, () -> {});
        session.onConnected(peer);
        final int cerHopByHop = capturedCerHopByHop(peer);
        final CapabilitiesExchangeAnswer cea = CapabilitiesExchangeAnswer.create(cerHopByHop, 2);
        cea.setResultCode(DiameterConstants.RES_DIAMETER_SUCCESS);

        // WHEN
        session.onMessage(peer, cea);

        // THEN
        assertThat(session.getWatchdogState()).isEqualTo(WatchdogState.OKAY);
    }

    @Test
    void it_sends_DWR_when_Tw_timer_fires() {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        when(peer.send(any())).thenReturn(mock(ChannelFuture.class));
        final List<Runnable> tasks = new ArrayList<>();
        stubEventLoop(peer, tasks);
        openedSession(peer, tasks);

        // WHEN — fire Tw timer (index 1, after the CER timeout at index 0)
        tasks.get(1).run();

        // THEN
        final ArgumentCaptor<DeviceWatchdogRequest> captor =
                ArgumentCaptor.forClass(DeviceWatchdogRequest.class);
        verify(peer).send(captor.capture());
        assertThat(captor.getValue()).isInstanceOf(DeviceWatchdogRequest.class);
    }

    @Test
    void it_transitions_to_SUSPECT_when_Tw_fires_with_unanswered_DWR() {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        when(peer.send(any())).thenReturn(mock(ChannelFuture.class));
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
    void it_transitions_to_DOWN_and_closes_peer_when_Tw_fires_in_SUSPECT() {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        when(peer.send(any())).thenReturn(mock(ChannelFuture.class));
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
        verify(peer).close();
    }

    @Test
    void it_sends_DWA_when_DWR_received_in_I_OPEN() {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        when(peer.send(any())).thenReturn(mock(ChannelFuture.class));
        final List<Runnable> tasks = new ArrayList<>();
        stubEventLoop(peer, tasks);
        final DiameterInitiatorSession session = openedSession(peer, tasks);
        final DeviceWatchdogRequest dwr = DeviceWatchdogRequest.create(77, 88);

        // WHEN
        session.onMessage(peer, dwr);

        // THEN — a DWA was sent back
        final ArgumentCaptor<DeviceWatchdogAnswer> captor =
                ArgumentCaptor.forClass(DeviceWatchdogAnswer.class);
        verify(peer).send(captor.capture());
        assertThat(captor.getValue().getHopByHopIdentifier()).isEqualTo(77);
    }

    @Test
    void it_rejects_handler_registration_for_DWR_once_watchdog_is_active() {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        when(peer.send(any())).thenReturn(mock(ChannelFuture.class));
        final List<Runnable> tasks = new ArrayList<>();
        stubEventLoop(peer, tasks);
        final DiameterInitiatorSession session = openedSession(peer, tasks);

        // WHEN / THEN — session occupies the DWR slot internally on entering I_OPEN
        assertThatThrownBy(() -> session.setHandler(DeviceWatchdogRequest.class, req -> new CompletableFuture<>()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void it_transitions_from_SUSPECT_to_OKAY_when_DWA_arrives() {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        when(peer.send(any())).thenReturn(mock(ChannelFuture.class));
        final List<Runnable> tasks = new ArrayList<>();
        stubEventLoop(peer, tasks);
        final DiameterInitiatorSession session = openedSession(peer, tasks);

        // Fire first Tw — sends DWR, store its hop-by-hop
        tasks.get(1).run();
        final ArgumentCaptor<DeviceWatchdogRequest> dwrCaptor =
                ArgumentCaptor.forClass(DeviceWatchdogRequest.class);
        verify(peer).send(dwrCaptor.capture());
        final int dwrHopByHop = dwrCaptor.getValue().getHopByHopIdentifier();
        Mockito.clearInvocations(peer);

        // Fire second Tw — transitions to SUSPECT
        tasks.get(2).run();
        assertThat(session.getWatchdogState()).isEqualTo(WatchdogState.SUSPECT);

        // WHEN — DWA arrives
        final DeviceWatchdogAnswer dwa = DeviceWatchdogAnswer.create(dwrHopByHop, 88);
        session.onMessage(peer, dwa);

        // THEN
        assertThat(session.getWatchdogState()).isEqualTo(WatchdogState.OKAY);
    }

    @Test
    void it_transitions_from_SUSPECT_to_OKAY_when_other_message_proves_link_alive() {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        when(peer.send(any())).thenReturn(mock(ChannelFuture.class));
        final List<Runnable> tasks = new ArrayList<>();
        stubEventLoop(peer, tasks);
        final DiameterInitiatorSession session = openedSession(peer, tasks);

        // Fire first Tw — sends DWR (pending)
        tasks.get(1).run();
        // Fire second Tw — transitions to SUSPECT
        tasks.get(2).run();
        assertThat(session.getWatchdogState()).isEqualTo(WatchdogState.SUSPECT);

        // WHEN — a non-DWA message arrives, proving the link alive
        final CapabilitiesExchangeAnswer answer = CapabilitiesExchangeAnswer.create(99, 99);
        answer.setResultCode(DiameterConstants.RES_DIAMETER_SUCCESS);
        session.onMessage(peer, answer);

        // THEN
        assertThat(session.getWatchdogState()).isEqualTo(WatchdogState.OKAY);
    }

    @Test
    @SuppressWarnings("unchecked")
    void it_cancels_Tw_timer_on_message_received() {
        // GIVEN
        final DiameterPeer peer = mock(DiameterPeer.class);
        when(peer.send(any())).thenReturn(mock(ChannelFuture.class));

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
        final int cerHopByHop = capturedCerHopByHop(peer);
        final CapabilitiesExchangeAnswer cea = CapabilitiesExchangeAnswer.create(cerHopByHop, 2);
        cea.setResultCode(DiameterConstants.RES_DIAMETER_SUCCESS);
        session.onMessage(peer, cea);
        Mockito.clearInvocations(peer);

        // WHEN — any message arrives in I_OPEN
        final ReAuthRequest rar = ReAuthRequest.create(10, 20);
        session.onMessage(peer, rar);

        // THEN — the Tw timer was cancelled and rescheduled
        verify(twTimer).cancel(false);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static DiameterInitiatorSession openedSession(final DiameterPeer peer) {
        stubEventLoop(peer);
        final DiameterInitiatorSession session = new DiameterInitiatorSession(CONFIG, () -> {});
        session.onConnected(peer);
        final int cerHopByHop = capturedCerHopByHop(peer);
        final CapabilitiesExchangeAnswer cea = CapabilitiesExchangeAnswer.create(cerHopByHop, 2);
        cea.setResultCode(DiameterConstants.RES_DIAMETER_SUCCESS);
        session.onMessage(peer, cea);
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
            final DiameterPeer peer, final List<Runnable> tasks) {
        final DiameterInitiatorSession session = new DiameterInitiatorSession(CONFIG, () -> {});
        session.onConnected(peer);
        final int cerHopByHop = capturedCerHopByHop(peer);
        final CapabilitiesExchangeAnswer cea = CapabilitiesExchangeAnswer.create(cerHopByHop, 2);
        cea.setResultCode(DiameterConstants.RES_DIAMETER_SUCCESS);
        session.onMessage(peer, cea);
        Mockito.clearInvocations(peer);
        return session;
    }

    private static int capturedCerHopByHop(final DiameterPeer peer) {
        final ArgumentCaptor<CapabilitiesExchangeRequest> captor =
                ArgumentCaptor.forClass(CapabilitiesExchangeRequest.class);
        verify(peer).send(captor.capture());
        Mockito.clearInvocations(peer);
        return captor.getValue().getHopByHopIdentifier();
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
