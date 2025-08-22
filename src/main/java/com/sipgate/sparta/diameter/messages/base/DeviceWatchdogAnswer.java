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
     * @param retransmitted      Indicates whether the message is retransmitted.
     * @param hopByHopIdentifier The hop-by-hop identifier.
     * @param endToEndIdentifier The end-to-end identifier.
     */
    public DeviceWatchdogAnswer(final boolean retransmitted, final int hopByHopIdentifier, final int endToEndIdentifier) {
        super(DiameterConstants.CMD_DEVICE_WATCHDOG, true, retransmitted,
              DiameterConstants.APP_DIAMETER_COMMON_MESSAGES, hopByHopIdentifier, endToEndIdentifier);
    }

    /**
     * Constructs a Device Watchdog Answer message with default retransmission flag set to false.
     *
     * @param hopByHopIdentifier The hop-by-hop identifier.
     * @param endToEndIdentifier The end-to-end identifier.
     */
    public DeviceWatchdogAnswer(final int hopByHopIdentifier, final int endToEndIdentifier) {
        this(false, hopByHopIdentifier, endToEndIdentifier);
    }

    /**
     * Constructs a Device Watchdog Answer message for error responses.
     *
     * @param retransmitted      Indicates whether the message is retransmitted.
     * @param hopByHopIdentifier The hop-by-hop identifier.
     * @param endToEndIdentifier The end-to-end identifier.
     * @param error              Indicates whether the message is an error response.
     */
    public DeviceWatchdogAnswer(final boolean retransmitted, final int hopByHopIdentifier, final int endToEndIdentifier, final boolean error) {
        super(DiameterConstants.CMD_DEVICE_WATCHDOG, true, error, retransmitted,
              DiameterConstants.APP_DIAMETER_COMMON_MESSAGES, hopByHopIdentifier, endToEndIdentifier);
    }
}
