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
public interface CapabilitiesExchangeAnswer<T extends CapabilitiesExchangeAnswer<T>>
        extends HasVendorIdAVP<T>, HasProductNameAVP<T>, HasOriginStateIdAVP<T>,
                HasHostIpAddressAVPs<T>, HasSupportedVendorIdAVPs<T>, HasAuthApplicationIdAVPs<T>,
                HasInbandSecurityIdAVPs<T>, HasAcctApplicationIdAVPs<T>,
                HasVendorSpecificApplicationIdAVPs<T>, HasFirmwareRevisionAVP<T>,
                HasErrorMessageAVP<T>, HasFailedAVP<T> {

    final class In extends IncomingAnswer<In>
            implements CapabilitiesExchangeAnswer<In> {

        In(final HopByHopId hopByHop, final EndToEndId endToEnd) {
            super(DiameterConstants.CMD_CAPABILITIES_EXCHANGE, false,
                  DiameterConstants.APP_DIAMETER_COMMON_MESSAGES, hopByHop, endToEnd);
        }
    }

    final class Out extends OutgoingAnswer<Out>
            implements CapabilitiesExchangeAnswer<Out> {

        Out(final HopByHopId hopByHop, final EndToEndId endToEnd) {
            super(DiameterConstants.CMD_CAPABILITIES_EXCHANGE, false,
                  DiameterConstants.APP_DIAMETER_COMMON_MESSAGES, hopByHop, endToEnd);
        }
    }
}
