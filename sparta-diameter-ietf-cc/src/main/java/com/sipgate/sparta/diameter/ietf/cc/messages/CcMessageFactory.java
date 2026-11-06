package com.sipgate.sparta.diameter.ietf.cc.messages;

import com.sipgate.sparta.diameter.base.core.DiameterPackageFactory;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.IncomingCommand;
import com.sipgate.sparta.diameter.base.core.OutgoingAnswer;
import com.sipgate.sparta.diameter.ietf.cc.CcConstants;

public final class CcMessageFactory implements DiameterPackageFactory {

    @Override
    public IncomingCommand createForParsing(final int commandCode, final int applicationId,
                                            final boolean isRequest,
                                            final HopByHopId hopByHop, final EndToEndId endToEnd,
                                            final boolean retransmitted) {
        if (applicationId != CcConstants.APP_ID_CREDIT_CONTROL) {
            return null;
        }
        return switch (commandCode) {
            case CcConstants.CMD_CREDIT_CONTROL -> isRequest
                    ? new CreditControlRequest.In(hopByHop, endToEnd, retransmitted)
                    : new CreditControlAnswer.In(hopByHop, endToEnd);
            default -> null;
        };
    }

    @Override
    public OutgoingAnswer createAnswer(final int commandCode, final int applicationId,
                                       final HopByHopId hopByHop, final EndToEndId endToEnd) {
        if (applicationId != CcConstants.APP_ID_CREDIT_CONTROL) {
            return null;
        }
        return switch (commandCode) {
            case CcConstants.CMD_CREDIT_CONTROL -> {
                final var answer = new CreditControlAnswer.Out(hopByHop, endToEnd);
                /// RFC 4006 §3.2: the CCA ABNF requires Auth-Application-Id, which MUST be 4.
                answer.setAuthApplicationId(CcConstants.APP_ID_CREDIT_CONTROL);
                yield answer;
            }
            default -> null;
        };
    }
}
