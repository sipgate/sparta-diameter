package com.sipgate.sparta.diameter.base.messages;

import com.sipgate.sparta.diameter.base.core.*;
import com.sipgate.sparta.diameter.base.core.avp.mixins.*;

/**
 * Capabilities Exchange Request (CER) message.
 * <p>
 * This interface represents the Capabilities Exchange Request message as defined in
 * <a href="https://datatracker.ietf.org/doc/html/rfc6733#section-5.3.1">RFC 6733, Section 5.3.1</a>.
 * The CER message is used to exchange capabilities between Diameter peers during connection establishment.
 * </p>
 */
public interface CapabilitiesExchangeRequest<T extends CapabilitiesExchangeRequest<T>>
        extends HasVendorIdAVP<T>, HasProductNameAVP<T>, HasHostIpAddressAVP<T>,
                HasSupportedVendorIdAVP<T>, HasAuthApplicationIdAVP<T>,
                HasAcctApplicationIdAVP<T>, HasFirmwareRevisionAVP<T> {

    final class In extends IncomingRequest<In, CapabilitiesExchangeAnswer.Out>
            implements CapabilitiesExchangeRequest<In> {

        In(final HopByHopId hopByHop, final EndToEndId endToEnd, final boolean retransmitted) {
            super(DiameterConstants.CMD_CAPABILITIES_EXCHANGE, false, retransmitted,
                  DiameterConstants.APP_DIAMETER_COMMON_MESSAGES, hopByHop, endToEnd);
        }
    }

    final class Out extends OutgoingRequest<Out, CapabilitiesExchangeAnswer.In>
            implements CapabilitiesExchangeRequest<Out> {

        public Out() {
            super(DiameterConstants.CMD_CAPABILITIES_EXCHANGE, false,
                  DiameterConstants.APP_DIAMETER_COMMON_MESSAGES);
        }
    }
}
