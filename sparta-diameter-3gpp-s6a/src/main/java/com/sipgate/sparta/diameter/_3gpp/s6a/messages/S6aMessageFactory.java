package com.sipgate.sparta.diameter._3gpp.s6a.messages;

import com.sipgate.sparta.diameter._3gpp.s6a.S6aConstants;
import com.sipgate.sparta.diameter.base.core.*;

/**
 * Creates S6a/S6d incoming commands and outgoing answers (3GPP TS 29.272). Auto-discovered by the
 * {@link DiameterMessageFactory} reflections scan.
 */
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
            case S6aConstants.CMD_PURGE_UE -> isRequest
                    ? new PurgeUeRequest.In(hopByHop, endToEnd, retransmitted)
                    : new PurgeUeAnswer.In(hopByHop, endToEnd);
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
            case S6aConstants.CMD_PURGE_UE -> new PurgeUeAnswer.Out(hopByHop, endToEnd);
            case S6aConstants.CMD_NOTIFY -> new NotifyAnswer.Out(hopByHop, endToEnd);
            default -> null;
        };

        if (outgoingAnswer != null) {
            /// 3GPP TS 29.272 §7.1.3: accounting functionality shall not be used on S6a/S6d, and
            /// §7.1.4: the Diameter session is implicitly terminated after each command pair. The
            /// answer therefore carries Auth-Session-State = NO_STATE_MAINTAINED to make that
            /// non-usage known to proxies/relays.
            outgoingAnswer.setAuthSessionState(DiameterConstants.AUTH_SESSION_STATE_NOT_MAINTAINED);
        }

        return outgoingAnswer;
    }
}
