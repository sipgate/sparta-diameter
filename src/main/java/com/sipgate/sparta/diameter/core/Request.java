package com.sipgate.sparta.diameter.core;

/**
 * Base class for all Diameter request messages.
 * <p>
 * This class represents a Diameter request message as defined in
 * <a href="https://datatracker.ietf.org/doc/html/rfc6733#section-3">RFC 6733, Section 3</a>.
 * Request messages have the R-bit set in the Diameter header flags.
 * </p>
 */
public abstract class Request extends Command {

    /**
     * Constructs a Diameter request message.
     *
     * @param commandCode        The command code of the request.
     * @param proxiable          Indicates whether the message is proxiable.
     * @param retransmitted      Indicates whether the message is retransmitted.
     * @param applicationId      The application ID of the request.
     * @param hopByHopIdentifier The hop-by-hop identifier.
     * @param endToEndIdentifier The end-to-end identifier.
     */
    protected Request(final int commandCode, final boolean proxiable, final boolean retransmitted,
                      final int applicationId, final int hopByHopIdentifier, final int endToEndIdentifier) {
        super(commandCode, true, proxiable, false, retransmitted, applicationId,
              hopByHopIdentifier, endToEndIdentifier);
    }

    /**
     * Creates an answer for this request with the same hop-by-hop and end-to-end identifiers.
     * Subclasses should override this method to return the appropriate answer type.
     *
     * @param resultCode The result code to set in the answer.
     * @return The created answer.
     */
    public abstract Answer createAnswer(long resultCode);
}
