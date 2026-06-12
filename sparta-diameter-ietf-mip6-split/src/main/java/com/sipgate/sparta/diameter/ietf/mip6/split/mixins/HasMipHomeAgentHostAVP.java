package com.sipgate.sparta.diameter.ietf.mip6.split.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;
import com.sipgate.sparta.diameter.ietf.mip6.split.Mip6SplitConstants;

import java.util.List;

/** Mixin for containers carrying the MIP-Home-Agent-Host grouped AVP (RFC 4004 §7.11 / RFC 5447 §4.2.3, code 348). */
public interface HasMipHomeAgentHostAVP extends AVPContainer {

    default void setMipHomeAgentHost(final List<AVP> avps) {
        setAVP(AVP.create(new AVPKey(Mip6SplitConstants.AVP_MIP_HOME_AGENT_HOST, 0), avps));
    }

    default AVPContainer getMipHomeAgentHost() {
        final var avp = findAVP(new AVPKey(Mip6SplitConstants.AVP_MIP_HOME_AGENT_HOST, 0));
        return avp instanceof final GroupedAVP grouped ? grouped : null;
    }
}
