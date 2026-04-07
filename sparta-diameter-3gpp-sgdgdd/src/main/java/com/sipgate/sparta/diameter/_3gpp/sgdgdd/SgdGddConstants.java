package com.sipgate.sparta.diameter._3gpp.sgdgdd;

/**
 * Constants for the SGd/Gdd Diameter interface (3GPP TS 29.338 section 6).
 */
public final class SgdGddConstants {

    // 3GPP TS 29 338, Sgd/Gdd interfaces
    public static final int APP_ID_SGD_GDD = 16777313;

    // Command codes (3GPP TS 29.338 Table 6.3.2.2/1)
    public static final int CMD_MO_FORWARD_SHORT_MESSAGE = 8388645;
    public static final int CMD_MT_FORWARD_SHORT_MESSAGE = 8388646;

    // SGd/Gdd specific AVPs (3GPP TS 29.338 Table 6.3.3.1/1)
    public static final int AVP_SC_ADDRESS = 3300;
    public static final int AVP_SM_RP_UI = 3301;
    public static final int AVP_TFR_FLAGS = 3302;
    public static final int AVP_SM_DELIVERY_FAILURE_CAUSE = 3303;
    public static final int AVP_SM_ENUMERATED_DELIVERY_FAILURE_CAUSE = 3304;
    public static final int AVP_SM_DIAGNOSTIC_INFO = 3305;
    public static final int AVP_SM_DELIVERY_TIMER = 3306;
    public static final int AVP_SM_DELIVERY_START_TIME = 3307;
    public static final int AVP_SMSMI_CORRELATION_ID = 3324;
    public static final int AVP_HSS_ID = 3325;
    public static final int AVP_ORIGINATING_SIP_URI = 3326;
    public static final int AVP_DESTINATION_SIP_URI = 3327;
    public static final int AVP_OFR_FLAGS = 3328;
    public static final int AVP_MAXIMUM_RETRANSMISSION_TIME = 3330;
    public static final int AVP_REQUESTED_RETRANSMISSION_TIME = 3331;
    public static final int AVP_SMS_GMSC_ADDRESS = 3332;

    // Experimental Result values (3GPP TS 29.338 section 7, SGd/Gdd interfaces)
    // Carried in Experimental-Result AVP; Result-Code AVP shall be absent.

    // Shared with S6c (3GPP TS 29.338 section 7.3)
    public static final long EXP_RES_DIAMETER_ERROR_ABSENT_USER = 5550L;
    public static final long EXP_RES_DIAMETER_ERROR_SERVICE_BARRED = 5557L;

    // SGd/Gdd specific (3GPP TS 29.338 section 7.3)
    public static final long EXP_RES_DIAMETER_ERROR_USER_BUSY_FOR_MT_SMS = 5551L;
    public static final long EXP_RES_DIAMETER_ERROR_FACILITY_NOT_SUPPORTED = 5552L;
    public static final long EXP_RES_DIAMETER_ERROR_ILLEGAL_USER = 5553L;
    public static final long EXP_RES_DIAMETER_ERROR_ILLEGAL_EQUIPMENT = 5554L;
    public static final long EXP_RES_DIAMETER_ERROR_SM_DELIVERY_FAILURE = 5555L;

    private SgdGddConstants() {}
}
