package com.sipgate.sparta.diameter.ietf.mip6serviceselection;

/**
 * Constants for the Service-Selection AVP defined by Diameter Mobile IPv6 (RFC 5778, HA-to-AAAH).
 */
public final class Mip6ServiceSelectionConstants {

    /** Service-Selection AVP code (RFC 5778 §6.2). UTF8String, M flag, MUST NOT set V. */
    public static final int AVP_SERVICE_SELECTION = 493;

    private Mip6ServiceSelectionConstants() {}
}
