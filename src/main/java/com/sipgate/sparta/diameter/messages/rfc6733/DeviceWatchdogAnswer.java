package com.sipgate.sparta.diameter.messages.rfc6733;

import com.sipgate.sparta.diameter.core.*;
import com.sipgate.sparta.diameter.core.annotations.DiameterResponse;
import com.sipgate.sparta.diameter.core.avp.mixins.HasErrorMessageAVP;
import com.sipgate.sparta.diameter.core.avp.mixins.HasFailedAVP;
import com.sipgate.sparta.diameter.core.avp.mixins.HasOriginStateIdAVP;
import com.sipgate.sparta.diameter.core.DiameterConstants;

/**
 * Device Watchdog Answer (DWA) message.
 * <p>
 * This class represents the Device Watchdog Answer message as defined in
 * <a href="https://datatracker.ietf.org/doc/html/rfc6733#section-5.5.2">RFC 6733, Section 5.5.2</a>.
 * The DWA message is used to confirm the health of the transport connection in response to a DWR message.
 * </p>
 */
@DiameterResponse(DiameterConstants.CMD_DEVICE_WATCHDOG)
public class DeviceWatchdogAnswer extends Answer<DeviceWatchdogAnswer> implements
        HasOriginStateIdAVP<DeviceWatchdogAnswer>,
        HasErrorMessageAVP<DeviceWatchdogAnswer>,
        HasFailedAVP<DeviceWatchdogAnswer> {

    /**
     * Constructs a Device Watchdog Answer message.
     *
     * @param hopByHopIdentifier The hop-by-hop identifier.
     * @param endToEndIdentifier The end-to-end identifier.
     */
    private DeviceWatchdogAnswer(final int hopByHopIdentifier, final int endToEndIdentifier) {
        super(DiameterConstants.CMD_DEVICE_WATCHDOG, false, false,
              DiameterConstants.APP_DIAMETER_COMMON_MESSAGES, hopByHopIdentifier, endToEndIdentifier);
    }

    /**
     * Creates a Device Watchdog Answer message.
     *
     * @param hopByHopIdentifier The hop-by-hop identifier.
     * @param endToEndIdentifier The end-to-end identifier.
     * @return A new DeviceWatchdogAnswer instance.
     */
    public static DeviceWatchdogAnswer create(final int hopByHopIdentifier, final int endToEndIdentifier) {
        return new DeviceWatchdogAnswer(hopByHopIdentifier, endToEndIdentifier);
    }
}
