package com.sipgate.sparta.diameter.messages.base;

import com.sipgate.sparta.diameter.core.*;
import com.sipgate.sparta.diameter.messages.base.mixins.CapabilitiesExchange;
import com.sipgate.sparta.diameter.core.mixins.OriginStateAware;
import com.sipgate.sparta.diameter.core.DiameterConstants;

/**
 * Capabilities Exchange Answer (CEA) message.
 * <p>
 * This class represents the Capabilities Exchange Answer message as defined in
 * <a href="https://datatracker.ietf.org/doc/html/rfc6733#section-5.3.2">RFC 6733, Section 5.3.2</a>.
 * The CEA message is used to respond to a CER message and exchange capabilities between Diameter peers.
 * </p>
 */
public class CapabilitiesExchangeAnswer extends Answer implements CapabilitiesExchange, OriginStateAware {

    /**
     * Constructs a Capabilities Exchange Answer message.
     *
     * @param retransmitted      Indicates whether the message is retransmitted.
     * @param hopByHopIdentifier The hop-by-hop identifier.
     * @param endToEndIdentifier The end-to-end identifier.
     */
    public CapabilitiesExchangeAnswer(final boolean retransmitted, final int hopByHopIdentifier, final int endToEndIdentifier) {
        super(DiameterConstants.CAPABILITIES_EXCHANGE_REQUEST, true, retransmitted,
              DiameterConstants.DIAMETER_COMMON_MESSAGES, hopByHopIdentifier, endToEndIdentifier);
    }

    /**
     * Constructs a Capabilities Exchange Answer message with default retransmission flag set to false.
     *
     * @param hopByHopIdentifier The hop-by-hop identifier.
     * @param endToEndIdentifier The end-to-end identifier.
     */
    public CapabilitiesExchangeAnswer(final int hopByHopIdentifier, final int endToEndIdentifier) {
        this(false, hopByHopIdentifier, endToEndIdentifier);
    }

    /**
     * Constructs a Capabilities Exchange Answer message for error responses.
     *
     * @param retransmitted      Indicates whether the message is retransmitted.
     * @param hopByHopIdentifier The hop-by-hop identifier.
     * @param endToEndIdentifier The end-to-end identifier.
     * @param error              Indicates whether the message is an error response.
     */
    public CapabilitiesExchangeAnswer(final boolean retransmitted, final int hopByHopIdentifier, final int endToEndIdentifier, final boolean error) {
        super(DiameterConstants.CAPABILITIES_EXCHANGE_REQUEST, true, error, retransmitted,
              DiameterConstants.DIAMETER_COMMON_MESSAGES, hopByHopIdentifier, endToEndIdentifier);
    }

    /**
     * Sets the Error-Message AVP for this answer.
     *
     * @param errorMessage The error message to set.
     */
    public void setErrorMessage(final String errorMessage) {
        setAVP(AVP.createStringAVP(DiameterConstants.ERROR_MESSAGE, false, errorMessage));
    }

    /**
     * Gets the Error-Message from this answer.
     *
     * @return The error message, or null if not set.
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
     *
     * @param failedAVP The Failed-AVP to set.
     */
    public void setFailedAVP(final AVP failedAVP) {
        setAVP(new AVP(DiameterConstants.FAILED_AVP, true, failedAVP.getData()));
    }

    /**
     * Gets the Failed-AVP from this answer.
     *
     * @return The Failed-AVP, or null if not set.
     */
    public AVP getFailedAVP() {
        return findAVP(DiameterConstants.FAILED_AVP);
    }
}
