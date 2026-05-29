package com.sipgate.sparta.diameter.ietf.radiusdigestauthentication;

import com.sipgate.sparta.diameter.base.core.avp.AVPDefinition;
import com.sipgate.sparta.diameter.base.core.avp.AVPProvider;

import java.util.Collection;
import java.util.List;

/**
 * AVP definitions for the RFC 5090 digest authentication AVPs used by Cx/Dx
 * inside SIP-Digest-Authenticate (3GPP TS 29.229 §6.3.36). Vendor 0, UTF8String, M-bit set.
 */
public final class RadiusDigestAuthenticationAVPProvider implements AVPProvider {

    @Override
    public Collection<AVPDefinition> getDefinitions() {
        return List.of(
            new AVPDefinition(RadiusDigestAuthenticationConstants.AVP_DIGEST_REALM, "Digest-Realm", String.class, true, false, 0),
            new AVPDefinition(RadiusDigestAuthenticationConstants.AVP_DIGEST_QOP, "Digest-QoP", String.class, true, false, 0),
            new AVPDefinition(RadiusDigestAuthenticationConstants.AVP_DIGEST_ALGORITHM, "Digest-Algorithm", String.class, true, false, 0),
            new AVPDefinition(RadiusDigestAuthenticationConstants.AVP_DIGEST_HA1, "Digest-HA1", String.class, true, false, 0)
        );
    }
}
