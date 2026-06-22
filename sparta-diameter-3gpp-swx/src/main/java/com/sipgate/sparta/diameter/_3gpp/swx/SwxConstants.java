package com.sipgate.sparta.diameter._3gpp.swx;

/**
 * Constants for the SWx Diameter interface (3GPP TS 29.273 §8).
 */
public final class SwxConstants {

    /** Diameter application id for the SWx interface (TS 29.273 §8.1.1, IANA-allocated). */
    public static final int APP_ID_SWX = 16777265;

    // Command codes (reused from the IMS Cx/Dx command set, TS 29.229).
    public static final int CMD_SERVER_ASSIGNMENT = 301;
    public static final int CMD_MULTIMEDIA_AUTH = 303;
    public static final int CMD_REGISTRATION_TERMINATION = 304;
    public static final int CMD_PUSH_PROFILE = 305;

    // SWx-specific AVP codes (TS 29.273 Table 8.2.3.0/1). Vendor 3GPP (10415) unless noted.
    public static final int AVP_NON_3GPP_USER_DATA = 1500;
    public static final int AVP_NON_3GPP_IP_ACCESS = 1501;
    public static final int AVP_NON_3GPP_IP_ACCESS_APN = 1502;
    public static final int AVP_AN_TRUSTED = 1503;
    public static final int AVP_ANID = 1504;
    public static final int AVP_TRACE_INFO = 1505;
    public static final int AVP_PPR_FLAGS = 1508;
    public static final int AVP_WLAN_IDENTIFIER = 1509;
    public static final int AVP_TWAN_ACCESS_INFO = 1510;
    public static final int AVP_ACCESS_AUTHORIZATION_FLAGS = 1511;
    public static final int AVP_TWAN_DEFAULT_APN_CONTEXT_ID = 1512;
    public static final int AVP_AAA_FAILURE_INDICATION = 1518;
    public static final int AVP_ACCESS_NETWORK_INFO = 1526;
    public static final int AVP_ERP_AUTHORIZATION = 1541;
    public static final int AVP_3GPP_AAA_SERVER_NAME = 318;

    private SwxConstants() {}
}
