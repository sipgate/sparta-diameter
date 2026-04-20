package com.sipgate.sparta.diameter.base.messages;

import com.sipgate.sparta.diameter.base.core.*;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasOriginStateIdAVP;

/**
 * Device Watchdog Request (DWR) message.
 * <p>
 * This interface represents the Device Watchdog Request message as defined in
 * <a href="https://datatracker.ietf.org/doc/html/rfc6733#section-5.5.1">RFC 6733, Section 5.5.1</a>.
 * The DWR message is used to monitor the health of the transport connection between Diameter peers.
 * </p>
 */
public interface DeviceWatchdogRequest
        extends HasOriginStateIdAVP {

    final class In extends IncomingRequest<DeviceWatchdogAnswer.Out>
            implements DeviceWatchdogRequest {

        In(final HopByHopId hopByHop, final EndToEndId endToEnd, final boolean retransmitted) {
            super(DiameterConstants.CMD_DEVICE_WATCHDOG, false, retransmitted,
                  DiameterConstants.APP_DIAMETER_COMMON_MESSAGES, hopByHop, endToEnd);
        }
    }

    final class Out extends OutgoingRequest<DeviceWatchdogAnswer.In>
            implements DeviceWatchdogRequest {

        public Out() {
            super(DiameterConstants.CMD_DEVICE_WATCHDOG, false,
                  DiameterConstants.APP_DIAMETER_COMMON_MESSAGES);
        }
    }
}
