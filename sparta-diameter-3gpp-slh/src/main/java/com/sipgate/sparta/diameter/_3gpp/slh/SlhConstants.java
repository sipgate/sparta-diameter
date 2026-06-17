package com.sipgate.sparta.diameter._3gpp.slh;

/**
 * Constants for the SLh Diameter interface (3GPP TS 29.173). Limited to AVPs that other
 * interfaces (e.g. S6a in TS 29.272) reference; no command messages.
 */
public final class SlhConstants {

    /** LMSI — TS 29.173 §6.4.2. */
    public static final int AVP_LMSI = 2400;

    /** Serving-Node — TS 29.173 §6.4.3. */
    public static final int AVP_SERVING_NODE = 2401;

    /** MME-Name — TS 29.173 §6.4.4. */
    public static final int AVP_MME_NAME = 2402;

    /** MSC-Number — TS 29.173 §6.4.5. */
    public static final int AVP_MSC_NUMBER = 2403;

    /** LCS-Capabilities-Sets — TS 29.173 §6.4.6. */
    public static final int AVP_LCS_CAPABILITIES_SETS = 2404;

    /** GMLC-Address — TS 29.173 §6.4.7. */
    public static final int AVP_GMLC_ADDRESS = 2405;

    /** Additional-Serving-Node — TS 29.173 §6.4.8. */
    public static final int AVP_ADDITIONAL_SERVING_NODE = 2406;

    /** PPR-Address — TS 29.173 §6.4.9. */
    public static final int AVP_PPR_ADDRESS = 2407;

    private SlhConstants() {}
}
