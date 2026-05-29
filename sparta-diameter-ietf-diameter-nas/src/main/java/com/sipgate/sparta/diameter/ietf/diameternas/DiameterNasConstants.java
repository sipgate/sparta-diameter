package com.sipgate.sparta.diameter.ietf.diameternas;

/**
 * Framed-* AVP codes (IETF RFC 7155, which obsoletes RFC 4005).
 * <p>
 * Imported into Cx/Dx by 3GPP TS 29.229 §6.3.53–6.3.55 inside SIP-Auth-Data-Item.
 * Vendor 0. Note Framed-Interface-Id is Unsigned64 (RFC 7155 §4.4.10.5.5).
 * </p>
 */
public final class DiameterNasConstants {

    public static final int AVP_FRAMED_IP_ADDRESS = 8;     // OctetString
    public static final int AVP_FRAMED_INTERFACE_ID = 96;  // Unsigned64
    public static final int AVP_FRAMED_IPV6_PREFIX = 97;   // OctetString

    private DiameterNasConstants() {}
}
