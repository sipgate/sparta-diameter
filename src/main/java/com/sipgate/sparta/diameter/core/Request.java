package com.sipgate.sparta.diameter.core;

/**
 * Base class for all Diameter request messages.
 * <p>
 * This class represents a Diameter request message as defined in
 * <a href="https://datatracker.ietf.org/doc/html/rfc6733#section-3">RFC 6733, Section 3</a>.
 * Request messages have the R-bit set in the Diameter header flags.
 * </p>
 */
public abstract class Request<T extends Request<T, ANSWER>, ANSWER extends Answer<ANSWER>> extends Command<T> {

    protected Request(final int commandCode, final boolean proxiable, final boolean retransmitted,
                      final int applicationId) {
        super(commandCode, true, proxiable, false, retransmitted, applicationId);
    }
}
