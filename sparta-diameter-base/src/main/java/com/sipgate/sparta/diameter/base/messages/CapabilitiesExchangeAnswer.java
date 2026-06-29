package com.sipgate.sparta.diameter.base.messages;

import com.sipgate.sparta.diameter.base.core.*;
import com.sipgate.sparta.diameter.base.core.avp.mixins.*;

/**
 * Capabilities Exchange Answer (CEA) message.
 * <p>
 * This interface represents the Capabilities Exchange Answer message as defined in
 * <a href="https://datatracker.ietf.org/doc/html/rfc6733#section-5.3.2">RFC 6733, Section 5.3.2</a>.
 * The CEA message is used to respond to a CER message and exchange capabilities between Diameter peers.
 * </p>
 */
public interface CapabilitiesExchangeAnswer
        extends HasVendorIdAVP, HasProductNameAVP, HasOriginStateIdAVP,
                HasHostIpAddressAVPs, HasSupportedVendorIdAVPs, HasAuthApplicationIdAVPs,
                HasInbandSecurityIdAVPs, HasAcctApplicationIdAVPs,
                HasVendorSpecificApplicationIdAVPs, HasFirmwareRevisionAVP,
                HasErrorMessageAVP, HasFailedAVP {

    final class In extends IncomingAnswer
            implements CapabilitiesExchangeAnswer {

        In(final HopByHopId hopByHop, final EndToEndId endToEnd) {
            super(DiameterConstants.CMD_CAPABILITIES_EXCHANGE, false,
                  DiameterConstants.APP_DIAMETER_COMMON_MESSAGES, hopByHop, endToEnd);
        }

        @Override
        public String getCommandName() {
            return "Capabilities-Exchange Answer";
        }
    }

    final class Out extends OutgoingAnswer
            implements CapabilitiesExchangeAnswer {

        Out(final HopByHopId hopByHop, final EndToEndId endToEnd) {
            super(DiameterConstants.CMD_CAPABILITIES_EXCHANGE, false,
                  DiameterConstants.APP_DIAMETER_COMMON_MESSAGES, hopByHop, endToEnd);
        }

        @Override
        public String getCommandName() {
            return "Capabilities-Exchange Answer";
        }
    }
}
