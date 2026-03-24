package com.sipgate.sparta.diameter.messages.rfc6733;

import com.sipgate.sparta.diameter.core.*;
import com.sipgate.sparta.diameter.core.annotations.DiameterRequest;
import com.sipgate.sparta.diameter.core.avp.mixins.HasOriginStateIdAVP;

/**
 * Device Watchdog Request (DWR) message.
 * <p>
 * This class represents the Device Watchdog Request message as defined in
 * <a href="https://datatracker.ietf.org/doc/html/rfc6733#section-5.5.1">RFC 6733, Section 5.5.1</a>.
 * The DWR message is used to monitor the health of the transport connection between Diameter peers.
 * </p>
 */
@DiameterRequest(DiameterConstants.CMD_DEVICE_WATCHDOG)
public class DeviceWatchdogRequest extends Request<DeviceWatchdogRequest, DeviceWatchdogAnswer> implements HasOriginStateIdAVP<DeviceWatchdogRequest> {

    /**
     * Constructs a Device Watchdog Request message.
     *
     * @param retransmitted      Indicates whether the message is retransmitted.
     * @param hopByHopIdentifier The hop-by-hop identifier.
     * @param endToEndIdentifier The end-to-end identifier.
     */
    private DeviceWatchdogRequest(final boolean retransmitted, final int hopByHopIdentifier, final int endToEndIdentifier) {
        super(DiameterConstants.CMD_DEVICE_WATCHDOG, false, retransmitted,
              DiameterConstants.APP_DIAMETER_COMMON_MESSAGES, hopByHopIdentifier, endToEndIdentifier);
    }

}
