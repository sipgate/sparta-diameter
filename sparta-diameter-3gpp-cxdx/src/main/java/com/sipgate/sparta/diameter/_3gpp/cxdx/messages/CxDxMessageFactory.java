package com.sipgate.sparta.diameter._3gpp.cxdx.messages;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.cxdx.CxDxConstants;
import com.sipgate.sparta.diameter.base.core.*;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import java.util.List;

public final class CxDxMessageFactory implements DiameterPackageFactory {

    @Override
    public IncomingCommand createForParsing(final int commandCode, final int applicationId,
                                            final boolean isRequest,
                                            final HopByHopId hopByHop, final EndToEndId endToEnd,
                                            final boolean retransmitted) {
        if (applicationId != CxDxConstants.APP_ID_CX_DX) {
            return null;
        }
        return switch (commandCode) {
            case CxDxConstants.CMD_SERVER_ASSIGNMENT -> isRequest
                    ? new ServerAssignmentRequest.In(hopByHop, endToEnd, retransmitted)
                    : new ServerAssignmentAnswer.In(hopByHop, endToEnd);
            case CxDxConstants.CMD_MULTIMEDIA_AUTH -> isRequest
                    ? new MultimediaAuthRequest.In(hopByHop, endToEnd, retransmitted)
                    : new MultimediaAuthAnswer.In(hopByHop, endToEnd);
            case CxDxConstants.CMD_REGISTRATION_TERMINATION -> isRequest
                    ? new RegistrationTerminationRequest.In(hopByHop, endToEnd, retransmitted)
                    : new RegistrationTerminationAnswer.In(hopByHop, endToEnd);
            default -> null;
        };
    }

    @Override
    public OutgoingAnswer createAnswer(final int commandCode, final int applicationId,
                                       final HopByHopId hopByHop, final EndToEndId endToEnd) {
        if (applicationId != CxDxConstants.APP_ID_CX_DX) {
            return null;
        }
        final var outgoingAnswer = switch (commandCode) {
            case CxDxConstants.CMD_SERVER_ASSIGNMENT -> new ServerAssignmentAnswer.Out(hopByHop, endToEnd);
            case CxDxConstants.CMD_MULTIMEDIA_AUTH -> new MultimediaAuthAnswer.Out(hopByHop, endToEnd);
            case CxDxConstants.CMD_REGISTRATION_TERMINATION -> new RegistrationTerminationAnswer.Out(hopByHop, endToEnd);
            default -> null;
        };

        if (outgoingAnswer != null) {
            /// 3GPP TS 29.229 §5.2/§5.3: accounting is not used on Cx/Dx and sessions are implicitly
            /// terminated; the client/server include Auth-Session-State = NO_STATE_MAINTAINED.
            outgoingAnswer.setAuthSessionState(DiameterConstants.AUTH_SESSION_STATE_NOT_MAINTAINED);
            // §6.x ABNF marks Vendor-Specific-Application-Id mandatory on every answer.
            outgoingAnswer.setVendorSpecificApplicationId(List.of(
                AVP.create(new AVPKey(DiameterConstants.AVP_VENDOR_ID, 0), (long) _3gppConstants.VENDOR_ID_3GPP),
                AVP.create(new AVPKey(DiameterConstants.AVP_AUTH_APPLICATION_ID, 0), (long) applicationId)));
        }

        return outgoingAnswer;
    }
}
