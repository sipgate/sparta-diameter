package com.sipgate.sparta.diameter.messages.rfc6733;

import com.sipgate.sparta.diameter.core.*;
import com.sipgate.sparta.diameter.core.DiameterConstants;

/**
 * Session Termination Request (STR) message.
 * <p>
 * This class represents the Session Termination Request message as defined in
 * <a href="https://datatracker.ietf.org/doc/html/rfc6733#section-8.4.1">RFC 6733, Section 8.4.1</a>.
 * The STR message is used to request termination of a user session.
 * </p>
 */
public final class SessionTerminationRequest extends Request {

    /**
     * Constructs a Session Termination Request message.
     *
     * @param retransmitted      Indicates whether the message is retransmitted.
     * @param hopByHopIdentifier The hop-by-hop identifier.
     * @param endToEndIdentifier The end-to-end identifier.
     */
    private SessionTerminationRequest(final boolean retransmitted, final int hopByHopIdentifier, final int endToEndIdentifier) {
        super(DiameterConstants.CMD_SESSION_TERMINATION, true, retransmitted,
              DiameterConstants.APP_DIAMETER_COMMON_MESSAGES, hopByHopIdentifier, endToEndIdentifier);
    }

    /**
     * Creates a Session Termination Request message.
     *
     * @param hopByHopIdentifier The hop-by-hop identifier.
     * @param endToEndIdentifier The end-to-end identifier.
     * @return A new SessionTerminationRequest instance.
     */
    public static SessionTerminationRequest create(final int hopByHopIdentifier, final int endToEndIdentifier) {
        return new SessionTerminationRequest(false, hopByHopIdentifier, endToEndIdentifier);
    }

    /**
     * Creates a retransmitted Session Termination Request message.
     *
     * @param hopByHopIdentifier The hop-by-hop identifier.
     * @param endToEndIdentifier The end-to-end identifier.
     * @return A new SessionTerminationRequest instance with retransmitted flag set.
     */
    public static SessionTerminationRequest createRetransmitted(final int hopByHopIdentifier, final int endToEndIdentifier) {
        return new SessionTerminationRequest(true, hopByHopIdentifier, endToEndIdentifier);
    }

    @Override
    public SessionTerminationAnswer createAnswer(final long resultCode) {
        return (SessionTerminationAnswer) SessionTerminationAnswer
                .create(getHopByHopIdentifier(), getEndToEndIdentifier())
                .setResultCode(resultCode);
    }
}
