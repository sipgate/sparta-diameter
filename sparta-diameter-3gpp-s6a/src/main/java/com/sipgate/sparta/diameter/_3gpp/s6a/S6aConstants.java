package com.sipgate.sparta.diameter._3gpp.s6a;

/**
 * Constants for the S6a/S6d Diameter interface (3GPP TS 29.272).
 */
public final class S6aConstants {

    // 3GPP TS 29.272, S6a/S6d interface (§7.1.8)
    public static final int APP_ID_S6A_S6D = 16777251;

    // Command codes (3GPP TS 29.272 Table 7.2.2/1)
    public static final int CMD_UPDATE_LOCATION = 316;
    public static final int CMD_CANCEL_LOCATION = 317;
    public static final int CMD_AUTHENTICATION_INFORMATION = 318;
    public static final int CMD_INSERT_SUBSCRIBER_DATA = 319;
    public static final int CMD_PURGE_UE = 321;
    public static final int CMD_NOTIFY = 323;

    // S6a/S6d-specific AVPs (3GPP TS 29.272 Table 7.3.1/1) — in-scope subset (HSS usage)
    public static final int AVP_SUBSCRIPTION_DATA = 1400;                              // §7.3.2 Grouped
    public static final int AVP_TERMINAL_INFORMATION = 1401;                           // §7.3.3 Grouped
    public static final int AVP_IMEI = 1402;                                           // §7.3.4 UTF8String
    public static final int AVP_SOFTWARE_VERSION = 1403;                               // §7.3.5 UTF8String
    public static final int AVP_ULR_FLAGS = 1405;                                      // §7.3.7 Unsigned32
    public static final int AVP_ULA_FLAGS = 1406;                                      // §7.3.8 Unsigned32
    public static final int AVP_VISITED_PLMN_ID = 1407;                                // §7.3.9 OctetString
    public static final int AVP_REQUESTED_EUTRAN_AUTHENTICATION_INFO = 1408;           // §7.3.11 Grouped
    public static final int AVP_REQUESTED_UTRAN_GERAN_AUTHENTICATION_INFO = 1409;      // §7.3.12 Grouped
    public static final int AVP_NUMBER_OF_REQUESTED_VECTORS = 1410;                    // §7.3.14 Unsigned32
    public static final int AVP_RE_SYNCHRONIZATION_INFO = 1411;                        // §7.3.15 OctetString
    public static final int AVP_IMMEDIATE_RESPONSE_PREFERRED = 1412;                   // §7.3.16 Unsigned32
    public static final int AVP_AUTHENTICATION_INFO = 1413;                            // §7.3.17 Grouped
    public static final int AVP_E_UTRAN_VECTOR = 1414;                                 // §7.3.18 Grouped
    public static final int AVP_UTRAN_VECTOR = 1415;                                   // §7.3.19 Grouped
    public static final int AVP_GERAN_VECTOR = 1416;                                   // §7.3.20 Grouped
    public static final int AVP_NETWORK_ACCESS_MODE = 1417;                            // §7.3.21 Enumerated
    public static final int AVP_ITEM_NUMBER = 1419;                                    // §7.3.23 Unsigned32
    public static final int AVP_CANCELLATION_TYPE = 1420;                              // §7.3.24 Enumerated
    public static final int AVP_CONTEXT_IDENTIFIER = 1423;                             // §7.3.27 Unsigned32
    public static final int AVP_SUBSCRIBER_STATUS = 1424;                              // §7.3.29 Enumerated
    public static final int AVP_APN_OI_REPLACEMENT = 1427;                             // §7.3.32 UTF8String
    public static final int AVP_ALL_APN_CONFIGURATIONS_INCLUDED_INDICATOR = 1428;      // §7.3.33 Enumerated
    public static final int AVP_APN_CONFIGURATION_PROFILE = 1429;                      // §7.3.34 Grouped
    public static final int AVP_APN_CONFIGURATION = 1430;                              // §7.3.35 Grouped
    public static final int AVP_EPS_SUBSCRIBED_QOS_PROFILE = 1431;                     // §7.3.37 Grouped
    public static final int AVP_VPLMN_DYNAMIC_ADDRESS_ALLOWED = 1432;                  // §7.3.38 Enumerated
    public static final int AVP_ALERT_REASON = 1434;                                   // §7.3.83 Enumerated
    public static final int AVP_AMBR = 1435;                                           // §7.3.41 Grouped
    public static final int AVP_PDN_GW_ALLOCATION_TYPE = 1438;                         // §7.3.44 Enumerated
    public static final int AVP_RAT_FREQUENCY_SELECTION_PRIORITY_ID = 1440;            // §7.3.46 Unsigned32
    public static final int AVP_IDA_FLAGS = 1441;                                      // §7.3.47 Unsigned32
    public static final int AVP_PUA_FLAGS = 1442;                                      // §7.3.48 Unsigned32
    public static final int AVP_NOR_FLAGS = 1443;                                      // §7.3.49 Unsigned32
    public static final int AVP_RAND = 1447;                                           // §7.3.53 OctetString
    public static final int AVP_XRES = 1448;                                           // §7.3.54 OctetString
    public static final int AVP_AUTN = 1449;                                           // §7.3.55 OctetString
    public static final int AVP_KASME = 1450;                                          // §7.3.56 OctetString
    public static final int AVP_KC = 1453;                                             // §7.3.59 OctetString
    public static final int AVP_SRES = 1454;                                           // §7.3.60 OctetString
    public static final int AVP_PDN_TYPE = 1456;                                       // §7.3.62 Enumerated
    public static final int AVP_IDR_FLAGS = 1490;                                      // §7.3.103 Unsigned32
    public static final int AVP_SIPTO_PERMISSION = 1613;                               // §7.3.135 Enumerated (V only)
    public static final int AVP_UE_SRVCC_CAPABILITY = 1615;                            // §7.3.130 Enumerated (V only)
    public static final int AVP_LIPA_PERMISSION = 1618;                                // §7.3.133 Enumerated (V only)
    public static final int AVP_CLR_FLAGS = 1638;                                      // §7.3.152 Unsigned32 (V only)

    // Cancellation-Type values (3GPP TS 29.272 §7.3.24)
    public static final int CANCELLATION_TYPE_MME_UPDATE_PROCEDURE = 0;
    public static final int CANCELLATION_TYPE_SGSN_UPDATE_PROCEDURE = 1;
    public static final int CANCELLATION_TYPE_SUBSCRIPTION_WITHDRAWAL = 2;
    public static final int CANCELLATION_TYPE_UPDATE_PROCEDURE_IWF = 3;
    public static final int CANCELLATION_TYPE_INITIAL_ATTACH_PROCEDURE = 4;

    // Experimental-Result values (3GPP TS 29.272 §7.4), carried in the Experimental-Result AVP.
    // DIAMETER_ERROR_USER_UNKNOWN (5001) is reused from _3gppConstants.
    public static final long EXP_RES_DIAMETER_AUTHENTICATION_DATA_UNAVAILABLE = 4181L;
    public static final long EXP_RES_DIAMETER_ERROR_CAMEL_SUBSCRIPTION_PRESENT = 4182L;
    public static final long EXP_RES_DIAMETER_ERROR_ROAMING_NOT_ALLOWED = 5004L;
    public static final long EXP_RES_DIAMETER_ERROR_UNKNOWN_EPS_SUBSCRIPTION = 5420L;
    public static final long EXP_RES_DIAMETER_ERROR_RAT_NOT_ALLOWED = 5421L;
    public static final long EXP_RES_DIAMETER_ERROR_EQUIPMENT_UNKNOWN = 5422L;
    public static final long EXP_RES_DIAMETER_ERROR_UNKNOWN_SERVING_NODE = 5423L;

    private S6aConstants() {}
}
