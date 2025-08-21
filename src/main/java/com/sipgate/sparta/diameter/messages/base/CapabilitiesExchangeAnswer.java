package com.sipgate.sparta.diameter.messages.base;

import com.sipgate.sparta.diameter.core.*;
import com.sipgate.sparta.diameter.messages.base.mixins.CapabilitiesExchange;
import com.sipgate.sparta.diameter.core.mixins.OriginStateAware;
import com.sipgate.sparta.diameter.core.DiameterConstants;

/**
 * Capabilities Exchange Answer (CEA) message.
 * Response to a CER message, used to exchange capabilities between Diameter peers.
 */
public class CapabilitiesExchangeAnswer extends Answer implements CapabilitiesExchange, OriginStateAware {

    public CapabilitiesExchangeAnswer(final boolean retransmitted, final int hopByHopIdentifier, final int endToEndIdentifier) {
        super(DiameterConstants.CAPABILITIES_EXCHANGE_REQUEST, true, retransmitted,
              DiameterConstants.DIAMETER_COMMON_MESSAGES, hopByHopIdentifier, endToEndIdentifier);
    }

    // Convenience constructor for backward compatibility (non-retransmitted messages)
    public CapabilitiesExchangeAnswer(final int hopByHopIdentifier, final int endToEndIdentifier) {
        this(false, hopByHopIdentifier, endToEndIdentifier);
    }

    /**
     * Constructor for error responses.
     */
    public CapabilitiesExchangeAnswer(final boolean retransmitted, final int hopByHopIdentifier, final int endToEndIdentifier, final boolean error) {
        super(DiameterConstants.CAPABILITIES_EXCHANGE_REQUEST, true, error, retransmitted,
              DiameterConstants.DIAMETER_COMMON_MESSAGES, hopByHopIdentifier, endToEndIdentifier);
    }

    // Convenience constructor for error responses (backward compatibility)
    public CapabilitiesExchangeAnswer(final int hopByHopIdentifier, final int endToEndIdentifier, final boolean error) {
        this(false, hopByHopIdentifier, endToEndIdentifier, error);
    }

    /**
     * Sets the Error-Message AVP for this answer.
     */
    public void setErrorMessage(final String errorMessage) {
        setAVP(AVP.createStringAVP(DiameterConstants.ERROR_MESSAGE, false, errorMessage));
    }

    /**
     * Gets the Error-Message from this answer.
     */
    public String getErrorMessage() {
        final AVP errorMessageAVP = findAVP(DiameterConstants.ERROR_MESSAGE);
        if (errorMessageAVP != null) {
            return errorMessageAVP.getDataAsString();
        }
        return null;
    }

    /**
     * Sets the Failed-AVP for this answer.
     */
    public void setFailedAVP(final AVP failedAVP) {
        setAVP(new AVP(DiameterConstants.FAILED_AVP, true, failedAVP.getData()));
    }

    /**
     * Gets the Failed-AVP from this answer.
     */
    public AVP getFailedAVP() {
        return findAVP(DiameterConstants.FAILED_AVP);
    }
}
