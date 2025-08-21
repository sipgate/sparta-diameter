package com.sipgate.sparta.diameter.base;

/**
 * Device Watchdog Request (DWR) message.
 * Used to monitor the health of the transport connection between Diameter peers.
 */
public class DeviceWatchdogRequest extends Request {

    public DeviceWatchdogRequest(final int hopByHopIdentifier, final int endToEndIdentifier) {
        super(DiameterConstants.DEVICE_WATCHDOG_REQUEST, false,
              DiameterConstants.DIAMETER_COMMON_MESSAGES, hopByHopIdentifier, endToEndIdentifier);
    }

    @Override
    public Answer createAnswer(final int resultCode) {
        final DeviceWatchdogAnswer dwa = new DeviceWatchdogAnswer(
            getHopByHopIdentifier(), getEndToEndIdentifier());
        dwa.setResultCode(resultCode);
        return dwa;
    }
}
