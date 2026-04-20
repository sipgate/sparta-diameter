package com.sipgate.sparta.diameter.base.core;

public abstract class IncomingAnswer
        extends Answer
        implements IncomingCommand {

    protected IncomingAnswer(final int commandCode, final boolean proxiable,
                              final int applicationId,
                              final HopByHopId hopByHop, final EndToEndId endToEnd) {
        super(commandCode, proxiable, applicationId, hopByHop, endToEnd);
    }

    protected IncomingAnswer(final int commandCode, final boolean proxiable, final boolean error,
                              final int applicationId,
                              final HopByHopId hopByHop, final EndToEndId endToEnd) {
        super(commandCode, proxiable, error, applicationId, hopByHop, endToEnd);
    }
}
