package com.sipgate.sparta.diameter.base.core;

import com.sipgate.sparta.diameter.base.DiameterException;
import com.sipgate.sparta.diameter.base.core.avp.AVP;

import java.util.List;

/**
 * Thrown when a Diameter message can be parsed far enough to identify a specific
 * protocol error, and an error answer with the given result code can be sent back.
 *
 * <p>Carries all header fields needed to construct the error answer:
 * command code, proxiable flag, application ID, hop-by-hop identifier, and
 * end-to-end identifier.
 *
 * @see com.sipgate.sparta.diameter.base.core.avp.AVPParseException
 */
public class DiameterResultCodeException extends DiameterException {

    private final long resultCode;
    private final boolean request;
    private final int commandCode;
    private final boolean proxiable;
    private final int applicationId;
    private final HopByHopId hopByHop;
    private final EndToEndId endToEnd;
    private final String sessionId;
    private final List<AVP> proxyInfos;
    private final AVP authSessionState;

    /**
     * Constructs a {@code DiameterResultCodeException} with the given result code and
     * header context required to build the error answer.
     *
     * @param resultCode    the Diameter result code (e.g. {@code RES_DIAMETER_UNSUPPORTED_VERSION})
     * @param commandCode   the command code from the parsed header
     * @param proxiable     whether the P-bit was set in the parsed header
     * @param applicationId the application ID from the parsed header
     * @param hopByHop      the hop-by-hop identifier from the parsed header
     * @param endToEnd      the end-to-end identifier from the parsed header
     * @param sessionId     the session id avp value from the parsed message, if parsed already
     */
    public DiameterResultCodeException(final long resultCode, final int commandCode, final boolean proxiable, final int applicationId, final HopByHopId hopByHop, final EndToEndId endToEnd, final String sessionId) {
        this(resultCode, commandCode, proxiable, applicationId, hopByHop, endToEnd, sessionId, null);
    }

    public DiameterResultCodeException(final long resultCode, final int commandCode, final boolean proxiable, final int applicationId, final HopByHopId hopByHop, final EndToEndId endToEnd, final String sessionId, final Throwable cause) {
        this(resultCode, true, commandCode, proxiable, applicationId, hopByHop, endToEnd, sessionId, cause);
    }

    /**
     * Full constructor.
     *
     * @param request whether the R-bit was set in the parsed header; an answer that fails to
     *                parse is never answered (RFC 6733 §7: errors are reported in answers to requests)
     */
    public DiameterResultCodeException(final long resultCode, final boolean request, final int commandCode, final boolean proxiable, final int applicationId, final HopByHopId hopByHop, final EndToEndId endToEnd, final String sessionId, final Throwable cause) {
        this(resultCode, request, commandCode, proxiable, applicationId, hopByHop, endToEnd, sessionId, List.of(), cause);
    }

    /**
     * Full constructor including the Proxy-Info AVPs parsed before the failure.
     *
     * @param request    whether the R-bit was set in the parsed header
     * @param proxyInfos Proxy-Info AVPs of the request parsed before the failure, in order;
     *                   copied into the answer (RFC 6733 §6.2). {@code null} means none
     */
    public DiameterResultCodeException(final long resultCode, final boolean request, final int commandCode, final boolean proxiable, final int applicationId, final HopByHopId hopByHop, final EndToEndId endToEnd, final String sessionId, final List<AVP> proxyInfos, final Throwable cause) {
        this(resultCode, request, commandCode, proxiable, applicationId, hopByHop, endToEnd, sessionId, proxyInfos, null, cause);
    }

    /**
     * Full constructor including the Proxy-Info and Auth-Session-State AVPs parsed before the failure.
     *
     * @param request          whether the R-bit was set in the parsed header
     * @param proxyInfos       Proxy-Info AVPs of the request parsed before the failure, in order;
     *                         copied into the answer (RFC 6733 §6.2). {@code null} means none
     * @param authSessionState the Auth-Session-State AVP of the request parsed before the failure;
     *                         mirrored into the answer (RFC 6733 §8.11). {@code null} means none
     */
    public DiameterResultCodeException(final long resultCode, final boolean request, final int commandCode, final boolean proxiable, final int applicationId, final HopByHopId hopByHop, final EndToEndId endToEnd, final String sessionId, final List<AVP> proxyInfos, final AVP authSessionState, final Throwable cause) {
        super("Diameter protocol error: result code " + resultCode, cause);
        this.resultCode = resultCode;
        this.request = request;
        this.commandCode = commandCode;
        this.proxiable = proxiable;
        this.applicationId = applicationId;
        this.hopByHop = hopByHop;
        this.endToEnd = endToEnd;
        this.sessionId = sessionId;
        this.proxyInfos = proxyInfos == null ? List.of() : List.copyOf(proxyInfos);
        this.authSessionState = authSessionState;
    }

    /**
     * Returns the Diameter result code identifying the protocol error.
     *
     * @return the result code
     */
    public long getResultCode() {
        return resultCode;
    }

    /**
     * Returns whether the R-bit was set in the parsed message header. Only requests are
     * answered; constructors without a {@code request} parameter default to {@code true}.
     *
     * @return {@code true} if the offending message was a request
     */
    public boolean isRequest() {
        return request;
    }

    /**
     * Returns the command code from the parsed message header.
     *
     * @return the command code
     */
    public int getCommandCode() {
        return commandCode;
    }

    /**
     * Returns whether the P-bit (proxiable) was set in the parsed message header.
     *
     * @return {@code true} if the message was proxiable
     */
    public boolean isProxiable() {
        return proxiable;
    }

    /**
     * Returns the application ID from the parsed message header.
     *
     * @return the application ID
     */
    public int getApplicationId() {
        return applicationId;
    }

    /**
     * Returns the hop-by-hop identifier from the parsed message header.
     *
     * @return the hop-by-hop identifier
     */
    public HopByHopId getHopByHop() {
        return hopByHop;
    }

    /**
     * Returns the end-to-end identifier from the parsed message header.
     *
     * @return the end-to-end identifier
     */
    public EndToEndId getEndToEnd() {
        return endToEnd;
    }

    /**
     * @return the session id avp value from the parsed message, if parsed already or null
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * @return the Proxy-Info AVPs of the request parsed before the failure, in wire order;
     *         empty if none (or none was parsed yet)
     */
    public List<AVP> getProxyInfos() {
        return proxyInfos;
    }

    /**
     * @return the Auth-Session-State AVP of the request parsed before the failure, or
     *         {@code null} if none (or none was parsed yet)
     */
    public AVP getAuthSessionState() {
        return authSessionState;
    }
}
