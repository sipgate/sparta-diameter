package com.sipgate.sparta.diameter._3gpp.s6c;

/**
 * Constants for the S6c Diameter interface (3GPP TS 29.338 section 5).
 */
public final class S6cConstants {

    // Experimental Result values (3GPP TS 29.338 section 7, S6c interface)
    // Carried in Experimental-Result AVP; Result-Code AVP shall be absent.

    // Shared with SGd/Gdd (3GPP TS 29.338 section 7.3)
    public static final long EXP_RES_DIAMETER_ERROR_ABSENT_USER = 5550L;
    public static final long EXP_RES_DIAMETER_ERROR_SERVICE_BARRED = 5557L;

    // S6c specific (3GPP TS 29.338 section 7.3)
    public static final long EXP_RES_DIAMETER_ERROR_SERVICE_NOT_SUBSCRIBED = 5556L;
    public static final long EXP_RES_DIAMETER_ERROR_MWD_LIST_FULL = 5558L;

    private S6cConstants() {}
}
