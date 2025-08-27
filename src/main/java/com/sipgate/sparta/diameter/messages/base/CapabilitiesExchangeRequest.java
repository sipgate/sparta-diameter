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
    private CapabilitiesExchangeRequest(final boolean retransmitted, final int hopByHopIdentifier, final int endToEndIdentifier) {
        super(DiameterConstants.CMD_CAPABILITIES_EXCHANGE, false, retransmitted,
              DiameterConstants.APP_DIAMETER_COMMON_MESSAGES, hopByHopIdentifier, endToEndIdentifier);
    }

    /**
     * Creates a Capabilities Exchange Request message.
     *
     * @param hopByHopIdentifier The hop-by-hop identifier.
     * @param endToEndIdentifier The end-to-end identifier.
     * @return A new CapabilitiesExchangeRequest instance.
     */
    public static CapabilitiesExchangeRequest create(final int hopByHopIdentifier, final int endToEndIdentifier) {
        return new CapabilitiesExchangeRequest(false, hopByHopIdentifier, endToEndIdentifier);
    }

    /**
     * Creates a retransmitted Capabilities Exchange Request message.
     *
     * @param hopByHopIdentifier The hop-by-hop identifier.
     * @param endToEndIdentifier The end-to-end identifier.
     * @return A new CapabilitiesExchangeRequest instance with retransmitted flag set.
     */
    public static CapabilitiesExchangeRequest createRetransmitted(final int hopByHopIdentifier, final int endToEndIdentifier) {
        return new CapabilitiesExchangeRequest(true, hopByHopIdentifier, endToEndIdentifier);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CapabilitiesExchangeAnswer createAnswer(final long resultCode) {
        final CapabilitiesExchangeAnswer cea = ResultCodeUtil.isErrorCode(resultCode)
            ? CapabilitiesExchangeAnswer.createError(getHopByHopIdentifier(), getEndToEndIdentifier())
            : CapabilitiesExchangeAnswer.create(getHopByHopIdentifier(), getEndToEndIdentifier());
        cea.setResultCode(resultCode);
        return cea;
    }
}
