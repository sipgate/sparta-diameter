package com.sipgate.sparta.diameter.ietf.doic;

/**
 * Constants for Diameter Overload Indication Conveyance (RFC 7683).
 */
public final class DoicConstants {

    // AVP codes (RFC 7683 §7.8). M not set, V MUST NOT be set; vendor id 0.
    public static final int AVP_OC_SUPPORTED_FEATURES = 621;
    public static final int AVP_OC_FEATURE_VECTOR = 622;
    public static final int AVP_OC_OLR = 623;
    public static final int AVP_OC_SEQUENCE_NUMBER = 624;
    public static final int AVP_OC_VALIDITY_DURATION = 625;
    public static final int AVP_OC_REPORT_TYPE = 626;
    public static final int AVP_OC_REDUCTION_PERCENTAGE = 627;

    /** OC-Feature-Vector value: loss-based abatement algorithm (RFC 7683 §7.2). */
    public static final long OLR_DEFAULT_ALGO_LOSS = 0x0000000000000001L;

    // OC-Report-Type values (RFC 7683 §7.6)
    public static final int REPORT_TYPE_HOST_REPORT = 0;
    public static final int REPORT_TYPE_REALM_REPORT = 1;

    private DoicConstants() {}
}
