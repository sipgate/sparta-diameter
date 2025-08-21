package com.sipgate.sparta.diameter.base;

/**
 * Capabilities Exchange Request (CER) message.
 * Used to exchange capabilities between Diameter peers during connection establishment.
 */
public class CapabilitiesExchangeRequest extends Request implements CapabilitiesExchange, OriginStateAware {

    public CapabilitiesExchangeRequest(final boolean retransmitted, final int hopByHopIdentifier, final int endToEndIdentifier) {
        super(DiameterConstants.CAPABILITIES_EXCHANGE_REQUEST, true, retransmitted,
              DiameterConstants.DIAMETER_COMMON_MESSAGES, hopByHopIdentifier, endToEndIdentifier);
    }

    // Convenience constructor for backward compatibility (non-retransmitted messages)
    public CapabilitiesExchangeRequest(final int hopByHopIdentifier, final int endToEndIdentifier) {
        this(false, hopByHopIdentifier, endToEndIdentifier);
    }

    /**
     * Creates an answer for this request with the same hop-by-hop and end-to-end identifiers.
     */
    public CapabilitiesExchangeAnswer createAnswer(final int resultCode) {
        final CapabilitiesExchangeAnswer cea = new CapabilitiesExchangeAnswer(
            getHopByHopIdentifier(), getEndToEndIdentifier());
        cea.setResultCode(resultCode);
        return cea;
    }
}
