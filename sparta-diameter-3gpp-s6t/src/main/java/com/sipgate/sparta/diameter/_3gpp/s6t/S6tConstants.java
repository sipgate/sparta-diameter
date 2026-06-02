package com.sipgate.sparta.diameter._3gpp.s6t;

/**
 * Constants for the S6t/T6a Diameter interface (3GPP TS 29.336). This module exports only the
 * AVPs that other interfaces (e.g. S6a in TS 29.272) reference; the S6t/T6a command messages
 * themselves are not implemented here.
 */
public final class S6tConstants {

    /** Monitoring-Event-Report — TS 29.336 §8.4.3. */
    public static final int AVP_MONITORING_EVENT_REPORT = 3123;
    /** SCEF-ID — TS 29.336 §8.4.5. */
    public static final int AVP_SCEF_ID = 3125;
    /** Monitoring-Event-Config-Status — TS 29.336 §8.4.21. */
    public static final int AVP_MONITORING_EVENT_CONFIG_STATUS = 3142;

    private S6tConstants() {}
}
