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
public abstract class Answer<T extends Answer<T>> extends Command<T> implements HasResultCodeAVP<T> {

    private final HopByHopId hopByHop;
    private final EndToEndId endToEnd;

    /**
     * Constructs a Diameter answer message.
     *
     * @param commandCode   The command code of the answer.
     * @param proxiable     Indicates whether the message is proxiable.
     * @param applicationId The application ID of the answer.
     */
    protected Answer(final int commandCode, final boolean proxiable, final int applicationId, final HopByHopId hopByHop, final EndToEndId endToEnd) {
        this(commandCode, proxiable, false, applicationId, hopByHop, endToEnd);
    }

    /**
     * Constructs a Diameter answer message with explicit error flag.
     * Use this variant for error answers where the E-bit must be set.
     */
    protected Answer(final int commandCode, final boolean proxiable, final boolean error,
                     final int applicationId, final HopByHopId hopByHop, final EndToEndId endToEnd) {
        super(commandCode, false, proxiable, error, false, applicationId);
        this.hopByHop = hopByHop;
        this.endToEnd = endToEnd;
    }

    public HopByHopId hopByHopId() { return hopByHop; }
    public EndToEndId endToEndId()  { return endToEnd; }
}
