package com.sipgate.sparta.diameter.ietf.mip6.split;

import com.sipgate.sparta.diameter.base.core.avp.AVPDefinition;
import com.sipgate.sparta.diameter.base.core.avp.AVPProvider;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

import java.math.BigInteger;
import java.net.InetAddress;
import java.util.Collection;
import java.util.List;

/** Provides AVP definitions defined by RFC 5447 that are reused on other interfaces. */
public final class Mip6SplitAVPProvider implements AVPProvider {

    @Override
    public Collection<AVPDefinition> getDefinitions() {
        return List.of(
            // flag rules per RFC 5778 §6: M MUST be set, V MUST NOT be set, vendor id 0
            new AVPDefinition(Mip6SplitConstants.AVP_MIP6_AGENT_INFO, "MIP6-Agent-Info", GroupedAVP.class, true, false, 0),
            new AVPDefinition(Mip6SplitConstants.AVP_MIP_HOME_AGENT_HOST, "MIP-Home-Agent-Host", GroupedAVP.class, true, false, 0),
            new AVPDefinition(Mip6SplitConstants.AVP_MIP_HOME_AGENT_ADDRESS, "MIP-Home-Agent-Address", InetAddress.class, true, false, 0),
            new AVPDefinition(Mip6SplitConstants.AVP_MIP6_HOME_LINK_PREFIX, "MIP6-Home-Link-Prefix", byte[].class, true, false, 0),
            new AVPDefinition(Mip6SplitConstants.AVP_MIP6_FEATURE_VECTOR, "MIP6-Feature-Vector", BigInteger.class, true, false, 0)
        );
    }
}
