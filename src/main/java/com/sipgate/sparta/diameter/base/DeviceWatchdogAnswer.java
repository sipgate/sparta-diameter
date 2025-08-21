package com.sipgate.sparta.diameter.base;

/**
 * Device Watchdog Answer (DWA) message.
 * Response to a DWR message, used to confirm the health of the transport connection.
 */
public class DeviceWatchdogAnswer extends Answer implements OriginStateAware {

    public DeviceWatchdogAnswer(final boolean retransmitted, final int hopByHopIdentifier, final int endToEndIdentifier) {
        super(DiameterConstants.DEVICE_WATCHDOG_REQUEST, true, retransmitted,
              DiameterConstants.DIAMETER_COMMON_MESSAGES, hopByHopIdentifier, endToEndIdentifier);
    }

    // Convenience constructor for backward compatibility (non-retransmitted messages)
    public DeviceWatchdogAnswer(final int hopByHopIdentifier, final int endToEndIdentifier) {
        this(false, hopByHopIdentifier, endToEndIdentifier);
    }

    /**
     * Constructor for error responses.
     */
    public DeviceWatchdogAnswer(final boolean retransmitted, final int hopByHopIdentifier, final int endToEndIdentifier, final boolean error) {
        super(DiameterConstants.DEVICE_WATCHDOG_REQUEST, true, error, retransmitted,
              DiameterConstants.DIAMETER_COMMON_MESSAGES, hopByHopIdentifier, endToEndIdentifier);
    }
}
