package com.sipgate.sparta.diameter.base.core;

public abstract class IncomingAnswer<T extends IncomingAnswer<T>>
        extends Answer<T>
        implements IncomingCommand {

    protected IncomingAnswer(final int commandCode, final boolean proxiable,
                              final int applicationId,
                              final HopByHopId hopByHop, final EndToEndId endToEnd) {
        super(commandCode, proxiable, applicationId, hopByHop, endToEnd);
    }
}
