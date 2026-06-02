package com.sipgate.sparta.diameter.ietf.mip6.split;

import com.sipgate.sparta.diameter.base.core.avp.AVPDefinition;
import com.sipgate.sparta.diameter.base.core.avp.AVPProvider;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

import java.util.Collection;
import java.util.List;

/** Provides AVP definitions defined by RFC 5447 that are reused on other interfaces. */
public final class Mip6SplitAVPProvider implements AVPProvider {

    @Override
    public Collection<AVPDefinition> getDefinitions() {
        return List.of(
            // RFC 5447 §6: M may be set, V MUST NOT be set, vendor id 0
            new AVPDefinition(Mip6SplitConstants.AVP_MIP6_AGENT_INFO, "MIP6-Agent-Info", GroupedAVP.class, true, false, 0)
        );
    }
}
