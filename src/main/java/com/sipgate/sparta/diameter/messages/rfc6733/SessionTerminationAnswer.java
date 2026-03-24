package com.sipgate.sparta.diameter.messages.rfc6733;

import com.sipgate.sparta.diameter.core.*;
import com.sipgate.sparta.diameter.core.annotations.DiameterResponse;
import com.sipgate.sparta.diameter.core.avp.mixins.*;

/**
 * Session Termination Answer (STA) message.
 * <p>
 * This class represents the Session Termination Answer message as defined in
 * <a href="https://datatracker.ietf.org/doc/html/rfc6733#section-8.4.2">RFC 6733, Section 8.4.2</a>.
 * The STA message is used to respond to an STR message for session termination requests.
 * </p>
 */
@DiameterResponse(DiameterConstants.CMD_SESSION_TERMINATION)
public final class SessionTerminationAnswer extends Answer<SessionTerminationAnswer> implements
        HasSessionIdAVP<SessionTerminationAnswer>,
        HasUserNameAVP<SessionTerminationAnswer>,
        HasClassAVP<SessionTerminationAnswer>,
        HasErrorMessageAVP<SessionTerminationAnswer>,
        HasErrorReportingHostAVP<SessionTerminationAnswer>,
        HasFailedAVP<SessionTerminationAnswer>,
        HasOriginStateIdAVP<SessionTerminationAnswer>,
        HasRedirectHostAVP<SessionTerminationAnswer>,
        HasRedirectHostUsageAVP<SessionTerminationAnswer>,
        HasProxyInfoAVP<SessionTerminationAnswer> {

    /**
     * Constructs a Session Termination Answer message.
     *
     * @param hopByHopIdentifier The hop-by-hop identifier.
     * @param endToEndIdentifier The end-to-end identifier.
     */
    private SessionTerminationAnswer(final int hopByHopIdentifier, final int endToEndIdentifier) {
        super(DiameterConstants.CMD_SESSION_TERMINATION, true, false,
              DiameterConstants.APP_DIAMETER_COMMON_MESSAGES, hopByHopIdentifier, endToEndIdentifier);
    }

}
