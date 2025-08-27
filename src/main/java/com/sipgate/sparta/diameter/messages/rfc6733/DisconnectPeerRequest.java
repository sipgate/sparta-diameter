package com.sipgate.sparta.diameter.messages.rfc6733;

import com.sipgate.sparta.diameter.core.*;
import com.sipgate.sparta.diameter.core.DiameterConstants;

/**
 * Disconnect Peer Request (DPR) message.
 * <p>
 * This class represents the Disconnect Peer Request message as defined in
 * <a href="https://datatracker.ietf.org/doc/html/rfc6733#section-5.4.1">RFC 6733, Section 5.4.1</a>.
 * The DPR message is used to gracefully disconnect a Diameter peer connection.
 * </p>
 */
public final class DisconnectPeerRequest extends Request {

    /**
     * Constructs a Disconnect Peer Request message.
     *
     * @param retransmitted      Indicates whether the message is retransmitted.
     * @param hopByHopIdentifier The hop-by-hop identifier.
     * @param endToEndIdentifier The end-to-end identifier.
     */
    private DisconnectPeerRequest(final boolean retransmitted, final int hopByHopIdentifier, final int endToEndIdentifier) {
        super(DiameterConstants.CMD_DISCONNECT_PEER, false, retransmitted,
              DiameterConstants.APP_DIAMETER_COMMON_MESSAGES, hopByHopIdentifier, endToEndIdentifier);
    }

    /**
     * Creates a Disconnect Peer Request message.
     *
     * @param hopByHopIdentifier The hop-by-hop identifier.
     * @param endToEndIdentifier The end-to-end identifier.
     * @return A new DisconnectPeerRequest instance.
     */
    public static DisconnectPeerRequest create(final int hopByHopIdentifier, final int endToEndIdentifier) {
        return new DisconnectPeerRequest(false, hopByHopIdentifier, endToEndIdentifier);
    }

    /**
     * Creates a retransmitted Disconnect Peer Request message.
     *
     * @param hopByHopIdentifier The hop-by-hop identifier.
     * @param endToEndIdentifier The end-to-end identifier.
     * @return A new DisconnectPeerRequest instance with retransmitted flag set.
     */
    public static DisconnectPeerRequest createRetransmitted(final int hopByHopIdentifier, final int endToEndIdentifier) {
        return new DisconnectPeerRequest(true, hopByHopIdentifier, endToEndIdentifier);
    }

    @Override
    public DisconnectPeerAnswer createAnswer(final long resultCode) {
        return (DisconnectPeerAnswer) DisconnectPeerAnswer
                .create(getHopByHopIdentifier(), getEndToEndIdentifier())
                .setResultCode(resultCode);
    }
}
