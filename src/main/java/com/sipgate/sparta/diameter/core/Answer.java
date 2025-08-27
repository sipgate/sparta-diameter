package com.sipgate.sparta.diameter.core;

import com.sipgate.sparta.diameter.core.avp.mixins.HasResultCodeAVP;

/**
 * Base class for all Diameter answer messages.
 * <p>
 * This class represents a Diameter answer message as defined in
 * <a href="https://datatracker.ietf.org/doc/html/rfc6733#section-3">RFC 6733, Section 3</a>.
 * Answer messages have the R-bit cleared in the Diameter header flags.
 * </p>
 */
public abstract class Answer extends Command implements HasResultCodeAVP<Answer> {

    /**
     * Constructs a Diameter answer message.
     *
     * @param commandCode        The command code of the answer.
     * @param proxiable          Indicates whether the message is proxiable.
     * @param retransmitted      Indicates whether the message is retransmitted.
     * @param applicationId      The application ID of the answer.
     * @param hopByHopIdentifier The hop-by-hop identifier.
     * @param endToEndIdentifier The end-to-end identifier.
     */
    protected Answer(final int commandCode, final boolean proxiable, final boolean retransmitted,
                     final int applicationId, final int hopByHopIdentifier, final int endToEndIdentifier) {
        super(commandCode, false, proxiable, false, retransmitted, applicationId,
              hopByHopIdentifier, endToEndIdentifier);
    }

    @Override
    public Answer self() {
        return this;
    }
}
