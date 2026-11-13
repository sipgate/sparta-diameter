package com.sipgate.sparta.diameter.base.core.avp;

import com.sipgate.sparta.diameter.base.core.DiameterResultCodeException;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;

/**
 * Thrown when {@link AVP#readFrom} detects a parse-time AVP violation.
 *
 * <p>Covers two generic violations detectable without application-level knowledge:
 * <ul>
 *   <li>{@code DIAMETER_AVP_UNSUPPORTED (5001)} — unrecognized AVP with M-bit set</li>
 *   <li>{@code DIAMETER_INVALID_AVP_LENGTH (5014)} — AVP length field out of range</li>
 * </ul>
 */
public class AVPParseException extends DiameterResultCodeException {

    private final AVP offendingAvp;

    /**
     * @param resultCode   the result code identifying the violation
     * @param offendingAvp the offending AVP (may be a stub if the header was incomplete)
     */
    AVPParseException(final long resultCode, final AVP offendingAvp) {
        this(resultCode, 0, false, 0, new HopByHopId(0), new EndToEndId(0), offendingAvp, null);
    }

    /**
     * @param resultCode    the result code identifying the violation
     * @param commandCode   the command code from the parsed header
     * @param proxiable     whether the P-bit was set in the parsed header
     * @param applicationId the application ID from the parsed header
     * @param hopByHop      the hop-by-hop identifier from the parsed header
     * @param endToEnd      the end-to-end identifier from the parsed header
     * @param offendingAvp  the offending AVP
     * @param sessionId     the session id of the request or null if unknown
     */
    public AVPParseException(final long resultCode, final int commandCode,
            final boolean proxiable, final int applicationId,
            final HopByHopId hopByHop, final EndToEndId endToEnd,
            final AVP offendingAvp, final String sessionId, final Throwable cause) {
        this(resultCode, true, commandCode, proxiable, applicationId, hopByHop, endToEnd, offendingAvp, sessionId, cause);
    }

    /**
     * @param request whether the R-bit was set in the parsed header (an answer is never answered)
     */
    public AVPParseException(final long resultCode, final boolean request, final int commandCode,
            final boolean proxiable, final int applicationId,
            final HopByHopId hopByHop, final EndToEndId endToEnd,
            final AVP offendingAvp, final String sessionId, final Throwable cause) {
        this(resultCode, request, commandCode, proxiable, applicationId, hopByHop, endToEnd, offendingAvp, sessionId,
                java.util.List.of(), cause);
    }

    /**
     * @param request    whether the R-bit was set in the parsed header (an answer is never answered)
     * @param proxyInfos Proxy-Info AVPs of the request parsed before the failure, in order
     *                   (RFC 6733 §6.2); {@code null} means none
     */
    public AVPParseException(final long resultCode, final boolean request, final int commandCode,
            final boolean proxiable, final int applicationId,
            final HopByHopId hopByHop, final EndToEndId endToEnd,
            final AVP offendingAvp, final String sessionId, final java.util.List<AVP> proxyInfos,
            final Throwable cause) {
        this(resultCode, request, commandCode, proxiable, applicationId, hopByHop, endToEnd, offendingAvp, sessionId,
                proxyInfos, null, cause);
    }

    /**
     * @param request          whether the R-bit was set in the parsed header (an answer is never answered)
     * @param proxyInfos       Proxy-Info AVPs of the request parsed before the failure, in order
     *                         (RFC 6733 §6.2); {@code null} means none
     * @param authSessionState Auth-Session-State AVP of the request parsed before the failure,
     *                         mirrored into the answer (RFC 6733 §8.11); {@code null} means none
     */
    public AVPParseException(final long resultCode, final boolean request, final int commandCode,
            final boolean proxiable, final int applicationId,
            final HopByHopId hopByHop, final EndToEndId endToEnd,
            final AVP offendingAvp, final String sessionId, final java.util.List<AVP> proxyInfos,
            final AVP authSessionState, final Throwable cause) {
        super(resultCode, request, commandCode, proxiable, applicationId, hopByHop, endToEnd, sessionId, proxyInfos,
                authSessionState, cause);
        this.offendingAvp = offendingAvp;
    }

    public AVPParseException(final long resultCode, final int commandCode,
            final boolean proxiable, final int applicationId,
            final HopByHopId hopByHop, final EndToEndId endToEnd,
            final AVP offendingAvp, final String sessionId) {
        this(resultCode, commandCode, proxiable, applicationId, hopByHop, endToEnd, offendingAvp, sessionId, null);
    }

    /**
     * Returns the offending AVP that triggered the parse violation.
     * May be a stub with empty data when the AVP header was incomplete.
     *
     * @return the offending AVP
     */
    public AVP getOffendingAvp() {
        return offendingAvp;
    }
}
