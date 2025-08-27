package com.sipgate.sparta.diameter.messages.rfc6733;

import com.sipgate.sparta.diameter.core.*;
import com.sipgate.sparta.diameter.core.DiameterConstants;
import com.sipgate.sparta.diameter.core.avp.mixins.*;

/**
 * Re-Auth Answer (RAA) message.
 * <p>
 * This class represents the Re-Auth Answer message as defined in
 * <a href="https://datatracker.ietf.org/doc/html/rfc6733#section-8.3.2">RFC 6733, Section 8.3.2</a>.
 * The RAA message is used to respond to a RAR message for re-authentication requests.
 * </p>
 */
public final class ReAuthAnswer extends Answer implements
        HasUserNameAVP<ReAuthAnswer>,
        HasOriginStateIdAVP<ReAuthAnswer>,
        HasErrorMessageAVP<ReAuthAnswer>,
        HasErrorReportingHostAVP<ReAuthAnswer>,
        HasRedirectHostAVP<ReAuthAnswer>,
        HasRedirectHostUsageAVP<ReAuthAnswer>,
        HasRedirectMaxCacheTimeAVP<ReAuthAnswer>,
        HasProxyInfoAVP<ReAuthAnswer> {

    /**
     * Constructs a Re-Auth Answer message.
     *
     * @param hopByHopIdentifier The hop-by-hop identifier.
     * @param endToEndIdentifier The end-to-end identifier.
     */
    private ReAuthAnswer(final int hopByHopIdentifier, final int endToEndIdentifier) {
        super(DiameterConstants.CMD_RE_AUTH, true, false,
              DiameterConstants.APP_DIAMETER_COMMON_MESSAGES, hopByHopIdentifier, endToEndIdentifier);
    }

    /**
     * Creates a Re-Auth Answer message.
     *
     * @param hopByHopIdentifier The hop-by-hop identifier.
     * @param endToEndIdentifier The end-to-end identifier.
     * @return A new ReAuthAnswer instance.
     */
    public static ReAuthAnswer create(final int hopByHopIdentifier, final int endToEndIdentifier) {
        return new ReAuthAnswer(hopByHopIdentifier, endToEndIdentifier);
    }

    @Override
    public ReAuthAnswer self() {
        return this;
    }

}
