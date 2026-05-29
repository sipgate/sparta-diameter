package com.sipgate.sparta.diameter._3gpp.common;

/**
 * Constants for common 3GPP Diameter interfaces.
 */
public final class _3gppConstants {

    public static final int VENDOR_ID_3GPP = 10415;

    // 3GPP TS 29.229, Cx and Dx interfaces
    public static final int AVP_SUPPORTED_FEATURES = 628;
    public static final int AVP_FEATURE_LIST_ID = 629;
    public static final int AVP_FEATURE_LIST = 630;

    // 3GPP TS 29.329, Sh interface
    public static final int AVP_MSISDN = 701;

    // 3GPP TS 29.336, S6m/S6n interfaces
    public static final int AVP_USER_IDENTIFIER = 3102;
    public static final int AVP_EXTERNAL_IDENTIFIER = 3111;

    // 3GPP TS 29.272, S6a/S6d, S7a/S7d and S13/S13 interfaces
    public static final int AVP_EPS_LOCATION_INFORMATION = 1496;
    public static final int AVP_MME_NUMBER_FOR_MT_SMS = 1645;
    public static final int AVP_SGSN_NUMBER = 1489;

    // 3GPP TS 29.173, SLh interface
    public static final int AVP_SERVING_NODE = 2401;

    // 3GPP TS 29.212, Gx interface (QoS AVPs reused by S6a/S6d Subscription-Data / EPS-Subscribed-QoS-Profile)
    public static final int AVP_QOS_CLASS_IDENTIFIER = 1028;          // §5.3.17
    public static final int AVP_RAT_TYPE = 1032;                      // §5.3.31
    public static final int AVP_ALLOCATION_RETENTION_PRIORITY = 1034; // §5.3.32
    public static final int AVP_PRIORITY_LEVEL = 1046;               // §5.3.45
    public static final int AVP_PRE_EMPTION_CAPABILITY = 1047;       // §5.3.46
    public static final int AVP_PRE_EMPTION_VULNERABILITY = 1048;    // §5.3.47

    // 3GPP TS 29.214, Rx interface (bandwidth AVPs reused by S6a/S6d AMBR)
    public static final int AVP_MAX_REQUESTED_BANDWIDTH_DL = 515;    // §5.3.14
    public static final int AVP_MAX_REQUESTED_BANDWIDTH_UL = 516;    // §5.3.15

    // 3GPP TS 29.061, Gi/Sgi interface
    public static final int AVP_3GPP_CHARGING_CHARACTERISTICS = 13;  // §16.4.7

    // 3GPP TS 29.229, Cx/Dx interface (also reused by S6a/S6d UTRAN-Vector)
    public static final int AVP_CONFIDENTIALITY_KEY = 625;           // §6.3.27
    public static final int AVP_INTEGRITY_KEY = 626;                 // §6.3.28

    // 3GPP TS 29.338, S6c interface
    public static final int AVP_SM_DELIVERY_OUTCOME = 3316;
    public static final int AVP_ABSENT_USER_DIAGNOSTIC_SM = 3322;
    public static final int AVP_SMS_GMSC_ALERT_EVENT = 3333;

    // Experimental Result value shared across multiple 3GPP interfaces (e.g. Cx/Dx, S6c, SGd/Gdd)
    // Carried in Experimental-Result AVP; distinct from RFC 6733 Result-Code 5001 (AVP_UNSUPPORTED)
    public static final long EXP_RES_DIAMETER_ERROR_USER_UNKNOWN = 5001L;

    private _3gppConstants() {}
}
