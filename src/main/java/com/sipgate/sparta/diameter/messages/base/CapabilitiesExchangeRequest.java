package com.sipgate.sparta.diameter.messages.base;

import com.sipgate.sparta.diameter.core.*;
import com.sipgate.sparta.diameter.messages.base.mixins.CapabilitiesExchange;
import com.sipgate.sparta.diameter.core.mixins.OriginStateAware;
import com.sipgate.sparta.diameter.core.DiameterConstants;

/**
 * Capabilities Exchange Request (CER) message.
 * <p>
 * This class represents the Capabilities Exchange Request message as defined in
 * <a href="https://datatracker.ietf.org/doc/html/rfc6733#section-5.3.1">RFC 6733, Section 5.3.1</a>.
 * The CER message is used to exchange capabilities between Diameter peers during connection establishment.
 * </p>
 */
public class CapabilitiesExchangeRequest extends Request implements CapabilitiesExchange, OriginStateAware {

    /**
     * Constructs a Capabilities Exchange Request message.
     *
     * @param retransmitted      Indicates whether the message is retransmitted.
     * @param hopByHopIdentifier The hop-by-hop identifier.
     * @param endToEndIdentifier The end-to-end identifier.
     */
    public CapabilitiesExchangeRequest(final boolean retransmitted, final int hopByHopIdentifier, final int endToEndIdentifier) {
        super(DiameterConstants.CMD_CAPABILITIES_EXCHANGE, true, retransmitted,
              DiameterConstants.DIAMETER_COMMON_MESSAGES, hopByHopIdentifier, endToEndIdentifier);
    }

    /**
     * Constructs a Capabilities Exchange Request message with default retransmission flag set to false.
     *
     * @param hopByHopIdentifier The hop-by-hop identifier.
     * @param endToEndIdentifier The end-to-end identifier.
     */
    public CapabilitiesExchangeRequest(final int hopByHopIdentifier, final int endToEndIdentifier) {
        this(false, hopByHopIdentifier, endToEndIdentifier);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CapabilitiesExchangeAnswer createAnswer(final int resultCode) {
        final CapabilitiesExchangeAnswer cea = new CapabilitiesExchangeAnswer(
            getHopByHopIdentifier(), getEndToEndIdentifier());
        cea.setResultCode(resultCode);
        return cea;
    }
}
