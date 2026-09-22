package com.sipgate.sparta.diameter.base.session.relay;

import com.sipgate.sparta.diameter.base.core.Command;
import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.DiameterMessageFactory;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.IncomingAnswer;
import com.sipgate.sparta.diameter.base.core.IncomingRequest;
import com.sipgate.sparta.diameter.base.core.OutgoingAnswer;
import com.sipgate.sparta.diameter.base.core.OutgoingRequest;
import com.sipgate.sparta.diameter.base.messages.AbortSessionAnswer;
import com.sipgate.sparta.diameter.base.messages.AbortSessionRequest;
import com.sipgate.sparta.diameter.base.messages.CapabilitiesExchangeRequest;
import com.sipgate.sparta.diameter.base.session.DiameterNodeConfig;
import com.sipgate.sparta.diameter.base.session.DiameterResponderSession;
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
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Session-level relay flow: {@code relay} registers a translation keyed by the outbound
 * hop-by-hop identifier, the answer dispatch restores it, and the request-timeout task
 * (already scheduled on the peer's event loop by the pending-request machinery) cleans
 * the translation up on expiry.
 */
class DiameterSessionRelayTest {

    private static final InetAddress LOCALHOST;

    static {
        try {
            LOCALHOST = InetAddress.getByName("127.0.0.1");
        } catch (final UnknownHostException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static final DiameterNodeConfig CONFIG = new DiameterNodeConfig(
            "dra.example.com",
            "example.com",
            Collections.singletonList(LOCALHOST),
            0L,
            "sparta",
            new DiameterNodeConfig.Capabilities(
                    Collections.singletonList(5L), Collections.emptyList(),
                    Collections.emptyList(), Collections.emptyList()));

    private static final HopByHopId ORIGINAL_HBH = new HopByHopId(100);
    private static final EndToEndId ORIGINAL_E2E = new EndToEndId(200);

    @Test
    void it_relays_with_a_fresh_hop_by_hop_and_the_preserved_end_to_end() {
        // GIVEN — upstream session holds the connection the request arrived on;
        // egress session (the one relaying) is OPEN on its own connection
        final DiameterSessionPair up = openedSession();
        final DiameterSessionPair egress = openedSession();
        final IncomingRequest<?> received = buildIncomingRequest(ORIGINAL_HBH, ORIGINAL_E2E);
        final AbortSessionRequest.Out forward = new AbortSessionRequest.Out();

        // WHEN
        final CompletableFuture<IncomingAnswer> relayed =
                egress.session().relay(up.session(), received, forward);

        // THEN — sent on the egress connection with a fresh hop-by-hop id; RFC 6733 §3:
        // the end-to-end identifier of the original request is preserved
        final ArgumentCaptor<HopByHopId> hbh = ArgumentCaptor.forClass(HopByHopId.class);
        final ArgumentCaptor<EndToEndId> e2e = ArgumentCaptor.forClass(EndToEndId.class);
        verify(egress.peer()).send(any(OutgoingRequest.class), hbh.capture(), e2e.capture());
        assertThat(hbh.getValue()).isNotEqualTo(ORIGINAL_HBH);
        assertThat(e2e.getValue()).isEqualTo(ORIGINAL_E2E);
        assertThat(relayed).isNotDone();
    }

    @Test
    void it_never_mutates_the_forwarded_request() {
        // GIVEN — the forward carries an explicit Origin-Host that is not ours
        final DiameterSessionPair up = openedSession();
        final DiameterSessionPair egress = openedSession();
        final IncomingRequest<?> received = buildIncomingRequest(ORIGINAL_HBH, ORIGINAL_E2E);
        final AbortSessionRequest.Out forward = new AbortSessionRequest.Out();
        forward.setOriginHost("origin.example.com");
        final int avpCountBefore = forward.getAVPs().size();

        // WHEN
        egress.session().relay(up.session(), received, forward);

        // THEN — relay bypasses the fill-if-absent Origin stamping of plain send():
        // the original origin identity passes through untouched (RFC 6733 §6.1.9)
        assertThat(forward.getOriginHost()).isEqualTo("origin.example.com");
        assertThat(forward.getAVPs()).hasSize(avpCountBefore);

        // and a forward without Origin AVPs stays without them
        final AbortSessionRequest.Out plainForward = new AbortSessionRequest.Out();
        egress.session().relay(up.session(), received, plainForward);
        assertThat(plainForward.getOriginHost()).isNull();
        assertThat(plainForward.getOriginRealm()).isNull();
    }

    @Test
    void it_completes_the_relay_future_and_sends_the_restored_answer_upstream() {
        // GIVEN — a relayed request in flight; remember the outbound identifiers
        final DiameterSessionPair up = openedSession();
        final DiameterSessionPair egress = openedSession();
        final RelayHop hop = startRelay(up, egress);

        // WHEN — the downstream answer arrives on the egress connection
        final IncomingAnswer downstreamAnswer =
                buildIncomingAnswer(hop.outbound().hopByHop(), hop.outbound().endToEnd(), null);
        egress.session().onMessage(downstreamAnswer);

        // THEN — the future completes with the downstream answer …
        assertThat(hop.future()).isCompletedWithValue(downstreamAnswer);

        // … and the answer goes upstream with the original hop-by-hop identifier
        // restored (RFC 6733 §6.2.2) and the same end-to-end identifier
        final ArgumentCaptor<OutgoingAnswer> restored = ArgumentCaptor.forClass(OutgoingAnswer.class);
        verify(up.peer()).send(restored.capture());
        assertThat(restored.getValue().hopByHopId()).isEqualTo(ORIGINAL_HBH);
        assertThat(restored.getValue().endToEndId()).isEqualTo(ORIGINAL_E2E);
        assertThat(restored.getValue().getCommandCode()).isEqualTo(DiameterConstants.CMD_ABORT_SESSION);
        assertThat(restored.getValue().getResultCode()).isEqualTo(DiameterConstants.RES_DIAMETER_SUCCESS);
    }

    @Test
    void it_does_not_override_the_origin_of_a_relayed_answer() {
        // GIVEN
        final DiameterSessionPair up = openedSession();
        final DiameterSessionPair egress = openedSession();
        final RelayHop hop = startRelay(up, egress);

        // WHEN — the downstream answer carries the answering endpoint's origin identity
        final IncomingAnswer downstreamAnswer =
                buildIncomingAnswer(hop.outbound().hopByHop(), hop.outbound().endToEnd(), "answerer.example.com");
        egress.session().onMessage(downstreamAnswer);

        // THEN — the answer is forwarded unchanged; we do not stamp our own identity
        // (RFC 6733 §6.2.2)
        final ArgumentCaptor<OutgoingAnswer> restored = ArgumentCaptor.forClass(OutgoingAnswer.class);
        verify(up.peer()).send(restored.capture());
        assertThat(restored.getValue().getOriginHost()).isEqualTo("answerer.example.com");
    }

    @Test
    void it_fails_the_relay_future_when_the_connection_is_not_open() {
        // GIVEN — egress session never connected (CLOSED)
        final DiameterSessionPair up = openedSession();
        final DiameterResponderSession egress = new DiameterResponderSession(CONFIG);

        // WHEN
        final CompletableFuture<IncomingAnswer> relayed =
                egress.relay(up.session(), buildIncomingRequest(ORIGINAL_HBH, ORIGINAL_E2E),
                        new AbortSessionRequest.Out());

        // THEN — mirrors the fail-fast contract of send() when not OPEN (ADR-0004)
        assertThat(relayed).isCompletedExceptionally();
        assertThat(causeOf(relayed)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void it_cleans_up_the_translation_on_request_timeout() {
        // GIVEN — egress with a short request timeout; the timeout task the
        // pending-request machinery schedules on the peer's event loop is captured
        final DiameterSessionPair up = openedSession();
        final DiameterSessionPair egress = openedSessionWithTimeout(configWithRequestTimeout(Duration.ofMillis(50)));
        final RelayHop hop = startRelay(up, egress);
        final List<Runnable> scheduledDuringRelay =
                egress.scheduledTasks().subList(egress.taskCountAtOpen(), egress.scheduledTasks().size());

        // WHEN — the scheduled request timeout fires (there is no separate relay cleanup
        // timer: the translation is paired with the pending request and is removed
        // together with it)
        for (final Runnable task : scheduledDuringRelay) {
            task.run();
        }

        // THEN
        assertThat(hop.future()).isCompletedExceptionally();
        assertThat(causeOf(hop.future())).isInstanceOf(TimeoutException.class);

        // and a late answer under the same identifiers is no longer forwarded upstream —
        // the translation is gone
        Mockito.clearInvocations(up.peer());
        final IncomingAnswer late = buildIncomingAnswer(hop.outbound().hopByHop(), ORIGINAL_E2E, null);
        egress.session().onMessage(late);
        verify(up.peer(), never()).send(any(OutgoingAnswer.class));
    }

    @Test
    void it_reports_no_restoration_for_unsolicited_answers() {
        // GIVEN — nothing relayed on this session
        final DiameterSessionPair egress = openedSession();

        // WHEN / THEN — the public restoration entry point is a no-op for answers
        // without a translation (the answer dispatch logs them as unsolicited)
        final IncomingAnswer unknown = buildIncomingAnswer(new HopByHopId(555), new EndToEndId(666), null);
        assertThat(egress.session().sendAnswerRestored(unknown)).isFalse();
    }

    // ---------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------

    /** Outbound identifiers of a relayed request write. */
    private record Ids(HopByHopId hopByHop, EndToEndId endToEnd) {}

    /** Result of one started relay: the future plus the identifiers used downstream. */
    private record RelayHop(CompletableFuture<IncomingAnswer> future, Ids outbound) {}

    /** An OPEN responder session on a mocked peer; scheduled event-loop tasks are captured. */
    private record DiameterSessionPair(
            DiameterResponderSession session,
            DiameterPeer peer,
            List<Runnable> scheduledTasks,
            int taskCountAtOpen) {}

    private static DiameterSessionPair openedSession() {
        return openedSessionWithTimeout(CONFIG);
    }

    /**
     * Builds an OPEN responder session on a mocked peer: onConnected establishes the
     * peer reference, the simulated common-application CER transitions to R_OPEN.
     */
    private static DiameterSessionPair openedSessionWithTimeout(final DiameterNodeConfig config) {
        final DiameterPeer peer = mock(DiameterPeer.class);
        final List<Runnable> scheduledTasks = new ArrayList<>();
        final EventLoop eventLoop = mock(EventLoop.class);
        when(peer.eventLoop()).thenReturn(eventLoop);
        when(eventLoop.schedule(any(Runnable.class), anyLong(), any(TimeUnit.class)))
                .thenAnswer(inv -> {
                    scheduledTasks.add(inv.getArgument(0));
                    return mock(ScheduledFuture.class);
                });
        when(peer.send(any(OutgoingRequest.class), any(HopByHopId.class), any(EndToEndId.class)))
                .thenReturn(mock(ChannelFuture.class));
        when(peer.send(any(OutgoingAnswer.class)))
                .thenAnswer(ignored -> immediatelyCompletedFuture());
        final DiameterResponderSession session = new DiameterResponderSession(config);
        session.onConnected(peer);
        try {
            // CER from the (simulated) downstream peer with a common application id
            final CapabilitiesExchangeRequest.Out cerOut = new CapabilitiesExchangeRequest.Out();
            cerOut.addAuthApplicationId(5L);
            final IncomingRequest<?> cer = toIncoming(cerOut, new HopByHopId(77), new EndToEndId(88));
            session.onMessage(cer);
        } catch (final Exception e) {
            throw new IllegalStateException("cannot open session", e);
        }
        Mockito.clearInvocations(peer);
        return new DiameterSessionPair(session, peer, scheduledTasks, scheduledTasks.size());
    }

    /**
     * Relays an ASR from the upstream to the egress session and captures the outbound
     * identifiers used on the egress connection.
     */
    private static RelayHop startRelay(final DiameterSessionPair up, final DiameterSessionPair egress) {
        final IncomingRequest<?> received = buildIncomingRequest(ORIGINAL_HBH, ORIGINAL_E2E);
        final CompletableFuture<IncomingAnswer> relayed =
                egress.session().relay(up.session(), received, new AbortSessionRequest.Out());
        final ArgumentCaptor<HopByHopId> hbh = ArgumentCaptor.forClass(HopByHopId.class);
        final ArgumentCaptor<EndToEndId> e2e = ArgumentCaptor.forClass(EndToEndId.class);
        verify(egress.peer()).send(any(OutgoingRequest.class), hbh.capture(), e2e.capture());
        Mockito.clearInvocations(up.peer(), egress.peer());
        return new RelayHop(relayed, new Ids(hbh.getValue(), e2e.getValue()));
    }

    private static DiameterNodeConfig configWithRequestTimeout(final Duration requestTimeout) {
        return new DiameterNodeConfig(
                CONFIG.getOriginHost(),
                CONFIG.getOriginRealm(),
                CONFIG.getHostIpAddresses(),
                CONFIG.getVendorId(),
                CONFIG.getProductName(),
                CONFIG.getCapabilities(),
                DiameterNodeConfig.TWINIT_DEFAULT,
                DiameterNodeConfig.TC_RECOMMENDED,
                requestTimeout);
    }

    private static AbortSessionRequest.In buildIncomingRequest(final HopByHopId hbh, final EndToEndId e2e) {
        try {
            final AbortSessionRequest.Out out = new AbortSessionRequest.Out();
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            out.writeTo(new DataOutputStream(baos), hbh, e2e);
            return (AbortSessionRequest.In) Command.parseMessage(ByteBuffer.wrap(baos.toByteArray()));
        } catch (final Exception e) {
            throw new IllegalStateException("cannot build incoming ASR", e);
        }
    }

    private static IncomingAnswer buildIncomingAnswer(final HopByHopId hbh, final EndToEndId e2e,
                                                      final String originHost) {
        try {
            final AbortSessionRequest.In fakeRequest = (AbortSessionRequest.In)
                    DiameterMessageFactory.createForParsing(DiameterConstants.CMD_ABORT_SESSION,
                            DiameterConstants.APP_DIAMETER_COMMON_MESSAGES, true, true, false, hbh, e2e, false);
            final AbortSessionAnswer.Out answerOut =
                    DiameterMessageFactory.createAnswer(fakeRequest, DiameterConstants.RES_DIAMETER_SUCCESS);
            if (originHost != null) {
                answerOut.setOriginHost(originHost);
            }
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            answerOut.writeTo(new DataOutputStream(baos));
            return (IncomingAnswer) Command.parseMessage(ByteBuffer.wrap(baos.toByteArray()));
        } catch (final Exception e) {
            throw new IllegalStateException("cannot build incoming answer", e);
        }
    }

    private static IncomingRequest<?> toIncoming(final OutgoingRequest<?> out, final HopByHopId hbh,
                                                 final EndToEndId e2e) throws Exception {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        out.writeTo(new DataOutputStream(baos), hbh, e2e);
        return (IncomingRequest<?>) Command.parseMessage(ByteBuffer.wrap(baos.toByteArray()));
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

    private static Throwable causeOf(final CompletableFuture<?> future) {
        try {
            future.join();
            throw new AssertionError("expected exceptional completion");
        } catch (final CompletionException e) {
            return e.getCause();
        }
    }
}
