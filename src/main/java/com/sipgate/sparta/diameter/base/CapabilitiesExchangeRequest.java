package com.sipgate.sparta.diameter.base;

/**
 * Capabilities Exchange Request (CER) message.
 * Used to exchange capabilities between Diameter peers during connection establishment.
 */
public class CapabilitiesExchangeRequest extends CapabilitiesExchange {

    public CapabilitiesExchangeRequest(final boolean retransmitted, final int hopByHopIdentifier, final int endToEndIdentifier) {
        super(true, true, false, retransmitted, hopByHopIdentifier, endToEndIdentifier);
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
