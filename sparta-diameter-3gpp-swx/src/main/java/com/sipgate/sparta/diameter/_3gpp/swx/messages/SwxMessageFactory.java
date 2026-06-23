package com.sipgate.sparta.diameter._3gpp.swx.messages;

import com.sipgate.sparta.diameter._3gpp.swx.SwxConstants;
import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.DiameterPackageFactory;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.IncomingCommand;
import com.sipgate.sparta.diameter.base.core.OutgoingAnswer;

public final class SwxMessageFactory implements DiameterPackageFactory {

    @Override
    public IncomingCommand createForParsing(final int commandCode, final int applicationId,
                                            final boolean isRequest,
                                            final HopByHopId hopByHop, final EndToEndId endToEnd,
                                            final boolean retransmitted) {
        if (applicationId != SwxConstants.APP_ID_SWX) {
            return null;
        }
        return switch (commandCode) {
            case SwxConstants.CMD_MULTIMEDIA_AUTH -> isRequest
                    ? new MultimediaAuthRequest.In(hopByHop, endToEnd, retransmitted)
                    : new MultimediaAuthAnswer.In(hopByHop, endToEnd);
            case SwxConstants.CMD_SERVER_ASSIGNMENT -> isRequest
                    ? new ServerAssignmentRequest.In(hopByHop, endToEnd, retransmitted)
                    : new ServerAssignmentAnswer.In(hopByHop, endToEnd);
            case SwxConstants.CMD_REGISTRATION_TERMINATION -> isRequest
                    ? new RegistrationTerminationRequest.In(hopByHop, endToEnd, retransmitted)
                    : new RegistrationTerminationAnswer.In(hopByHop, endToEnd);
            case SwxConstants.CMD_PUSH_PROFILE -> isRequest
                    ? new PushProfileRequest.In(hopByHop, endToEnd, retransmitted)
                    : new PushProfileAnswer.In(hopByHop, endToEnd);
            default -> null;
        };
    }

    @Override
    public OutgoingAnswer createAnswer(final int commandCode, final int applicationId,
                                       final HopByHopId hopByHop, final EndToEndId endToEnd) {
        if (applicationId != SwxConstants.APP_ID_SWX) {
            return null;
        }
        final var outgoingAnswer = switch (commandCode) {
            case SwxConstants.CMD_MULTIMEDIA_AUTH -> new MultimediaAuthAnswer.Out(hopByHop, endToEnd);
            case SwxConstants.CMD_SERVER_ASSIGNMENT -> new ServerAssignmentAnswer.Out(hopByHop, endToEnd);
            case SwxConstants.CMD_REGISTRATION_TERMINATION -> new RegistrationTerminationAnswer.Out(hopByHop, endToEnd);
            case SwxConstants.CMD_PUSH_PROFILE -> new PushProfileAnswer.Out(hopByHop, endToEnd);
            default -> null;
        };
        if (outgoingAnswer != null) {
            // TS 29.273 §8 reuses Cx/Dx semantics: sessions are not state-maintained.
            outgoingAnswer.setAuthSessionState(DiameterConstants.AUTH_SESSION_STATE_NOT_MAINTAINED);
        }
        return outgoingAnswer;
    }
}
