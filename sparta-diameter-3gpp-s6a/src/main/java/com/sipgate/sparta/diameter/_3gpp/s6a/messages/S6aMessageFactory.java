package com.sipgate.sparta.diameter._3gpp.s6a.messages;

import com.sipgate.sparta.diameter._3gpp.s6a.S6aConstants;
import com.sipgate.sparta.diameter.base.core.*;

/** Message factory for S6a/S6d commands defined in 3GPP TS 29.272. */
public final class S6aMessageFactory implements DiameterPackageFactory {

    @Override
    public IncomingCommand createForParsing(final int commandCode, final int applicationId,
                                            final boolean isRequest,
                                            final HopByHopId hopByHop, final EndToEndId endToEnd,
                                            final boolean retransmitted) {
        return switch (commandCode) {
            case S6aConstants.CMD_UPDATE_LOCATION -> isRequest
                    ? new UpdateLocationRequest.In(hopByHop, endToEnd, retransmitted)
                    : new UpdateLocationAnswer.In(hopByHop, endToEnd);
            case S6aConstants.CMD_CANCEL_LOCATION -> isRequest
                    ? new CancelLocationRequest.In(hopByHop, endToEnd, retransmitted)
                    : new CancelLocationAnswer.In(hopByHop, endToEnd);
            case S6aConstants.CMD_AUTHENTICATION_INFORMATION -> isRequest
                    ? new AuthenticationInformationRequest.In(hopByHop, endToEnd, retransmitted)
                    : new AuthenticationInformationAnswer.In(hopByHop, endToEnd);
            case S6aConstants.CMD_INSERT_SUBSCRIBER_DATA -> isRequest
                    ? new InsertSubscriberDataRequest.In(hopByHop, endToEnd, retransmitted)
                    : new InsertSubscriberDataAnswer.In(hopByHop, endToEnd);
            case S6aConstants.CMD_DELETE_SUBSCRIBER_DATA -> isRequest
                    ? new DeleteSubscriberDataRequest.In(hopByHop, endToEnd, retransmitted)
                    : new DeleteSubscriberDataAnswer.In(hopByHop, endToEnd);
            case S6aConstants.CMD_PURGE_UE -> isRequest
                    ? new PurgeUeRequest.In(hopByHop, endToEnd, retransmitted)
                    : new PurgeUeAnswer.In(hopByHop, endToEnd);
            case S6aConstants.CMD_RESET -> isRequest
                    ? new ResetRequest.In(hopByHop, endToEnd, retransmitted)
                    : new ResetAnswer.In(hopByHop, endToEnd);
            case S6aConstants.CMD_NOTIFY -> isRequest
                    ? new NotifyRequest.In(hopByHop, endToEnd, retransmitted)
                    : new NotifyAnswer.In(hopByHop, endToEnd);
            default -> null;
        };
    }

    @Override
    public OutgoingAnswer createAnswer(final int commandCode, final int applicationId,
                                       final HopByHopId hopByHop, final EndToEndId endToEnd) {
        final var outgoingAnswer = switch (commandCode) {
            case S6aConstants.CMD_UPDATE_LOCATION -> new UpdateLocationAnswer.Out(hopByHop, endToEnd);
            case S6aConstants.CMD_CANCEL_LOCATION -> new CancelLocationAnswer.Out(hopByHop, endToEnd);
            case S6aConstants.CMD_AUTHENTICATION_INFORMATION -> new AuthenticationInformationAnswer.Out(hopByHop, endToEnd);
            case S6aConstants.CMD_INSERT_SUBSCRIBER_DATA -> new InsertSubscriberDataAnswer.Out(hopByHop, endToEnd);
            case S6aConstants.CMD_DELETE_SUBSCRIBER_DATA -> new DeleteSubscriberDataAnswer.Out(hopByHop, endToEnd);
            case S6aConstants.CMD_PURGE_UE -> new PurgeUeAnswer.Out(hopByHop, endToEnd);
            case S6aConstants.CMD_RESET -> new ResetAnswer.Out(hopByHop, endToEnd);
            case S6aConstants.CMD_NOTIFY -> new NotifyAnswer.Out(hopByHop, endToEnd);
            default -> null;
        };

        if (outgoingAnswer != null) {
            /// 3GPP TS 29.272 §7.1.4: S6a/S6d sessions are implicitly terminated;
            /// the server sets Auth-Session-State to NO_STATE_MAINTAINED in every response.
            outgoingAnswer.setAuthSessionState(DiameterConstants.AUTH_SESSION_STATE_NOT_MAINTAINED);
        }

        return outgoingAnswer;
    }
}
