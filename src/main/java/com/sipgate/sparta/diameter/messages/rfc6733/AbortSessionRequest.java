package com.sipgate.sparta.diameter.messages.rfc6733;

import com.sipgate.sparta.diameter.core.*;
import com.sipgate.sparta.diameter.core.DiameterConstants;
import com.sipgate.sparta.diameter.core.avp.mixins.*;

/**
 * Abort Session Request (ASR) message.
 * <p>
 * This class represents the Abort Session Request message as defined in
 * <a href="https://datatracker.ietf.org/doc/html/rfc6733#section-8.5.1">RFC 6733, Section 8.5.1</a>.
 * The ASR message is used to request immediate termination of a user session.
 * </p>
 */
public final class AbortSessionRequest extends Request<AbortSessionRequest, AbortSessionAnswer> implements
        HasSessionIdAVP<AbortSessionRequest>,
        HasAuthApplicationIdAVP<AbortSessionRequest>,
        HasUserNameAVP<AbortSessionRequest>,
        HasOriginStateIdAVP<AbortSessionRequest>,
        HasProxyInfoAVP<AbortSessionRequest>,
        HasRouteRecordAVP<AbortSessionRequest> {

    /**
     * Constructs an Abort Session Request message.
     *
     * @param retransmitted      Indicates whether the message is retransmitted.
     * @param hopByHopIdentifier The hop-by-hop identifier.
     * @param endToEndIdentifier The end-to-end identifier.
     */
    private AbortSessionRequest(final boolean retransmitted, final int hopByHopIdentifier, final int endToEndIdentifier) {
        super(DiameterConstants.CMD_ABORT_SESSION, true, retransmitted,
              DiameterConstants.APP_DIAMETER_COMMON_MESSAGES, hopByHopIdentifier, endToEndIdentifier);
    }

    /**
     * Creates an Abort Session Request message.
     *
     * @param hopByHopIdentifier The hop-by-hop identifier.
     * @param endToEndIdentifier The end-to-end identifier.
     * @return A new AbortSessionRequest instance.
     */
    public static AbortSessionRequest create(final int hopByHopIdentifier, final int endToEndIdentifier) {
        return new AbortSessionRequest(false, hopByHopIdentifier, endToEndIdentifier);
    }

    /**
     * Creates a retransmitted Abort Session Request message.
     *
     * @param hopByHopIdentifier The hop-by-hop identifier.
     * @param endToEndIdentifier The end-to-end identifier.
     * @return A new AbortSessionRequest instance with retransmitted flag set.
     */
    public static AbortSessionRequest createRetransmitted(final int hopByHopIdentifier, final int endToEndIdentifier) {
        return new AbortSessionRequest(true, hopByHopIdentifier, endToEndIdentifier);
    }

    @Override
    public AbortSessionAnswer createAnswer(final long resultCode) {
        return AbortSessionAnswer
                .create(getHopByHopIdentifier(), getEndToEndIdentifier())
                .setResultCode(resultCode);
    }
}
