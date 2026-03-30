package com.sipgate.sparta.diameter._3gpp.sgdgdd.messages;

import com.sipgate.sparta.diameter._3gpp.sgdgdd.SgdGddConstants;
import com.sipgate.sparta.diameter.base.core.DiameterPackageFactory;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.IncomingCommand;
import com.sipgate.sparta.diameter.base.core.OutgoingAnswer;

public final class SgdGddMessageFactory implements DiameterPackageFactory {

    @Override
    public IncomingCommand createForParsing(final int commandCode, final int applicationId,
                                            final boolean isRequest,
                                            final HopByHopId hopByHop, final EndToEndId endToEnd,
                                            final boolean retransmitted) {
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
    public OutgoingAnswer<?> createAnswer(final int commandCode, final int applicationId,
                                          final HopByHopId hopByHop, final EndToEndId endToEnd) {
        return switch (commandCode) {
            case SgdGddConstants.CMD_MO_FORWARD_SHORT_MESSAGE ->
                    new MoForwardShortMessageAnswer.Out(hopByHop, endToEnd);
            case SgdGddConstants.CMD_MT_FORWARD_SHORT_MESSAGE ->
                    new MtForwardShortMessageAnswer.Out(hopByHop, endToEnd);
            default -> null;
        };
    }
}
