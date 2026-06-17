package com.sipgate.sparta.diameter._3gpp.gx;

/**
 * Constants for the Gx Diameter interface (3GPP TS 29.212). Limited to AVPs that other
 * interfaces (e.g. S6a in TS 29.272) reference; no command messages.
 */
public final class GxConstants {

    public static final int AVP_RAT_TYPE = 1032;
    public static final int AVP_ACCESS_NETWORK_CHARGING_IDENTIFIER_GX = 1022;
    public static final int AVP_BEARER_CONTROL_MODE = 1023;
    public static final int AVP_BEARER_IDENTIFIER = 1020;
    public static final int AVP_BEARER_OPERATION = 1021;
    public static final int AVP_BEARER_USAGE = 1000;
    public static final int AVP_CHARGING_RULE_BASE_NAME = 1004;
    public static final int AVP_CHARGING_RULE_DEFINITION = 1003;
    public static final int AVP_CHARGING_RULE_INSTALL = 1001;
    public static final int AVP_CHARGING_RULE_NAME = 1005;
    public static final int AVP_CHARGING_RULE_REMOVE = 1002;
    public static final int AVP_CHARGING_RULE_REPORT = 1018;
    public static final int AVP_EVENT_TRIGGER = 1006;
    public static final int AVP_GUARANTEED_BITRATE_DL = 1025;
    public static final int AVP_GUARANTEED_BITRATE_UL = 1026;
    public static final int AVP_IP_CAN_TYPE = 1027;
    public static final int AVP_METERING_METHOD = 1007;
    public static final int AVP_NETWORK_REQUEST_SUPPORT = 1024;
    public static final int AVP_OFFLINE = 1008;
    public static final int AVP_ONLINE = 1009;
    public static final int AVP_PCC_RULE_STATUS = 1019;
    public static final int AVP_PRECEDENCE = 1010;
    public static final int AVP_QOS_CLASS_IDENTIFIER = 1028;
    public static final int AVP_QOS_INFORMATION = 1016;
    public static final int AVP_QOS_NEGOTIATION = 1029;
    public static final int AVP_QOS_UPGRADE = 1030;
    public static final int AVP_REPORTING_LEVEL = 1011;
    public static final int AVP_REVALIDATION_TIME = 1042;
    public static final int AVP_RULE_ACTIVATION_TIME = 1043;
    public static final int AVP_RULE_DEACTIVATION_TIME = 1044;
    public static final int AVP_RULE_FAILURE_CODE = 1031;
    public static final int AVP_SESSION_RELEASE_CAUSE = 1045;
    public static final int AVP_TFT_FILTER = 1012;
    public static final int AVP_TFT_PACKET_FILTER_INFORMATION = 1013;
    public static final int AVP_TOS_TRAFFIC_CLASS = 1014;

    private GxConstants() {}
}
