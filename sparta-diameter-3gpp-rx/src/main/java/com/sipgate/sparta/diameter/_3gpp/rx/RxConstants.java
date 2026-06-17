package com.sipgate.sparta.diameter._3gpp.rx;

/**
 * Constants for the Rx Diameter interface (3GPP TS 29.214). Limited to AVPs that other
 * interfaces (e.g. S6a in TS 29.272) reference; no command messages.
 */
public final class RxConstants {

    public static final int AVP_MAX_REQUESTED_BANDWIDTH_DL = 515;
    public static final int AVP_MAX_REQUESTED_BANDWIDTH_UL = 516;

    private RxConstants() {}
}
