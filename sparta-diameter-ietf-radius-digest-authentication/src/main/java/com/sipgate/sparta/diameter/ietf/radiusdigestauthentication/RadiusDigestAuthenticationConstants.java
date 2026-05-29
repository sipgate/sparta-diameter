package com.sipgate.sparta.diameter.ietf.radiusdigestauthentication;

/**
 * Digest authentication AVP codes (IETF RFC 5090, which obsoletes RFC 4590).
 * <p>
 * Imported into the Diameter Cx/Dx application by 3GPP TS 29.229 §6.3.37–6.3.41.
 * All are vendor 0 (IETF namespace), type UTF8String, M-bit set.
 * </p>
 */
public final class RadiusDigestAuthenticationConstants {

    public static final int AVP_DIGEST_REALM = 104;
    public static final int AVP_DIGEST_QOP = 110;
    public static final int AVP_DIGEST_ALGORITHM = 111;
    public static final int AVP_DIGEST_HA1 = 121;

    private RadiusDigestAuthenticationConstants() {}
}
