package com.sipgate.sparta.diameter.base.core;

/**
 * Base class for all parsed incoming Diameter request messages.
 * <p>
 * Centralises the hop-by-hop and end-to-end identifier fields so that
 * concrete {@code In} classes do not need to redeclare them.
 * </p>
 */
public abstract class IncomingRequest<T extends IncomingRequest<T, A>, A extends OutgoingAnswer<A>>
        extends Request<T, A>
        implements IncomingCommand {

    private final HopByHopId hopByHop;
    private final EndToEndId endToEnd;

    protected IncomingRequest(final int commandCode, final boolean proxiable,
                               final boolean retransmitted, final int applicationId,
                               final HopByHopId hopByHop, final EndToEndId endToEnd) {
        super(commandCode, proxiable, retransmitted, applicationId);
        this.hopByHop = hopByHop;
        this.endToEnd = endToEnd;
    }

    @Override public final HopByHopId hopByHopId() { return hopByHop; }
    @Override public final EndToEndId endToEndId()  { return endToEnd; }
}
