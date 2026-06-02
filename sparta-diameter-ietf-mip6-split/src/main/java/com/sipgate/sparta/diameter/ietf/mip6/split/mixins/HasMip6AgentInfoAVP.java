package com.sipgate.sparta.diameter.ietf.mip6.split.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;
import com.sipgate.sparta.diameter.ietf.mip6.split.Mip6SplitConstants;

import java.util.List;

/** Mixin for messages carrying the MIP6-Agent-Info grouped AVP (RFC 5447 §4.2.1, code 486). */
public interface HasMip6AgentInfoAVP extends AVPContainer {

    default void setMip6AgentInfo(final List<AVP> avps) {
        setAVP(AVP.create(new AVPKey(Mip6SplitConstants.AVP_MIP6_AGENT_INFO, 0), avps));
    }

    default AVPContainer getMip6AgentInfo() {
        final var avp = findAVP(new AVPKey(Mip6SplitConstants.AVP_MIP6_AGENT_INFO, 0));
        return avp instanceof final GroupedAVP grouped ? grouped : null;
    }
}
