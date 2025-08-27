package com.sipgate.sparta.diameter.core;

import com.sipgate.sparta.diameter.core.avp.mixins.*;

/**
 * Error Answer message that follows RFC 6733 Section 7.2 format.
 * <p>
 * This class represents a Diameter error answer message as defined in
 * <a href="https://datatracker.ietf.org/doc/html/rfc6733#section-7.2">RFC 6733, Section 7.2</a>.
 * Error messages have the 'E' bit set in the Diameter header flags and follow a specific CCF format.
 * </p>
 */
public final class ErrorAnswer extends Answer implements HasSessionIdAVP<ErrorAnswer>, HasOriginStateIdAVP<ErrorAnswer>, HasErrorMessageAVP<ErrorAnswer>, HasErrorReportingHostAVP<ErrorAnswer>, HasFailedAVP<ErrorAnswer>, HasExperimentalResultAVP<ErrorAnswer>, HasProxyInfoAVP<ErrorAnswer> {

    /**
     * Constructs a Diameter error answer message.
     *
     * @param commandCode        The command code of the answer (same as request).
     * @param proxiable          Indicates whether the message is proxiable (from request).
     * @param applicationId      The application ID of the answer.
     * @param hopByHopIdentifier The hop-by-hop identifier.
     * @param endToEndIdentifier The end-to-end identifier.
     */
    private ErrorAnswer(final int commandCode, final boolean proxiable,
                        final int applicationId, final int hopByHopIdentifier, final int endToEndIdentifier) {
        super(commandCode, proxiable, true, applicationId, hopByHopIdentifier, endToEndIdentifier);
    }

    /**
     * Creates an error answer message.
     *
     * @param commandCode        The command code (same as the request that caused the error).
     * @param proxiable          Indicates whether the message is proxiable (should match request).
     * @param applicationId      The application ID.
     * @param hopByHopIdentifier The hop-by-hop identifier.
     * @param endToEndIdentifier The end-to-end identifier.
     * @return A new ErrorAnswer instance.
     */
    public static ErrorAnswer create(final int commandCode, final boolean proxiable, final int applicationId,
                                   final int hopByHopIdentifier, final int endToEndIdentifier) {
        return new ErrorAnswer(commandCode, proxiable, applicationId, hopByHopIdentifier, endToEndIdentifier);
    }

    @Override
    public ErrorAnswer self() {
        return this;
    }
}
