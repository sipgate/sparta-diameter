package com.sipgate.sparta.diameter.messages.rfc6733;

import com.sipgate.sparta.diameter.core.*;
import com.sipgate.sparta.diameter.core.annotations.DiameterRequest;
import com.sipgate.sparta.diameter.core.avp.mixins.*;

/**
 * Re-Auth Request (RAR) message.
 * <p>
 * This class represents the Re-Auth Request message as defined in
 * <a href="https://datatracker.ietf.org/doc/html/rfc6733#section-8.3.1">RFC 6733, Section 8.3.1</a>.
 * The RAR message is used to request re-authentication of a user session.
 * </p>
 */
@DiameterRequest(DiameterConstants.CMD_RE_AUTH)
public final class ReAuthRequest extends Request<ReAuthRequest, ReAuthAnswer> implements
        HasAuthApplicationIdAVP<ReAuthRequest>,
        HasReAuthRequestTypeAVP<ReAuthRequest>,
        HasUserNameAVP<ReAuthRequest>,
        HasOriginStateIdAVP<ReAuthRequest>,
        HasProxyInfoAVP<ReAuthRequest>,
        HasRouteRecordAVP<ReAuthRequest> {

    /**
     * Constructs a Re-Auth Request message.
     *
     * @param retransmitted      Indicates whether the message is retransmitted.
     * @param hopByHopIdentifier The hop-by-hop identifier.
     * @param endToEndIdentifier The end-to-end identifier.
     */
    private ReAuthRequest(final boolean retransmitted, final int hopByHopIdentifier, final int endToEndIdentifier) {
        super(DiameterConstants.CMD_RE_AUTH, true, retransmitted,
              DiameterConstants.APP_DIAMETER_COMMON_MESSAGES, hopByHopIdentifier, endToEndIdentifier);
    }

    /**
     * Creates a Re-Auth Request message.
     *
     * @param hopByHopIdentifier The hop-by-hop identifier.
     * @param endToEndIdentifier The end-to-end identifier.
     * @return A new ReAuthRequest instance.
     */
    public static ReAuthRequest create(final int hopByHopIdentifier, final int endToEndIdentifier) {
        return new ReAuthRequest(false, hopByHopIdentifier, endToEndIdentifier);
    }

    /**
     * Creates a retransmitted Re-Auth Request message.
     *
     * @param hopByHopIdentifier The hop-by-hop identifier.
     * @param endToEndIdentifier The end-to-end identifier.
     * @return A new ReAuthRequest instance with retransmitted flag set.
     */
    public static ReAuthRequest createRetransmitted(final int hopByHopIdentifier, final int endToEndIdentifier) {
        return new ReAuthRequest(true, hopByHopIdentifier, endToEndIdentifier);
    }

    @Override
    public ReAuthAnswer createAnswer(final long resultCode) {
        return ReAuthAnswer
                .create(getHopByHopIdentifier(), getEndToEndIdentifier())
                .setResultCode(resultCode);
    }
}
