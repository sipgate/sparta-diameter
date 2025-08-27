package com.sipgate.sparta.diameter.messages.base;

import com.sipgate.sparta.diameter.core.*;
import com.sipgate.sparta.diameter.core.mixins.OriginStateAware;
import com.sipgate.sparta.diameter.core.DiameterConstants;

/**
 * Device Watchdog Request (DWR) message.
 * <p>
 * This class represents the Device Watchdog Request message as defined in
 * <a href="https://datatracker.ietf.org/doc/html/rfc6733#section-5.5.1">RFC 6733, Section 5.5.1</a>.
 * The DWR message is used to monitor the health of the transport connection between Diameter peers.
 * </p>
 */
public class DeviceWatchdogRequest extends Request implements OriginStateAware {

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

    /**
     * Creates a Device Watchdog Request message.
     *
     * @param hopByHopIdentifier The hop-by-hop identifier.
     * @param endToEndIdentifier The end-to-end identifier.
     * @return A new DeviceWatchdogRequest instance.
     */
    public static DeviceWatchdogRequest create(final int hopByHopIdentifier, final int endToEndIdentifier) {
        return new DeviceWatchdogRequest(false, hopByHopIdentifier, endToEndIdentifier);
    }

    /**
     * Creates a retransmitted Device Watchdog Request message.
     *
     * @param hopByHopIdentifier The hop-by-hop identifier.
     * @param endToEndIdentifier The end-to-end identifier.
     * @return A new DeviceWatchdogRequest instance with retransmitted flag set.
     */
    public static DeviceWatchdogRequest createRetransmitted(final int hopByHopIdentifier, final int endToEndIdentifier) {
        return new DeviceWatchdogRequest(true, hopByHopIdentifier, endToEndIdentifier);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Answer createAnswer(final long resultCode) {
        final DeviceWatchdogAnswer dwa = ResultCodeUtil.isErrorCode(resultCode)
            ? DeviceWatchdogAnswer.createError(getHopByHopIdentifier(), getEndToEndIdentifier())
            : DeviceWatchdogAnswer.create(getHopByHopIdentifier(), getEndToEndIdentifier());
        dwa.setResultCode(resultCode);
        return dwa;
    }
}
