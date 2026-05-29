package com.sipgate.sparta.diameter.ietf.mobileipv4;

import com.sipgate.sparta.diameter.base.core.avp.AVPDefinition;
import com.sipgate.sparta.diameter.base.core.avp.AVPProvider;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

import java.net.InetAddress;
import java.util.Collection;
import java.util.List;

/**
 * Provides AVP definitions for the Diameter Mobile IPv4 Application (RFC 4004).
 * <p>
 * Both AVPs carry the M flag and MUST NOT set the V flag (vendor 0), per the RFC 4004 §7 AVP flag
 * table. MIP-Home-Agent-Host is of type Grouped per §7.11 (the flag-table "DiamIdent" entry is a
 * known erratum; the §7.11 body and its ABNF define a Grouped container).
 * </p>
 */
public final class MobileIpv4AVPProvider implements AVPProvider {

    @Override
    public Collection<AVPDefinition> getDefinitions() {
        return List.of(
            new AVPDefinition(MobileIpv4Constants.AVP_MIP_HOME_AGENT_ADDRESS, "MIP-Home-Agent-Address",
                InetAddress.class, true, false, 0),
            new AVPDefinition(MobileIpv4Constants.AVP_MIP_HOME_AGENT_HOST, "MIP-Home-Agent-Host",
                GroupedAVP.class, true, false, 0)
        );
    }
}
