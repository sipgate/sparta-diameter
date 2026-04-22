package com.sipgate.sparta.diameter.base.session;

import com.sipgate.sparta.diameter.base.core.*;
import com.sipgate.sparta.diameter.base.messages.CapabilitiesExchangeAnswer;
import com.sipgate.sparta.diameter.base.messages.CapabilitiesExchangeRequest;
import com.sipgate.sparta.diameter.base.messages.DisconnectPeerRequest;
import com.sipgate.sparta.diameter.base.transport.DiameterPeer;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * Diameter session for the initiator (I-) side of a connection.
 */
public final class DiameterInitiatorSession extends DiameterSession {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiameterInitiatorSession.class);

    private final Consumer<DiameterInitiatorSession> reconnect;
    private final Timer tcTimer = new Timer();
    private final AtomicReference<TimerTask> reconnectTask = new AtomicReference<>();

    public DiameterInitiatorSession(final DiameterNodeConfig config, final Consumer<DiameterInitiatorSession> reconnect) {
        this(config, reconnect, new SimpleMeterRegistry());
    }

    public DiameterInitiatorSession(final DiameterNodeConfig config, final Consumer<DiameterInitiatorSession> reconnect,
                                    final MeterRegistry meterRegistry) {
        super(config, meterRegistry);
        this.reconnect = reconnect;
    }

    @Override
    public void stop() {
        shuttingDown = true;
        stopTcTimer();
        super.stop();
    }

    @Override
    public void stopGracefully() {
        shuttingDown = true;
        stopTcTimer();
        super.stopGracefully();
    }

    @Override
    public void closeGracefully() {
        stopTcTimer();
        super.closeGracefully();
    }

    @Override
    public void onConnected(final DiameterPeer peer) {
        LOGGER.info("local {} connected to remote {}", peer.localAddress(), peer.remoteAddress());
        super.onConnected(peer);
        this.peerState = PeerState.WAIT_I_CEA;
        sendAndTrack(buildCer()).whenComplete((cea, err) -> {
            if (err == null) {
                handleCea(cea);
                return;
            }

            if (err instanceof final DiameterErrorAnswerException e) {
                LOGGER.info("CER rejected with {}", ResultCodeUtil.describeResultCode(e.getAnswer()));
            } else {
                LOGGER.error("error during CER/CEA", err);
            }

            if (peerState == PeerState.WAIT_I_CEA) {
                closePeer();
            }
        });
    }

    @Override
    public void onMessage(final IncomingCommand command) {
        if (peerState == PeerState.I_OPEN) {
            handleWatchdog(command);
            if (command instanceof final DisconnectPeerRequest.In dpr) {
                handleInboundDpr(dpr);
                return;
            }
            if (command instanceof final IncomingRequest<?> request) {
                dispatchInboundRequest(request);
                return;
            }
        }

        if (command instanceof final IncomingAnswer answer) {
            complete(answer);
        }
    }

    @Override
    public void onDisconnected(final DiameterPeer peer) {
        super.onDisconnected(peer);
        if (!shuttingDown) {
            scheduleReconnect();
        }
    }

    public void scheduleReconnect() {
        LOGGER.debug("scheduling reconnect");
        reconnectTask.getAndUpdate(oldTask -> {
            stopTcTimer(oldTask);
            final long tcMs = config.getTc().toMillis();
            final var newTask = reconnectTimerTask(reconnect, this);
            tcTimer.schedule(newTask, tcMs);
            return newTask;
        });
    }

    private static TimerTask reconnectTimerTask(final Consumer<DiameterInitiatorSession> reconnect,
                                                final DiameterInitiatorSession diameterInitiatorSession) {
        return new TimerTask() {
            @Override
            public void run() {
                reconnect.accept(diameterInitiatorSession);
            }
        };
    }

    private void stopTcTimer() {
        reconnectTask.getAndUpdate(DiameterInitiatorSession::stopTcTimer);
    }

    private static TimerTask stopTcTimer(final TimerTask task) {
        if (task != null) {
            task.cancel();
        }

        return null;
    }

    private void handleCea(final CapabilitiesExchangeAnswer.In cea) {
        if (cea.getResultCode() == DiameterConstants.RES_DIAMETER_SUCCESS) {
            peerState = PeerState.I_OPEN;
            startWatchdog();
        } else {
            closePeer();
        }
    }

    private CapabilitiesExchangeRequest.Out buildCer() {
        final CapabilitiesExchangeRequest.Out cer =
            new CapabilitiesExchangeRequest.Out();
        populateCapabilityAvps(cer);
        return cer;
    }
}
