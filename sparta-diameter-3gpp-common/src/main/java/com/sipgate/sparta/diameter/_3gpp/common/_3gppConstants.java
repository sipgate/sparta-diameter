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

    // 3GPP TS 29.212 §5.3.31 — shared access AVP
    public static final int AVP_RAT_TYPE = 1032;
    // 3GPP TS 29.272 §7.3 — shared subscription AVPs
    public static final int AVP_ACTIVE_APN = 1612;
    public static final int AVP_LOCAL_TIME_ZONE = 1649;
    // 3GPP TS 29.273 — shared access AVP
    public static final int AVP_EMERGENCY_SERVICES = 3370;

    // 3GPP TS 29.173, SLh interface
    public static final int AVP_SERVING_NODE = 2401;

    // 3GPP TS 29.338, S6c interface
    public static final int AVP_SM_DELIVERY_OUTCOME = 3316;
    public static final int AVP_ABSENT_USER_DIAGNOSTIC_SM = 3322;
    public static final int AVP_SMS_GMSC_ALERT_EVENT = 3333;

    // Mandatory AVPs from 3GPP interface specifications (Cx/Dx, S6a/S6d/S7a/S7d/S13, S6c, S6m/S6n)
    public static final int AVP_DIGEST_REALM = 104;
    public static final int AVP_DIGEST_QOP = 110;
    public static final int AVP_DIGEST_ALGORITHM = 111;
    public static final int AVP_DIGEST_HA1 = 121;
    public static final int AVP_VISITED_NETWORK_IDENTIFIER = 600;
    public static final int AVP_PUBLIC_IDENTITY = 601;
    public static final int AVP_SERVER_NAME = 602;
    public static final int AVP_SERVER_CAPABILITIES = 603;
    public static final int AVP_MANDATORY_CAPABILITY = 604;
    public static final int AVP_OPTIONAL_CAPABILITY = 605;
    public static final int AVP_USER_DATA = 606;
    public static final int AVP_SIP_NUMBER_AUTH_ITEMS = 607;
    public static final int AVP_SIP_AUTHENTICATION_SCHEME = 608;
    public static final int AVP_SIP_AUTHENTICATE = 609;
    public static final int AVP_SIP_AUTHORIZATION = 610;
    public static final int AVP_SIP_AUTHENTICATION_CONTEXT = 611;
    public static final int AVP_SIP_AUTH_DATA_ITEM = 612;
    public static final int AVP_SIP_ITEM_NUMBER = 613;
    public static final int AVP_SERVER_ASSIGNMENT_TYPE = 614;
    public static final int AVP_DEREGISTRATION_REASON = 615;
    public static final int AVP_REASON_CODE = 616;
    public static final int AVP_REASON_INFO = 617;
    public static final int AVP_CHARGING_INFORMATION = 618;
    public static final int AVP_PRIMARY_EVENT_CHARGING_FUNCTION_NAME = 619;
    public static final int AVP_SECONDARY_EVENT_CHARGING_FUNCTION_NAME = 620;
    public static final int AVP_PRIMARY_CHARGING_COLLECTION_FUNCTION_NAME = 621;
    public static final int AVP_SECONDARY_CHARGING_COLLECTION_FUNCTION_NAME = 622;
    public static final int AVP_USER_AUTHORIZATION_TYPE = 623;
    public static final int AVP_USER_DATA_ALREADY_AVAILABLE = 624;
    public static final int AVP_CONFIDENTIALITY_KEY = 625;
    public static final int AVP_INTEGRITY_KEY = 626;
    public static final int AVP_ORIGINATING_REQUEST = 633;
    public static final int AVP_SUBSCRIPTION_DATA = 1400;
    public static final int AVP_TERMINAL_INFORMATION = 1401;
    public static final int AVP_IMEI = 1402;
    public static final int AVP_SOFTWARE_VERSION = 1403;
    public static final int AVP_QOS_SUBSCRIBED = 1404;
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
    public static final int AVP_GERAN_VECTOR = 1416;
    public static final int AVP_NETWORK_ACCESS_MODE = 1417;
    public static final int AVP_HPLMN_ODB = 1418;
    public static final int AVP_ITEM_NUMBER = 1419;
    public static final int AVP_CANCELLATION_TYPE = 1420;
    public static final int AVP_DSR_FLAGS = 1421;
    public static final int AVP_DSA_FLAGS = 1422;
    public static final int AVP_CONTEXT_IDENTIFIER = 1423;
    public static final int AVP_SUBSCRIBER_STATUS = 1424;
    public static final int AVP_OPERATOR_DETERMINED_BARRING = 1425;
    public static final int AVP_ACCESS_RESTRICTION_DATA = 1426;
    public static final int AVP_APN_OI_REPLACEMENT = 1427;
    public static final int AVP_ALL_APN_CONFIGURATIONS_INCLUDED_INDICATOR = 1428;
    public static final int AVP_APN_CONFIGURATION_PROFILE = 1429;
    public static final int AVP_APN_CONFIGURATION = 1430;
    public static final int AVP_EPS_SUBSCRIBED_QOS_PROFILE = 1431;
    public static final int AVP_VPLMN_DYNAMIC_ADDRESS_ALLOWED = 1432;
    public static final int AVP_STN_SR = 1433;
    public static final int AVP_ALERT_REASON = 1434;
    public static final int AVP_AMBR = 1435;
    public static final int AVP_CSG_SUBSCRIPTION_DATA = 1436;
    public static final int AVP_CSG_ID = 1437;
    public static final int AVP_PDN_GW_ALLOCATION_TYPE = 1438;
    public static final int AVP_EXPIRATION_DATE = 1439;
    public static final int AVP_RAT_FREQUENCY_SELECTION_PRIORITY_ID = 1440;
    public static final int AVP_IDA_FLAGS = 1441;
    public static final int AVP_PUA_FLAGS = 1442;
    public static final int AVP_NOR_FLAGS = 1443;
    public static final int AVP_EQUIPMENT_STATUS = 1445;
    public static final int AVP_REGIONAL_SUBSCRIPTION_ZONE_CODE = 1446;
    public static final int AVP_RAND = 1447;
    public static final int AVP_XRES = 1448;
    public static final int AVP_AUTN = 1449;
    public static final int AVP_KASME = 1450;
    public static final int AVP_TRACE_COLLECTION_ENTITY = 1452;
    public static final int AVP_KC = 1453;
    public static final int AVP_SRES = 1454;
    public static final int AVP_PDN_TYPE = 1456;
    public static final int AVP_ROAMING_RESTRICTED_DUE_TO_UNSUPPORTED_FEATURE = 1457;
    public static final int AVP_TRACE_DATA = 1458;
    public static final int AVP_TRACE_REFERENCE = 1459;
    public static final int AVP_TRACE_DEPTH = 1462;
    public static final int AVP_TRACE_NE_TYPE_LIST = 1463;
    public static final int AVP_TRACE_INTERFACE_LIST = 1464;
    public static final int AVP_TRACE_EVENT_LIST = 1465;
    public static final int AVP_OMC_ID = 1466;
    public static final int AVP_GPRS_SUBSCRIPTION_DATA = 1467;
    public static final int AVP_COMPLETE_DATA_LIST_INCLUDED_INDICATOR = 1468;
    public static final int AVP_PDP_CONTEXT = 1469;
    public static final int AVP_PDP_TYPE = 1470;
    public static final int AVP_3GPP2_MEID = 1471;
    public static final int AVP_SPECIFIC_APN_INFO = 1472;
    public static final int AVP_LCS_INFO = 1473;
    public static final int AVP_GMLC_NUMBER = 1474;
    public static final int AVP_LCS_PRIVACYEXCEPTION = 1475;
    public static final int AVP_SS_CODE = 1476;
    public static final int AVP_SS_STATUS = 1477;
    public static final int AVP_NOTIFICATION_TO_UE_USER = 1478;
    public static final int AVP_EXTERNAL_CLIENT = 1479;
    public static final int AVP_CLIENT_IDENTITY = 1480;
    public static final int AVP_GMLC_RESTRICTION = 1481;
    public static final int AVP_PLMN_CLIENT = 1482;
    public static final int AVP_SERVICE_TYPE = 1483;
    public static final int AVP_SERVICETYPEIDENTITY = 1484;
    public static final int AVP_MO_LR = 1485;
    public static final int AVP_TELESERVICE_LIST = 1486;
    public static final int AVP_TS_CODE = 1487;
    public static final int AVP_CALL_BARRING_INFO = 1488;
    public static final int AVP_IDR_FLAGS = 1490;
    public static final int AVP_UVR_FLAGS = 1639;
    public static final int AVP_UVA_FLAGS = 1640;
    public static final int AVP_VPLMN_CSG_SUBSCRIPTION_DATA = 1641;
    public static final int AVP_IP_SM_GW_NUMBER = 3100;
    public static final int AVP_IP_SM_GW_NAME = 3101;
    public static final int AVP_SERVICE_ID = 3103;
    public static final int AVP_SCS_IDENTITY = 3104;
    public static final int AVP_SERVICE_PARAMETERS = 3105;
    public static final int AVP_T4_PARAMETERS = 3106;
    public static final int AVP_SERVICE_DATA = 3107;
    public static final int AVP_T4_DATA = 3108;
    public static final int AVP_HSS_CAUSE = 3109;
    public static final int AVP_SIR_FLAGS = 3110;
    public static final int AVP_IP_SM_GW_REALM = 3112;
    public static final int AVP_SM_RP_MTI = 3308;
    public static final int AVP_SM_RP_SMEA = 3309;
    public static final int AVP_SRR_FLAGS = 3310;
    public static final int AVP_SM_DELIVERY_NOT_INTENDED = 3311;
    public static final int AVP_MWD_STATUS = 3312;
    public static final int AVP_MME_ABSENT_USER_DIAGNOSTIC_SM = 3313;
    public static final int AVP_MSC_ABSENT_USER_DIAGNOSTIC_SM = 3314;
    public static final int AVP_SGSN_ABSENT_USER_DIAGNOSTIC_SM = 3315;
    public static final int AVP_MME_SM_DELIVERY_OUTCOME = 3317;
    public static final int AVP_MSC_SM_DELIVERY_OUTCOME = 3318;
    public static final int AVP_SGSN_SM_DELIVERY_OUTCOME = 3319;
    public static final int AVP_IP_SM_GW_SM_DELIVERY_OUTCOME = 3320;
    public static final int AVP_SM_DELIVERY_CAUSE = 3321;

    // Experimental Result value shared across multiple 3GPP interfaces (e.g. Cx/Dx, S6c, SGd/Gdd)
    // Carried in Experimental-Result AVP; distinct from RFC 6733 Result-Code 5001 (AVP_UNSUPPORTED)
    public static final long EXP_RES_DIAMETER_ERROR_USER_UNKNOWN = 5001L;
    public static final long EXP_RES_DIAMETER_ERROR_IDENTITIES_DONT_MATCH = 5002L;

    private _3gppConstants() {}
}
