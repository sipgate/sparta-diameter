package com.sipgate.sparta.diameter.messages.base;

import com.sipgate.sparta.diameter.core.*;
import com.sipgate.sparta.diameter.core.mixins.OriginStateAware;
import com.sipgate.sparta.diameter.core.DiameterConstants;

/**
 * Device Watchdog Answer (DWA) message.
 * <p>
 * This class represents the Device Watchdog Answer message as defined in
 * <a href="https://datatracker.ietf.org/doc/html/rfc6733#section-5.5.2">RFC 6733, Section 5.5.2</a>.
 * The DWA message is used to confirm the health of the transport connection in response to a DWR message.
 * </p>
 */
public class DeviceWatchdogAnswer extends Answer implements OriginStateAware {

    /**
     * Constructs a Device Watchdog Answer message.
     *
     * @param error              Indicates whether the message is an error response.
     * @param hopByHopIdentifier The hop-by-hop identifier.
     * @param endToEndIdentifier The end-to-end identifier.
     */
    private DeviceWatchdogAnswer(final boolean error, final int hopByHopIdentifier, final int endToEndIdentifier) {
        super(DiameterConstants.CMD_DEVICE_WATCHDOG, false, error, false,
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
        return new DeviceWatchdogAnswer(false, hopByHopIdentifier, endToEndIdentifier);
    }

    /**
     * Creates an error Device Watchdog Answer message.
     *
     * @param hopByHopIdentifier The hop-by-hop identifier.
     * @param endToEndIdentifier The end-to-end identifier.
     * @return A new DeviceWatchdogAnswer instance with error flag set.
     */
    public static DeviceWatchdogAnswer createError(final int hopByHopIdentifier, final int endToEndIdentifier) {
        return new DeviceWatchdogAnswer(true, hopByHopIdentifier, endToEndIdentifier);
    }
}
