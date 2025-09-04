package com.sipgate.sparta.diameter.messages.rfc6733;

import com.sipgate.sparta.diameter.core.*;
import com.sipgate.sparta.diameter.core.annotations.DiameterResponse;
import com.sipgate.sparta.diameter.core.avp.mixins.*;

/**
 * Abort Session Answer (ASA) message.
 * <p>
 * This class represents the Abort Session Answer message as defined in
 * <a href="https://datatracker.ietf.org/doc/html/rfc6733#section-8.5.2">RFC 6733, Section 8.5.2</a>.
 * The ASA message is used to respond to an ASR message for session abort requests.
 * </p>
 */
@DiameterResponse(DiameterConstants.CMD_ABORT_SESSION)
public final class AbortSessionAnswer extends Answer<AbortSessionAnswer> implements
        HasSessionIdAVP<AbortSessionAnswer>,
        HasUserNameAVP<AbortSessionAnswer>,
        HasErrorMessageAVP<AbortSessionAnswer>,
        HasErrorReportingHostAVP<AbortSessionAnswer>,
        HasFailedAVP<AbortSessionAnswer>,
        HasRedirectHostAVP<AbortSessionAnswer>,
        HasRedirectHostUsageAVP<AbortSessionAnswer>,
        HasRedirectMaxCacheTimeAVP<AbortSessionAnswer>,
        HasProxyInfoAVP<AbortSessionAnswer> {

    /**
     * Constructs an Abort Session Answer message.
     *
     * @param hopByHopIdentifier The hop-by-hop identifier.
     * @param endToEndIdentifier The end-to-end identifier.
     */
    private AbortSessionAnswer(final int hopByHopIdentifier, final int endToEndIdentifier) {
        super(DiameterConstants.CMD_ABORT_SESSION, true, false,
              DiameterConstants.APP_DIAMETER_COMMON_MESSAGES, hopByHopIdentifier, endToEndIdentifier);
    }

    /**
     * Creates an Abort Session Answer message.
     *
     * @param hopByHopIdentifier The hop-by-hop identifier.
     * @param endToEndIdentifier The end-to-end identifier.
     * @return A new AbortSessionAnswer instance.
     */
    public static AbortSessionAnswer create(final int hopByHopIdentifier, final int endToEndIdentifier) {
        return new AbortSessionAnswer(hopByHopIdentifier, endToEndIdentifier);
    }
}
