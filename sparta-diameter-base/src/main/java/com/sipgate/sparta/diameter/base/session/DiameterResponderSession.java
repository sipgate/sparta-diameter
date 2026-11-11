package com.sipgate.sparta.diameter.base.session;

import com.sipgate.sparta.diameter.base.core.Command;
import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.DiameterMessageFactory;
import com.sipgate.sparta.diameter.base.core.IncomingCommand;
import com.sipgate.sparta.diameter.base.core.IncomingAnswer;
import com.sipgate.sparta.diameter.base.core.IncomingRequest;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.messages.CapabilitiesExchangeAnswer;
import com.sipgate.sparta.diameter.base.messages.CapabilitiesExchangeRequest;
import com.sipgate.sparta.diameter.base.messages.DisconnectPeerRequest;
import com.sipgate.sparta.diameter.base.transport.DiameterPeer;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
/**
 * Diameter session for the responder (R-) side of a connection.
 *
 * <p>Subclassable (not final) so routing agents can bind a configured peer
 * identity at capabilities-exchange time via {@link #onCapabilitiesExchange};
 * request-time binding alone never sees peers that only answer (an HSS behind
 * a relay).</p>
 */
public class DiameterResponderSession extends DiameterSession {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiameterResponderSession.class);

    public DiameterResponderSession(final DiameterNodeConfig config) {
        this(config, new SimpleMeterRegistry());
    }

    public DiameterResponderSession(final DiameterNodeConfig config, final MeterRegistry meterRegistry) {
        super(config, meterRegistry);
    }

    @Override
    public void onConnected(final DiameterPeer peer) {
        LOGGER.info("remote {} connected to local {}", peer.remoteAddress(), peer.localAddressesForLog());
        super.onConnected(peer);
    }

    @Override
    public void onMessage(final IncomingCommand command) {
        if (command instanceof final CapabilitiesExchangeRequest.In cer) {
            handleCer(cer);
            return;
        }

        if (peerState == PeerState.R_OPEN) {
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

    /**
     * Application hook fired after a SUCCESSFUL capabilities exchange: the CEA
     * has been sent, the peer is in {@link PeerState#R_OPEN} and the watchdog is
     * running. Routing agents bind the connection to a configured peer identity
     * ({@code cer.getOriginHost()}) here — BEFORE any application request
     * arrives; inbound peers that only answer (e.g. an HSS behind a relay)
     * would never be bound by request-time tracking.
     *
     * <p>Default: no-op. Exceptions from overrides propagate like from
     * {@link #onMessage}.</p>
     *
     * @param cer the capabilities exchange request of the now-open peer
     */
    protected void onCapabilitiesExchange(final CapabilitiesExchangeRequest.In cer) {
        // no-op
    }

    /**
     * Application hook fired when a CER arrives on a connection that is already
     * {@link PeerState#R_OPEN} (a repeated capabilities exchange), before it is
     * processed. The CER is then handled like the first one: a CEA is sent, the watchdog
     * is restarted (never duplicated) and {@link #onCapabilitiesExchange} fires again on
     * success — or the connection is closed if no common application remains.
     *
     * <p>Default: no-op.</p>
     *
     * @param cer the repeated capabilities exchange request
     */
    protected void onCapabilitiesReExchange(final CapabilitiesExchangeRequest.In cer) {
        // no-op
    }

    private void handleCer(final CapabilitiesExchangeRequest.In cer) {
        if (peerState == PeerState.R_OPEN) {
            LOGGER.info("received CER on already open connection from {}", cer.getOriginHost());
            onCapabilitiesReExchange(cer);
        }
        recordRemoteOriginStateId(cer.getOriginHost(), cer.getOriginStateId());
        final List<Long> remoteAuthIds = cer.getAuthApplicationIds();
        final List<Long> remoteAcctIds = cer.getAcctApplicationIds();
        final List<Long> remoteVendorSpecificAppIds = extractVendorSpecificAppIds(cer);

        final DiameterNodeConfig.Capabilities configCapabilities = config.getCapabilities();
        if (negotiator.hasCommonApplication(configCapabilities, remoteAuthIds, remoteAcctIds, remoteVendorSpecificAppIds)) {
            send(buildCea(cer, DiameterConstants.RES_DIAMETER_SUCCESS));
            peerState = PeerState.R_OPEN;
            startWatchdog();
            onCapabilitiesExchange(cer);
        } else {
            LOGGER.warn("capabilities exchange did not find any common application");
            LOGGER.debug(
                "configCapabilities={}, remoteAuthIds={}, remoteAcctIds={}, remoteVendorSpecificAppIds={}",
                configCapabilities, remoteAuthIds, remoteAcctIds, remoteVendorSpecificAppIds);
            send(buildCea(cer, DiameterConstants.RES_DIAMETER_NO_COMMON_APPLICATION));
            closePeer();
        }
    }

    private static List<Long> extractVendorSpecificAppIds(final CapabilitiesExchangeRequest.In cer) {
        final List<Long> appIds = new ArrayList<>();
        for (final AVPContainer container : cer.getVendorSpecificApplicationIds()) {
            final AVP authAppAvp = container.findAVP(new AVPKey(DiameterConstants.AVP_AUTH_APPLICATION_ID, 0));
            if (authAppAvp != null) {
                appIds.add(authAppAvp.getDataAsUnsignedInt());
            }
            final AVP acctAppAvp = container.findAVP(new AVPKey(DiameterConstants.AVP_ACCT_APPLICATION_ID, 0));
            if (acctAppAvp != null) {
                appIds.add(acctAppAvp.getDataAsUnsignedInt());
            }
        }
        return appIds;
    }

    private CapabilitiesExchangeAnswer.Out buildCea(final CapabilitiesExchangeRequest.In cer,
                                                    final long resultCode) {
        final CapabilitiesExchangeAnswer.Out cea = DiameterMessageFactory.createAnswer(cer, resultCode);
        populateCapabilityAvps(cea);
        return cea;
    }

    /**
     * Test seam for Origin-State-Id stamping assertions, mirroring the
     * {@code getWatchdogState()} seam pattern on {@code DiameterSession}.
     */
    CapabilitiesExchangeAnswer.Out buildCeaForTest() throws Exception {
        final java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        final CapabilitiesExchangeRequest.Out cerOut = new CapabilitiesExchangeRequest.Out();
        cerOut.addAuthApplicationId(5L);
        cerOut.writeTo(new java.io.DataOutputStream(baos),
                new com.sipgate.sparta.diameter.base.core.HopByHopId(1),
                new com.sipgate.sparta.diameter.base.core.EndToEndId(2));
        final CapabilitiesExchangeRequest.In cer = (CapabilitiesExchangeRequest.In)
                Command.parseMessage(java.nio.ByteBuffer.wrap(baos.toByteArray()));
        return buildCea(cer, DiameterConstants.RES_DIAMETER_SUCCESS);
    }
}
