package com.sipgate.sparta.diameter.ietf.mobileipv6;

import com.sipgate.sparta.diameter.base.core.avp.AVPDefinition;
import com.sipgate.sparta.diameter.base.core.avp.AVPProvider;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

import java.util.Collection;
import java.util.List;

/**
 * Provides AVP definitions for Diameter Mobile IPv6 (RFC 5447).
 * <p>
 * MIP6-Agent-Info (486) is of type Grouped with the M flag set and the V flag unset (vendor 0),
 * per RFC 5447 §4.2.1 (confirmed by the RFC 5778 reused-AVP flag table). Its children
 * (MIP-Home-Agent-Address/Host) are defined by RFC 4004 in {@code …-mobile-ipv4}.
 * </p>
 */
public final class MobileIpv6AVPProvider implements AVPProvider {

    @Override
    public Collection<AVPDefinition> getDefinitions() {
        return List.of(
            new AVPDefinition(MobileIpv6Constants.AVP_MIP6_AGENT_INFO, "MIP6-Agent-Info",
                GroupedAVP.class, true, false, 0)
        );
    }
}
