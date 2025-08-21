package com.sipgate.sparta.diameter.base;

/**
 * Device Watchdog Answer (DWA) message.
 * Response to a DWR message, used to confirm the health of the transport connection.
 */
public class DeviceWatchdogAnswer extends Answer {

    public DeviceWatchdogAnswer(final int hopByHopIdentifier, final int endToEndIdentifier) {
        super(DiameterConstants.DEVICE_WATCHDOG_REQUEST, true,
              DiameterConstants.DIAMETER_COMMON_MESSAGES, hopByHopIdentifier, endToEndIdentifier);
    }

    /**
     * Constructor for error responses.
     */
    public DeviceWatchdogAnswer(final int hopByHopIdentifier, final int endToEndIdentifier, final boolean error) {
        super(DiameterConstants.DEVICE_WATCHDOG_REQUEST, true, error,
              DiameterConstants.DIAMETER_COMMON_MESSAGES, hopByHopIdentifier, endToEndIdentifier);
    }
}
