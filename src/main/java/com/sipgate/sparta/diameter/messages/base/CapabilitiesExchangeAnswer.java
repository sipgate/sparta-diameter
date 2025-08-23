package com.sipgate.sparta.diameter.messages.base;

import com.sipgate.sparta.diameter.core.*;
import com.sipgate.sparta.diameter.core.avp.AVP;
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
     * @param error              Indicates whether the message is an error response.
     * @param hopByHopIdentifier The hop-by-hop identifier.
     * @param endToEndIdentifier The end-to-end identifier.
     */
    private CapabilitiesExchangeAnswer(final boolean error, final int hopByHopIdentifier, final int endToEndIdentifier) {
        super(DiameterConstants.CMD_CAPABILITIES_EXCHANGE, false, error, false,
              DiameterConstants.APP_DIAMETER_COMMON_MESSAGES, hopByHopIdentifier, endToEndIdentifier);
    }

    /**
     * Creates a Capabilities Exchange Answer message.
     *
     * @param hopByHopIdentifier The hop-by-hop identifier.
     * @param endToEndIdentifier The end-to-end identifier.
     * @return A new CapabilitiesExchangeAnswer instance.
     */
    public static CapabilitiesExchangeAnswer create(final int hopByHopIdentifier, final int endToEndIdentifier) {
        return new CapabilitiesExchangeAnswer(false, hopByHopIdentifier, endToEndIdentifier);
    }

    /**
     * Creates an error Capabilities Exchange Answer message.
     *
     * @param hopByHopIdentifier The hop-by-hop identifier.
     * @param endToEndIdentifier The end-to-end identifier.
     * @return A new CapabilitiesExchangeAnswer instance with error flag set.
     */
    public static CapabilitiesExchangeAnswer createError(final int hopByHopIdentifier, final int endToEndIdentifier) {
        return new CapabilitiesExchangeAnswer(true, hopByHopIdentifier, endToEndIdentifier);
    }

    /**
     * Sets the Error-Message AVP for this answer.
     *
     * @param errorMessage The error message to set.
     */
    public void setErrorMessage(final String errorMessage) {
        setAVP(AVP.createStringAVP(DiameterConstants.AVP_ERROR_MESSAGE, false, errorMessage));
    }

    /**
     * Gets the Error-Message from this answer.
     *
     * @return The error message, or null if not set.
     */
    public String getErrorMessage() {
        final AVP errorMessageAVP = findAVP(DiameterConstants.AVP_ERROR_MESSAGE);
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
        setAVP(new AVP(DiameterConstants.AVP_FAILED_AVP, true, failedAVP.getData()));
    }

    /**
     * Gets the Failed-AVP from this answer.
     *
     * @return The Failed-AVP, or null if not set.
     */
    public AVP getFailedAVP() {
        return findAVP(DiameterConstants.AVP_FAILED_AVP);
    }
}
