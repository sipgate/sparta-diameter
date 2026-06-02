package com.sipgate.sparta.diameter.ietf.mip6.integrated;

/**
 * Constants for Diameter Mobile IPv6 — Home Agent to Diameter Server, integrated scenario (RFC 5778).
 * Limited to AVPs reused by other interfaces; no command messages.
 */
public final class Mip6IntegratedConstants {

    /** Service-Selection — RFC 5778 §3.4. */
    public static final int AVP_SERVICE_SELECTION = 493;

    private Mip6IntegratedConstants() {}
}
