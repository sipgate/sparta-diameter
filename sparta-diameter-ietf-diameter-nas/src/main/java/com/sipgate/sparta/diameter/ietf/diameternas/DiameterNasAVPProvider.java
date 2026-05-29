package com.sipgate.sparta.diameter.ietf.diameternas;

import com.sipgate.sparta.diameter.base.core.avp.AVPDefinition;
import com.sipgate.sparta.diameter.base.core.avp.AVPProvider;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;

/**
 * AVP definitions for the RFC 7155 Framed-* AVPs used by Cx/Dx inside SIP-Auth-Data-Item
 * (3GPP TS 29.229 §6.3.13). Vendor 0.
 */
public final class DiameterNasAVPProvider implements AVPProvider {

    @Override
    public Collection<AVPDefinition> getDefinitions() {
        return List.of(
            new AVPDefinition(DiameterNasConstants.AVP_FRAMED_IP_ADDRESS, "Framed-IP-Address", byte[].class, true, false, 0),
            new AVPDefinition(DiameterNasConstants.AVP_FRAMED_INTERFACE_ID, "Framed-Interface-Id", BigInteger.class, true, false, 0),
            new AVPDefinition(DiameterNasConstants.AVP_FRAMED_IPV6_PREFIX, "Framed-IPv6-Prefix", byte[].class, true, false, 0)
        );
    }
}
