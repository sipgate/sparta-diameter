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
public final class ErrorAnswer extends OutgoingAnswer<ErrorAnswer>
        implements HasSessionIdAVP<ErrorAnswer>, HasOriginStateIdAVP<ErrorAnswer>,
                   HasErrorMessageAVP<ErrorAnswer>, HasErrorReportingHostAVP<ErrorAnswer>,
                   HasFailedAVP<ErrorAnswer>, HasExperimentalResultAVP<ErrorAnswer>,
                   HasProxyInfoAVP<ErrorAnswer> {

    private ErrorAnswer(final int commandCode, final boolean proxiable,
                        final int applicationId, final HopByHopId hopByHop, final EndToEndId endToEnd) {
        super(commandCode, proxiable, true, applicationId, hopByHop, endToEnd);
    }

    public static ErrorAnswer create(final int commandCode, final boolean proxiable,
                                     final int applicationId,
                                     final HopByHopId hopByHop, final EndToEndId endToEnd) {
        return new ErrorAnswer(commandCode, proxiable, applicationId, hopByHop, endToEnd);
    }
}
