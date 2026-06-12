package com.sipgate.sparta.diameter.ietf.mip6.split.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.mip6.split.Mip6SplitConstants;

import java.net.InetAddress;

/** Mixin for containers carrying the MIP-Home-Agent-Address AVP (RFC 4004 §7.10 / RFC 5447 §4.2.2, code 334). */
public interface HasMipHomeAgentAddressAVP extends AVPContainer {

    default void setMipHomeAgentAddress(final InetAddress value) {
        setAVP(AVP.create(new AVPKey(Mip6SplitConstants.AVP_MIP_HOME_AGENT_ADDRESS, 0), value));
    }

    default InetAddress getMipHomeAgentAddress() {
        final var avp = findAVP(new AVPKey(Mip6SplitConstants.AVP_MIP_HOME_AGENT_ADDRESS, 0));
        return avp != null ? avp.getDataAsIPAddress() : null;
    }
}
