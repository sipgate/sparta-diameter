package com.sipgate.sparta.diameter._3gpp.s6a;

/**
 * Constants for the S6a/S6d Diameter interface (3GPP TS 29.272 / ETSI TS 129 272 v19.5.0).
 */
public final class S6aConstants {

    /** Diameter application id for the S6a/S6d interface (TS 29.272 §7.1.8, IANA-allocated). */
    public static final int APP_ID_S6A_S6D = 16777251;

    // Command codes (TS 29.272 Table 7.2.2/1)
    public static final int CMD_UPDATE_LOCATION = 316;
    public static final int CMD_CANCEL_LOCATION = 317;
    public static final int CMD_AUTHENTICATION_INFORMATION = 318;
    public static final int CMD_INSERT_SUBSCRIBER_DATA = 319;
    public static final int CMD_DELETE_SUBSCRIBER_DATA = 320;
    public static final int CMD_PURGE_UE = 321;
    public static final int CMD_RESET = 322;
    public static final int CMD_NOTIFY = 323;

    // S6a/S6d AVP codes (TS 29.272 §7.3). All vendor-specific (3GPP, 10415).
    public static final int AVP_SUBSCRIPTION_DATA = 1400;
    public static final int AVP_TERMINAL_INFORMATION = 1401;
    public static final int AVP_IMEI = 1402;
    public static final int AVP_SOFTWARE_VERSION = 1403;

    public static final int AVP_ULR_FLAGS = 1405;
    public static final int AVP_ULA_FLAGS = 1406;
    public static final int AVP_VISITED_PLMN_ID = 1407;
    public static final int AVP_REQUESTED_EUTRAN_AUTHENTICATION_INFO = 1408;
    public static final int AVP_REQUESTED_UTRAN_GERAN_AUTHENTICATION_INFO = 1409;
    public static final int AVP_NUMBER_OF_REQUESTED_VECTORS = 1410;
    public static final int AVP_RE_SYNCHRONIZATION_INFO = 1411;
    public static final int AVP_IMMEDIATE_RESPONSE_PREFERRED = 1412;
    public static final int AVP_AUTHENTICATION_INFO = 1413;
    public static final int AVP_E_UTRAN_VECTOR = 1414;
    public static final int AVP_UTRAN_VECTOR = 1415;
    public static final int AVP_NETWORK_ACCESS_MODE = 1417;
    public static final int AVP_ITEM_NUMBER = 1419;
    public static final int AVP_CANCELLATION_TYPE = 1420;
    public static final int AVP_DSR_FLAGS = 1421;
    public static final int AVP_DSA_FLAGS = 1422;
    public static final int AVP_CONTEXT_IDENTIFIER = 1423;
    public static final int AVP_SUBSCRIBER_STATUS = 1424;
    public static final int AVP_APN_OI_REPLACEMENT = 1427;
    public static final int AVP_ALL_APN_CONFIGURATIONS_INCLUDED_INDICATOR = 1428;
    public static final int AVP_APN_CONFIGURATION_PROFILE = 1429;
    public static final int AVP_APN_CONFIGURATION = 1430;
    public static final int AVP_EPS_SUBSCRIBED_QOS_PROFILE = 1431;
    public static final int AVP_VPLMN_DYNAMIC_ADDRESS_ALLOWED = 1432;
    public static final int AVP_ALERT_REASON = 1434;
    public static final int AVP_AMBR = 1435;
    public static final int AVP_PDN_GW_ALLOCATION_TYPE = 1438;
    public static final int AVP_RAT_FREQUENCY_SELECTION_PRIORITY_ID = 1440;
    public static final int AVP_IDA_FLAGS = 1441;
    public static final int AVP_PUA_FLAGS = 1442;
    public static final int AVP_NOR_FLAGS = 1443;
    public static final int AVP_USER_ID = 1444;
    public static final int AVP_RAND = 1447;
    public static final int AVP_XRES = 1448;
    public static final int AVP_AUTN = 1449;
    public static final int AVP_KASME = 1450;
    public static final int AVP_PDN_TYPE = 1456;
    public static final int AVP_TRACE_REFERENCE = 1459;
    public static final int AVP_SS_CODE = 1476;
    public static final int AVP_TS_CODE = 1487;
    public static final int AVP_IDR_FLAGS = 1490;
    public static final int AVP_IMS_VOICE_OVER_PS_SESSIONS_SUPPORTED = 1492;
    public static final int AVP_HOMOGENEOUS_SUPPORT_OF_IMS_VOICE_OVER_PS_SESSIONS = 1493;
    public static final int AVP_LAST_UE_ACTIVITY_TIME = 1494;
    public static final int AVP_EPS_USER_STATE = 1495;
    public static final int AVP_ACTIVE_APN = 1612;
    public static final int AVP_SIPTO_PERMISSION = 1613;
    public static final int AVP_ERROR_DIAGNOSTIC = 1614;
    public static final int AVP_UE_SRVCC_CAPABILITY = 1615;
    public static final int AVP_LIPA_PERMISSION = 1618;
    public static final int AVP_PUR_FLAGS = 1635;
    public static final int AVP_EQUIVALENT_PLMN_LIST = 1637;
    public static final int AVP_CLR_FLAGS = 1638;
    public static final int AVP_LOCAL_TIME_ZONE = 1649;
    public static final int AVP_SMS_REGISTER_REQUEST = 1648;
    public static final int AVP_SGS_MME_IDENTITY = 1664;
    public static final int AVP_COUPLED_NODE_DIAMETER_ID = 1666;
    public static final int AVP_RESET_ID = 1670;
    public static final int AVP_ADJACENT_PLMNS = 1672;
    public static final int AVP_AIR_FLAGS = 1679;
    public static final int AVP_UE_USAGE_TYPE = 1680;
    public static final int AVP_SUBSCRIPTION_DATA_DELETION = 1685;
    public static final int AVP_EDRX_RELATED_RAT = 1705;
    public static final int AVP_SF_ULR_TIMESTAMP = 1729;
    public static final int AVP_SF_PROVISIONAL_INDICATION = 1730;

    /** Supported-Services — 3GPP TS 29.272 §7.3.199. */
    public static final int AVP_SUPPORTED_SERVICES = 3143;
    /** Maximum-UE-Availability-Time — 3GPP TS 29.272 §7.3.198. */
    public static final int AVP_MAXIMUM_UE_AVAILABILITY_TIME = 3329;
    /** Emergency-Services — 3GPP TS 29.272 §7.3.221. */
    public static final int AVP_EMERGENCY_SERVICES = 3370;

    // TS 29.212 (Gx) AVP codes
    public static final int AVP_MAX_REQUESTED_BANDWIDTH_DL = 515;
    public static final int AVP_MAX_REQUESTED_BANDWIDTH_UL = 516;
    public static final int AVP_QOS_CLASS_IDENTIFIER = 1028;
    public static final int AVP_ALLOCATION_RETENTION_PRIORITY = 1034;
    public static final int AVP_PRIORITY_LEVEL = 1046;
    public static final int AVP_PRE_EMPTION_CAPABILITY = 1047;
    public static final int AVP_PRE_EMPTION_VULNERABILITY = 1048;

    public static final int AVP_CHARGING_CHARACTERISTICS_3GPP = 13;

    // Cancellation-Type values (TS 29.272 §7.3.24)
    public static final int CANCELLATION_TYPE_MME_UPDATE_PROCEDURE = 0;
    public static final int CANCELLATION_TYPE_SGSN_UPDATE_PROCEDURE = 1;
    public static final int CANCELLATION_TYPE_SUBSCRIPTION_WITHDRAWAL = 2;
    public static final int CANCELLATION_TYPE_UPDATE_PROCEDURE_IWF = 3;
    public static final int CANCELLATION_TYPE_INITIAL_ATTACH_PROCEDURE = 4;
    public static final int CANCELLATION_TYPE_DISASTER_CONDITION_TERMINATED = 5;

    // Alert-Reason values (TS 29.272 §7.3.83)
    public static final int ALERT_REASON_UE_PRESENT = 0;
    public static final int ALERT_REASON_UE_MEMORY_AVAILABLE = 1;

    // Error-Diagnostic values (TS 29.272 §7.3.128)
    public static final int ERROR_DIAGNOSTIC_GPRS_DATA_SUBSCRIBED = 0;
    public static final int ERROR_DIAGNOSTIC_NO_GPRS_DATA_SUBSCRIBED = 1;
    public static final int ERROR_DIAGNOSTIC_ODB_ALL_APN = 2;
    public static final int ERROR_DIAGNOSTIC_ODB_HPLMN_APN = 3;
    public static final int ERROR_DIAGNOSTIC_ODB_VPLMN_APN = 4;

    // Experimental-Result values (TS 29.272 §7.4). 5001 (USER_UNKNOWN) is in _3gppConstants.
    public static final long EXP_RES_DIAMETER_ERROR_UNKNOWN_EPS_SUBSCRIPTION = 5420L;
    public static final long EXP_RES_DIAMETER_ERROR_RAT_NOT_ALLOWED = 5421L;
    public static final long EXP_RES_DIAMETER_ERROR_EQUIPMENT_UNKNOWN = 5422L;
    public static final long EXP_RES_DIAMETER_ERROR_UNKNOWN_SERVING_NODE = 5423L;
    public static final long EXP_RES_DIAMETER_AUTHENTICATION_DATA_UNAVAILABLE = 4181L;

    private S6aConstants() {}
}
