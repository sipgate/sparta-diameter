package com.sipgate.sparta.diameter.base;

/**
 * Capabilities Exchange Answer (CEA) message.
 * Response to a CER message, used to exchange capabilities between Diameter peers.
 */
public class CapabilitiesExchangeAnswer extends CapabilitiesExchange {

    public CapabilitiesExchangeAnswer(final boolean retransmitted, final int hopByHopIdentifier, final int endToEndIdentifier) {
        super(false, true, false, retransmitted, hopByHopIdentifier, endToEndIdentifier);
    }

    // Convenience constructor for backward compatibility (non-retransmitted messages)
    public CapabilitiesExchangeAnswer(final int hopByHopIdentifier, final int endToEndIdentifier) {
        this(false, hopByHopIdentifier, endToEndIdentifier);
    }

    /**
     * Constructor for error responses.
     */
    public CapabilitiesExchangeAnswer(final boolean retransmitted, final int hopByHopIdentifier, final int endToEndIdentifier, final boolean error) {
        super(false, true, error, retransmitted, hopByHopIdentifier, endToEndIdentifier);
    }

    /**
     * Gets the Result-Code AVP from this answer.
     * @return the result code, or -1 if not found
     */
    public int getResultCode() {
        final AVP resultCodeAVP = findAVP(DiameterConstants.RESULT_CODE);
        if (resultCodeAVP != null && resultCodeAVP.getData().length >= 4) {
            return resultCodeAVP.getDataAsInt();
        }
        return -1;
    }

    /**
     * Sets the Result-Code AVP for this answer.
     */
    public void setResultCode(final int resultCode) {
        addAVP(AVP.createIntegerAVP(DiameterConstants.RESULT_CODE, true, resultCode));
    }
}
