package com.sipgate.sparta.diameter.base.core;

import com.sipgate.sparta.diameter.base.core.avp.mixins.HasRouteRecordAVPs;

/**
 * A received request whose {@code (command code, application id)} no registered factory
 * handles. It carries the raw AVPs, so it can be answered ({@code DIAMETER_COMMAND_UNSUPPORTED})
 * or relayed like any typed request.
 */
public final class GenericRequest extends IncomingRequest<GenericAnswer.Out> implements HasRouteRecordAVPs {

    public GenericRequest(final int commandCode, final boolean proxiable, final boolean retransmitted,
                          final int applicationId, final HopByHopId hopByHop, final EndToEndId endToEnd) {
        super(commandCode, proxiable, retransmitted, applicationId, hopByHop, endToEnd);
    }

    @Override
    public String getCommandName() {
        return "Unknown Request[code=" + getCommandCode() + "]";
    }
}
