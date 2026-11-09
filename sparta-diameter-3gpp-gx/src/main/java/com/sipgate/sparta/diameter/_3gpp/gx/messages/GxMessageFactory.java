package com.sipgate.sparta.diameter._3gpp.gx.messages;

import com.sipgate.sparta.diameter._3gpp.gx.GxConstants;
import com.sipgate.sparta.diameter.base.core.DiameterPackageFactory;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.IncomingCommand;
import com.sipgate.sparta.diameter.base.core.OutgoingAnswer;
import com.sipgate.sparta.diameter.ietf.cc.CcConstants;

/**
 * Parses and answers the Gx Credit-Control command (code 272, application 16777238). RAR and RAA are left to the base
 * factory, which handles them for every application.
 */
public final class GxMessageFactory implements DiameterPackageFactory {

    @Override
    public IncomingCommand createForParsing(final int commandCode, final int applicationId,
                                            final boolean isRequest,
                                            final HopByHopId hopByHop, final EndToEndId endToEnd,
                                            final boolean retransmitted) {
        if (applicationId != GxConstants.APP_ID_GX) {
            return null;
        }
        return switch (commandCode) {
            case CcConstants.CMD_CREDIT_CONTROL -> isRequest
                    ? new GxCreditControlRequest.In(hopByHop, endToEnd, retransmitted)
                    : new GxCreditControlAnswer.In(hopByHop, endToEnd);
            default -> null;
        };
    }

    @Override
    public OutgoingAnswer createAnswer(final int commandCode, final int applicationId,
                                       final HopByHopId hopByHop, final EndToEndId endToEnd) {
        if (applicationId != GxConstants.APP_ID_GX) {
            return null;
        }
        return switch (commandCode) {
            case CcConstants.CMD_CREDIT_CONTROL -> {
                final var answer = new GxCreditControlAnswer.Out(hopByHop, endToEnd);
                /// TS 29.212 §5.6.3: the CCA ABNF requires Auth-Application-Id, which is the Gx application id.
                answer.setAuthApplicationId(GxConstants.APP_ID_GX);
                yield answer;
            }
            default -> null;
        };
    }
}
