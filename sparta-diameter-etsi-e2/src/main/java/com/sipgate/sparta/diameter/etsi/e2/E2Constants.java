package com.sipgate.sparta.diameter.etsi.e2;

/**
 * AVP definitions for the ETSI NASS e2 interface (ETSI ES 283 035 v3.2.1).
 * Used by Cx/Dx inside SIP-Auth-Data-Item for NASS-Bundled authentication
 * (referenced by 3GPP TS 29.229 §6.3.42).
 */
public final class E2Constants {

    /** ETSI vendor id. */
    public static final int VENDOR_ID_ETSI = 13019;

    /** Line-Identifier AVP (ES 283 035 §7.3.5). OctetString, V flag only (M-bit MUST NOT be set). */
    public static final int AVP_LINE_IDENTIFIER = 500;

    private E2Constants() {}
}
