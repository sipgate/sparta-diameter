package com.sipgate.sparta.diameter.ietf.drmp;

/**
 * Constants for Diameter Routing Message Priority (RFC 7944).
 */
public final class DrmpConstants {

    /** DRMP AVP code (RFC 7944 §9.1). Enumerated, no M or V flags. */
    public static final int AVP_DRMP = 301;

    // Priority values — lower number means higher priority (RFC 7944 §9.1).
    public static final int PRIORITY_0  =  0;
    public static final int PRIORITY_1  =  1;
    public static final int PRIORITY_2  =  2;
    public static final int PRIORITY_3  =  3;
    public static final int PRIORITY_4  =  4;
    public static final int PRIORITY_5  =  5;
    public static final int PRIORITY_6  =  6;
    public static final int PRIORITY_7  =  7;
    public static final int PRIORITY_8  =  8;
    public static final int PRIORITY_9  =  9;
    public static final int PRIORITY_10 = 10;
    public static final int PRIORITY_11 = 11;
    public static final int PRIORITY_12 = 12;
    public static final int PRIORITY_13 = 13;
    public static final int PRIORITY_14 = 14;
    public static final int PRIORITY_15 = 15;

    private DrmpConstants() {}
}
