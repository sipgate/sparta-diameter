package com.sipgate.sparta.diameter._3gpp.sgdgdd.messages;

import com.sipgate.sparta.diameter._3gpp.sgdgdd.SgdGddConstants;
import com.sipgate.sparta.diameter.base.core.*;

public final class SgdGddMessageFactory implements DiameterPackageFactory {

    @Override
    public IncomingCommand createForParsing(final int commandCode, final int applicationId,
                                            final boolean isRequest,
                                            final HopByHopId hopByHop, final EndToEndId endToEnd,
                                            final boolean retransmitted) {
        if (applicationId != SgdGddConstants.APP_ID_SGD_GDD) {
            return null;
        }
        return switch (commandCode) {
            case SgdGddConstants.CMD_MO_FORWARD_SHORT_MESSAGE -> isRequest
                    ? new MoForwardShortMessageRequest.In(hopByHop, endToEnd, retransmitted)
                    : new MoForwardShortMessageAnswer.In(hopByHop, endToEnd);
            case SgdGddConstants.CMD_MT_FORWARD_SHORT_MESSAGE -> isRequest
                    ? new MtForwardShortMessageRequest.In(hopByHop, endToEnd, retransmitted)
                    : new MtForwardShortMessageAnswer.In(hopByHop, endToEnd);
            default -> null;
        };
    }

    @Override
    public OutgoingAnswer createAnswer(final int commandCode, final int applicationId,
                                          final HopByHopId hopByHop, final EndToEndId endToEnd) {
        if (applicationId != SgdGddConstants.APP_ID_SGD_GDD) {
            return null;
        }
        final var outgoingAnswer = switch (commandCode) {
            case SgdGddConstants.CMD_MO_FORWARD_SHORT_MESSAGE ->
                new MoForwardShortMessageAnswer.Out(hopByHop, endToEnd);
            case SgdGddConstants.CMD_MT_FORWARD_SHORT_MESSAGE ->
                new MtForwardShortMessageAnswer.Out(hopByHop, endToEnd);
            default -> null;
        };

        if (outgoingAnswer != null) {
            /// 3GPP TS 29.338 §4.2:
            /// > Accounting functionality (Accounting Session State Machine, related command codes and AVPs) shall not
            /// > be used on the S6c, SGd and Gdd interfaces.
            ///
            /// 3GPP TS 29.338 §4.5 request implementers to send an Auth-Session-State AVP with the value
            /// `NO_STATE_MAINTAINED` to make that non-usage known (i.e. to diameter proxies/relays).
            outgoingAnswer.setAuthSessionState(DiameterConstants.AUTH_SESSION_STATE_NOT_MAINTAINED);
        }

        return outgoingAnswer;
    }
}
