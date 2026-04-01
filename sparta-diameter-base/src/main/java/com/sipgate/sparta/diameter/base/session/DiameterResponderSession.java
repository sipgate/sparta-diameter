package com.sipgate.sparta.diameter.base.session;

import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.DiameterMessageFactory;
import com.sipgate.sparta.diameter.base.core.IncomingAnswer;
import com.sipgate.sparta.diameter.base.core.IncomingCommand;
import com.sipgate.sparta.diameter.base.core.IncomingRequest;
import com.sipgate.sparta.diameter.base.core.OutgoingAnswer;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;
import com.sipgate.sparta.diameter.base.messages.CapabilitiesExchangeAnswer;
import com.sipgate.sparta.diameter.base.messages.CapabilitiesExchangeRequest;
import com.sipgate.sparta.diameter.base.messages.DisconnectPeerRequest;
import com.sipgate.sparta.diameter.base.transport.DiameterPeer;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

import java.util.ArrayList;
import java.util.List;
/**
 * Diameter session for the responder (R-) side of a connection.
 */
public final class DiameterResponderSession extends DiameterSession {

    public DiameterResponderSession(final DiameterNodeConfig config) {
        this(config, new SimpleMeterRegistry());
    }

    public DiameterResponderSession(final DiameterNodeConfig config, final MeterRegistry meterRegistry) {
        super(config, meterRegistry);
    }

    @Override
    public void onConnected(final DiameterPeer peer) {
        this.peer = peer;
    }

    @Override
    public void onMessage(final DiameterPeer peer, final IncomingCommand command) {
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
            if (command instanceof final IncomingRequest<?, ?> request) {
                dispatchInboundRequest(request);
                return;
            }
        }

        if (command instanceof final IncomingAnswer<?> answer) {
            complete(answer);
        }
    }

    private void handleCer(final CapabilitiesExchangeRequest.In cer) {
        final List<Long> remoteAuthIds = cer.getAuthApplicationIds();
        final List<Long> remoteAcctIds = cer.getAcctApplicationIds();
        final List<Long> remoteVendorSpecificAppIds = extractVendorSpecificAppIds(cer);
        final int commandCode = cer.getCommandCode();
        final int applicationId = cer.getApplicationId();

        if (negotiator.hasCommonApplication(config.getCapabilities(), remoteAuthIds, remoteAcctIds, remoteVendorSpecificAppIds)) {
            peer.send(buildCea(cer, DiameterConstants.RES_DIAMETER_SUCCESS));
            meters.recordSent(commandCode, applicationId, DiameterSessionMeters.COMMAND_TYPE_ANSWER);
            peerState = PeerState.R_OPEN;
            startWatchdog();
        } else {
            peer.send(buildCea(cer, DiameterConstants.RES_DIAMETER_NO_COMMON_APPLICATION));
            meters.recordSent(commandCode, applicationId, DiameterSessionMeters.COMMAND_TYPE_ANSWER);
            peerState = PeerState.CLOSED;
            peer.close();
        }
    }

    private static List<Long> extractVendorSpecificAppIds(final CapabilitiesExchangeRequest.In cer) {
        final List<Long> appIds = new ArrayList<>();
        for (final GroupedAVP grouped : cer.getVendorSpecificApplicationIds()) {
            final AVP authAppAvp = grouped.findAVP(new AVPKey(DiameterConstants.AVP_AUTH_APPLICATION_ID, 0));
            if (authAppAvp != null) {
                appIds.add(authAppAvp.getDataAsUnsignedInt());
            }
            final AVP acctAppAvp = grouped.findAVP(new AVPKey(DiameterConstants.AVP_ACCT_APPLICATION_ID, 0));
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
}
