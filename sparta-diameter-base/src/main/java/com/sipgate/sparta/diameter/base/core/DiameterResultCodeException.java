package com.sipgate.sparta.diameter.base.core;

import com.sipgate.sparta.diameter.base.DiameterException;

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
    private final int commandCode;
    private final boolean proxiable;
    private final int applicationId;
    private final HopByHopId hopByHop;
    private final EndToEndId endToEnd;
    private final String sessionId;

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
        super("Diameter protocol error: result code " + resultCode, cause);
        this.resultCode = resultCode;
        this.commandCode = commandCode;
        this.proxiable = proxiable;
        this.applicationId = applicationId;
        this.hopByHop = hopByHop;
        this.endToEnd = endToEnd;
        this.sessionId = sessionId;
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
}
