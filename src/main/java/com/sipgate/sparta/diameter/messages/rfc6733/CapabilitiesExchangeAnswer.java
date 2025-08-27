package com.sipgate.sparta.diameter.messages.rfc6733;

import com.sipgate.sparta.diameter.core.Answer;
import com.sipgate.sparta.diameter.core.DiameterConstants;
import com.sipgate.sparta.diameter.core.avp.mixins.*;

/**
 * Capabilities Exchange Answer (CEA) message.
 * <p>
 * This class represents the Capabilities Exchange Answer message as defined in
 * <a href="https://datatracker.ietf.org/doc/html/rfc6733#section-5.3.2">RFC 6733, Section 5.3.2</a>.
 * The CEA message is used to respond to a CER message and exchange capabilities between Diameter peers.
 * </p>
 */
public final class CapabilitiesExchangeAnswer extends Answer implements HasVendorIdAVP<CapabilitiesExchangeAnswer>,
        HasProductNameAVP<CapabilitiesExchangeAnswer>,
        HasSupportedVendorIdAVP<CapabilitiesExchangeAnswer>,
        HasAuthApplicationIdAVP<CapabilitiesExchangeAnswer>,
        HasAcctApplicationIdAVP<CapabilitiesExchangeAnswer>,
        HasFirmwareRevisionAVP<CapabilitiesExchangeAnswer>,
        HasErrorMessageAVP<CapabilitiesExchangeAnswer>,
        HasFailedAVP<CapabilitiesExchangeAnswer> {

    public CapabilitiesExchangeAnswer(final int commandCode, final boolean proxiable, final boolean retransmitted,
                                      final int applicationId, final int hopByHopIdentifier, final int endToEndIdentifier) {
        super(commandCode, proxiable, retransmitted, applicationId, hopByHopIdentifier, endToEndIdentifier);
    }

    /**
     * Creates a Capabilities Exchange Answer message.
     *
     * @param hopByHopIdentifier The hop-by-hop identifier.
     * @param endToEndIdentifier The end-to-end identifier.
     * @return A new CapabilitiesExchangeAnswer instance.
     */
    public static CapabilitiesExchangeAnswer create(final int hopByHopIdentifier, final int endToEndIdentifier) {
        return new CapabilitiesExchangeAnswer(DiameterConstants.CMD_CAPABILITIES_EXCHANGE, false, false, DiameterConstants.APP_DIAMETER_COMMON_MESSAGES, hopByHopIdentifier, endToEndIdentifier);
    }

    @Override
    public CapabilitiesExchangeAnswer self() {
        return this;
    }
}
