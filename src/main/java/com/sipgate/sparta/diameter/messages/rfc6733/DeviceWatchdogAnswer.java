package com.sipgate.sparta.diameter.messages.rfc6733;

import com.sipgate.sparta.diameter.core.*;
import com.sipgate.sparta.diameter.core.annotations.DiameterResponse;
import com.sipgate.sparta.diameter.core.avp.mixins.HasErrorMessageAVP;
import com.sipgate.sparta.diameter.core.avp.mixins.HasFailedAVP;
import com.sipgate.sparta.diameter.core.avp.mixins.HasOriginStateIdAVP;

/**
 * Device Watchdog Answer (DWA) message.
 * <p>
 * This interface represents the Device Watchdog Answer message as defined in
 * <a href="https://datatracker.ietf.org/doc/html/rfc6733#section-5.5.2">RFC 6733, Section 5.5.2</a>.
 * The DWA message is used to confirm the health of the transport connection in response to a DWR message.
 * </p>
 */
public interface DeviceWatchdogAnswer<T extends DeviceWatchdogAnswer<T>>
        extends HasOriginStateIdAVP<T>, HasErrorMessageAVP<T>, HasFailedAVP<T> {

    @DiameterResponse(DiameterConstants.CMD_DEVICE_WATCHDOG)
    final class In extends IncomingAnswer<In>
            implements DeviceWatchdogAnswer<In> {

        private In(final HopByHopId hopByHop, final EndToEndId endToEnd) {
            super(DiameterConstants.CMD_DEVICE_WATCHDOG, false,
                  DiameterConstants.APP_DIAMETER_COMMON_MESSAGES, hopByHop, endToEnd);
        }
    }

    final class Out extends OutgoingAnswer<Out>
            implements DeviceWatchdogAnswer<Out> {

        private Out(final HopByHopId hopByHop, final EndToEndId endToEnd) {
            super(DiameterConstants.CMD_DEVICE_WATCHDOG, false,
                  DiameterConstants.APP_DIAMETER_COMMON_MESSAGES, hopByHop, endToEnd);
        }
    }
}
