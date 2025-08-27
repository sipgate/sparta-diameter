package com.sipgate.sparta.diameter.messages.rfc6733;

import com.sipgate.sparta.diameter.core.*;
import com.sipgate.sparta.diameter.core.avp.AVP;
import com.sipgate.sparta.diameter.core.DiameterConstants;
import com.sipgate.sparta.diameter.core.avp.mixins.HasErrorMessageAVP;
import com.sipgate.sparta.diameter.core.avp.mixins.HasFailedAVP;

/**
 * Disconnect Peer Answer (DPA) message.
 * <p>
 * This class represents the Disconnect Peer Answer message as defined in
 * <a href="https://datatracker.ietf.org/doc/html/rfc6733#section-5.4.2">RFC 6733, Section 5.4.2</a>.
 * The DPA message is used to respond to a DPR message when gracefully disconnecting a Diameter peer connection.
 * </p>
 */
public final class DisconnectPeerAnswer extends Answer implements
        HasErrorMessageAVP<DisconnectPeerAnswer>,
        HasFailedAVP<DisconnectPeerAnswer> {

    /**
     * Constructs a Disconnect Peer Answer message.
     *
     * @param hopByHopIdentifier The hop-by-hop identifier.
     * @param endToEndIdentifier The end-to-end identifier.
     */
    private DisconnectPeerAnswer(final int hopByHopIdentifier, final int endToEndIdentifier) {
        super(DiameterConstants.CMD_DISCONNECT_PEER, false, false,
              DiameterConstants.APP_DIAMETER_COMMON_MESSAGES, hopByHopIdentifier, endToEndIdentifier);
    }

    /**
     * Creates a Disconnect Peer Answer message.
     *
     * @param hopByHopIdentifier The hop-by-hop identifier.
     * @param endToEndIdentifier The end-to-end identifier.
     * @return A new DisconnectPeerAnswer instance.
     */
    public static DisconnectPeerAnswer create(final int hopByHopIdentifier, final int endToEndIdentifier) {
        return new DisconnectPeerAnswer(hopByHopIdentifier, endToEndIdentifier);
    }

    @Override
    public DisconnectPeerAnswer self() {
        return this;
    }
}
