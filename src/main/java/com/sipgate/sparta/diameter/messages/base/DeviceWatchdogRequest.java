package com.sipgate.sparta.diameter.messages.base;

import com.sipgate.sparta.diameter.core.*;
import com.sipgate.sparta.diameter.core.mixins.OriginStateAware;
import com.sipgate.sparta.diameter.core.DiameterConstants;

/**
 * Device Watchdog Request (DWR) message.
 * Used to monitor the health of the transport connection between Diameter peers.
 */
public class DeviceWatchdogRequest extends Request implements OriginStateAware {

    public DeviceWatchdogRequest(final boolean retransmitted, final int hopByHopIdentifier, final int endToEndIdentifier) {
        super(DiameterConstants.DEVICE_WATCHDOG_REQUEST, false, retransmitted,
              DiameterConstants.DIAMETER_COMMON_MESSAGES, hopByHopIdentifier, endToEndIdentifier);
    }

    // Convenience constructor for backward compatibility (non-retransmitted messages)
    public DeviceWatchdogRequest(final int hopByHopIdentifier, final int endToEndIdentifier) {
        this(false, hopByHopIdentifier, endToEndIdentifier);
    }

    @Override
    public Answer createAnswer(final int resultCode) {
        final DeviceWatchdogAnswer dwa = new DeviceWatchdogAnswer(
            getHopByHopIdentifier(), getEndToEndIdentifier());
        dwa.setResultCode(resultCode);
        return dwa;
    }
}
