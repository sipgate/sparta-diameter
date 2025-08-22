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
    public DeviceWatchdogRequest(final boolean retransmitted, final int hopByHopIdentifier, final int endToEndIdentifier) {
        super(DiameterConstants.DEVICE_WATCHDOG_REQUEST, false, retransmitted,
              DiameterConstants.DIAMETER_COMMON_MESSAGES, hopByHopIdentifier, endToEndIdentifier);
    }

    /**
     * Constructs a Device Watchdog Request message with default retransmission flag set to false.
     *
     * @param hopByHopIdentifier The hop-by-hop identifier.
     * @param endToEndIdentifier The end-to-end identifier.
     */
    public DeviceWatchdogRequest(final int hopByHopIdentifier, final int endToEndIdentifier) {
        this(false, hopByHopIdentifier, endToEndIdentifier);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Answer createAnswer(final int resultCode) {
        final DeviceWatchdogAnswer dwa = new DeviceWatchdogAnswer(
            getHopByHopIdentifier(), getEndToEndIdentifier());
        dwa.setResultCode(resultCode);
        return dwa;
    }
}
