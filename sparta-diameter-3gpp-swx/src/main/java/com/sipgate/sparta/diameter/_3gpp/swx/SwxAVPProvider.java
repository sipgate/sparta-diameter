package com.sipgate.sparta.diameter._3gpp.swx;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVPDefinition;
import com.sipgate.sparta.diameter.base.core.avp.AVPProvider;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

import java.util.Arrays;
import java.util.Collection;

/** Provides AVP definitions for the SWx Diameter interface (3GPP TS 29.273 §8.2.3). */
public final class SwxAVPProvider implements AVPProvider {

    private static final int V = _3gppConstants.VENDOR_ID_3GPP;

    @Override
    public Collection<AVPDefinition> getDefinitions() {
        return Arrays.asList(
            new AVPDefinition(SwxConstants.AVP_NON_3GPP_USER_DATA, "Non-3GPP-User-Data", GroupedAVP.class, true, true, V),
            new AVPDefinition(SwxConstants.AVP_NON_3GPP_IP_ACCESS, "Non-3GPP-IP-Access", Integer.class, true, true, V),
            new AVPDefinition(SwxConstants.AVP_NON_3GPP_IP_ACCESS_APN, "Non-3GPP-IP-Access-APN", Integer.class, true, true, V),
            new AVPDefinition(SwxConstants.AVP_AN_TRUSTED, "AN-Trusted", Integer.class, false, true, V),
            new AVPDefinition(SwxConstants.AVP_ANID, "ANID", String.class, true, true, V),
            new AVPDefinition(SwxConstants.AVP_TRACE_INFO, "Trace-Info", GroupedAVP.class, false, true, V),
            new AVPDefinition(SwxConstants.AVP_PPR_FLAGS, "PPR-Flags", Long.class, false, true, V),
            new AVPDefinition(SwxConstants.AVP_WLAN_IDENTIFIER, "WLAN-Identifier", GroupedAVP.class, false, true, V),
            new AVPDefinition(SwxConstants.AVP_TWAN_ACCESS_INFO, "TWAN-Access-Info", GroupedAVP.class, false, true, V),
            new AVPDefinition(SwxConstants.AVP_ACCESS_AUTHORIZATION_FLAGS, "Access-Authorization-Flags", Long.class, false, true, V),
            new AVPDefinition(SwxConstants.AVP_TWAN_DEFAULT_APN_CONTEXT_ID, "TWAN-Default-APN-Context-Id", Long.class, false, true, V),
            new AVPDefinition(SwxConstants.AVP_AAA_FAILURE_INDICATION, "AAA-Failure-Indication", Long.class, false, true, V),
            new AVPDefinition(SwxConstants.AVP_ACCESS_NETWORK_INFO, "Access-Network-Info", GroupedAVP.class, false, true, V),
            new AVPDefinition(SwxConstants.AVP_ERP_AUTHORIZATION, "ERP-Authorization", Long.class, false, true, V),
            new AVPDefinition(SwxConstants.AVP_3GPP_AAA_SERVER_NAME, "3GPP-AAA-Server-Name", String.class, true, true, V)
        );
    }
}
