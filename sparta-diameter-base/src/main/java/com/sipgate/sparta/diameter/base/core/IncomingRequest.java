package com.sipgate.sparta.diameter.base.core;

/**
 * Base class for all parsed incoming Diameter request messages.
 * <p>
 * Centralises the hop-by-hop and end-to-end identifier fields so that
 * concrete {@code In} classes do not need to redeclare them.
 * </p>
 */
public abstract class IncomingRequest<A extends OutgoingAnswer>
        extends Request<A>
        implements IncomingCommand {

    private final HopByHopId hopByHop;
    private final EndToEndId endToEnd;

    // Transaction state, not message content: the wire-parsed AVPs stay immutable.
    private volatile boolean answeredByRelay;

    protected IncomingRequest(final int commandCode, final boolean proxiable,
                               final boolean retransmitted, final int applicationId,
                               final HopByHopId hopByHop, final EndToEndId endToEnd) {
        super(commandCode, proxiable, retransmitted, applicationId);
        this.hopByHop = hopByHop;
        this.endToEnd = endToEnd;
    }

    @Override public final HopByHopId hopByHopId() { return hopByHop; }
    @Override public final EndToEndId endToEndId()  { return endToEnd; }

    /**
     * Marks this request as answered: the relay path forwarded a downstream answer for it
     * upstream. Called by the session's relay answer path; applications do not call it.
     */
    public final void markAnsweredByRelay() { answeredByRelay = true; }

    /**
     * Whether a relayed answer has been forwarded upstream for this request
     * (see {@link #markAnsweredByRelay()}).
     */
    public final boolean isAnsweredByRelay() { return answeredByRelay; }
}
