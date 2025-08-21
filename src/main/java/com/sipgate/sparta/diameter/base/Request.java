package com.sipgate.sparta.diameter.base;

/**
 * Base class for all Diameter request messages.
 * Request messages have the R-bit set in the Diameter header flags.
 */
public abstract class Request extends Command {

    protected Request(final int commandCode, final boolean proxiable, final boolean retransmitted,
                      final int applicationId, final int hopByHopIdentifier, final int endToEndIdentifier) {
        super(commandCode, true, proxiable, false, retransmitted, applicationId,
              hopByHopIdentifier, endToEndIdentifier);
    }

    /**
     * Creates an answer for this request with the same hop-by-hop and end-to-end identifiers.
     * Subclasses should override this method to return the appropriate answer type.
     */
    public abstract Answer createAnswer(int resultCode);
}
