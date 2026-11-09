package com.sipgate.sparta.diameter._3gpp.gy;

/**
 * Constants for the Gy/Ro online charging AVPs (3GPP TS 32.299) used inside Credit-Control messages
 * (RFC 4006). All AVPs are 3GPP vendor-specific (vendor id 10415).
 */
public final class GyConstants {

    public static final int AVP_3GPP_CHARGING_ID = 2; // OctetString
    public static final int AVP_3GPP_IMSI_MCC_MNC = 8; // UTF8String
    public static final int AVP_3GPP_SGSN_MCC_MNC = 18; // UTF8String
    public static final int AVP_3GPP_RAT_TYPE = 21; // OctetString
    public static final int AVP_3GPP_USER_LOCATION_INFO = 22; // OctetString
    public static final int AVP_3GPP_MS_TIMEZONE = 23; // OctetString
    public static final int AVP_SERVICE_INFORMATION = 873; // Grouped
    public static final int AVP_PS_INFORMATION = 874; // Grouped
    public static final int AVP_PDP_ADDRESS = 1227; // Address

    public static final int AVP_3GPP_PDP_TYPE = 3; // Enumerated
    public static final int AVP_3GPP_NSAPI = 10; // OctetString
    public static final int AVP_3GPP_SELECTION_MODE = 12; // UTF8String
    public static final int AVP_GGSN_ADDRESS = 847; // Address
    public static final int AVP_REPORTING_REASON = 872; // Enumerated
    public static final int AVP_SGSN_ADDRESS = 1228; // Address
    public static final int AVP_AOC_REQUEST_TYPE = 2055; // Enumerated

    private GyConstants() {}
}
