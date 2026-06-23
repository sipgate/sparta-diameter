package com.sipgate.sparta.diameter.base.messages;

import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.DiameterPackageFactory;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.IncomingCommand;
import com.sipgate.sparta.diameter.base.core.OutgoingAnswer;

public final class BaseMessageFactory implements DiameterPackageFactory {

    // No application-id guard here, by design (see AGENTS.md "Diameter Message Factories").
    // Base command codes (257–282) live in the IANA-reserved base range that no 3GPP application
    // shares, so there is no cross-app stealing risk. ASR/ASA, RAR/RAA, STR/STA, ACR/ACA are
    // application-agnostic per RFC 6733 and may legally carry any application-id, so a blanket
    // `applicationId == 0` check would be semantically wrong and would block vendor-app usage.

    @Override
    public IncomingCommand createForParsing(final int commandCode, final int applicationId,
                                            final boolean isRequest,
                                            final HopByHopId hopByHop, final EndToEndId endToEnd,
                                            final boolean retransmitted) {
        return switch (commandCode) {
            case DiameterConstants.CMD_ABORT_SESSION -> isRequest
                    ? new AbortSessionRequest.In(hopByHop, endToEnd, retransmitted)
                    : new AbortSessionAnswer.In(hopByHop, endToEnd);
            case DiameterConstants.CMD_ACCOUNTING -> isRequest
                    ? new AccountingRequest.In(hopByHop, endToEnd, retransmitted)
                    : new AccountingAnswer.In(hopByHop, endToEnd);
            case DiameterConstants.CMD_CAPABILITIES_EXCHANGE -> isRequest
                    ? new CapabilitiesExchangeRequest.In(hopByHop, endToEnd, retransmitted)
                    : new CapabilitiesExchangeAnswer.In(hopByHop, endToEnd);
            case DiameterConstants.CMD_DEVICE_WATCHDOG -> isRequest
                    ? new DeviceWatchdogRequest.In(hopByHop, endToEnd, retransmitted)
                    : new DeviceWatchdogAnswer.In(hopByHop, endToEnd);
            case DiameterConstants.CMD_DISCONNECT_PEER -> isRequest
                    ? new DisconnectPeerRequest.In(hopByHop, endToEnd, retransmitted)
                    : new DisconnectPeerAnswer.In(hopByHop, endToEnd);
            case DiameterConstants.CMD_RE_AUTH -> isRequest
                    ? new ReAuthRequest.In(hopByHop, endToEnd, retransmitted)
                    : new ReAuthAnswer.In(hopByHop, endToEnd);
            case DiameterConstants.CMD_SESSION_TERMINATION -> isRequest
                    ? new SessionTerminationRequest.In(hopByHop, endToEnd, retransmitted)
                    : new SessionTerminationAnswer.In(hopByHop, endToEnd);
            default -> null;
        };
    }

    @Override
    public OutgoingAnswer createAnswer(final int commandCode, final int applicationId,
                                          final HopByHopId hopByHop, final EndToEndId endToEnd) {
        return switch (commandCode) {
            case DiameterConstants.CMD_ABORT_SESSION ->
                    new AbortSessionAnswer.Out(hopByHop, endToEnd);
            case DiameterConstants.CMD_ACCOUNTING ->
                    new AccountingAnswer.Out(hopByHop, endToEnd);
            case DiameterConstants.CMD_CAPABILITIES_EXCHANGE ->
                    new CapabilitiesExchangeAnswer.Out(hopByHop, endToEnd);
            case DiameterConstants.CMD_DEVICE_WATCHDOG ->
                    new DeviceWatchdogAnswer.Out(hopByHop, endToEnd);
            case DiameterConstants.CMD_DISCONNECT_PEER ->
                    new DisconnectPeerAnswer.Out(hopByHop, endToEnd);
            case DiameterConstants.CMD_RE_AUTH ->
                    new ReAuthAnswer.Out(hopByHop, endToEnd);
            case DiameterConstants.CMD_SESSION_TERMINATION ->
                    new SessionTerminationAnswer.Out(hopByHop, endToEnd);
            default -> null;
        };
    }
}
