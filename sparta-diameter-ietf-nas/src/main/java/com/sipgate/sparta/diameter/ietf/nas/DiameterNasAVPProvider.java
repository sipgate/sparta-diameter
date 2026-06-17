package com.sipgate.sparta.diameter.ietf.nas;

import com.sipgate.sparta.diameter.base.core.avp.AVPDefinition;
import com.sipgate.sparta.diameter.base.core.avp.AVPProvider;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;

/**
 * AVP definitions for the IETF NAS AVPs (RFC 4005 §6, obsoleted by RFC 7155), including the
 * Framed-* AVPs used by Cx/Dx inside SIP-Auth-Data-Item (3GPP TS 29.229 §6.3.13). All vendor 0.
 */
public final class DiameterNasAVPProvider implements AVPProvider {

    @Override
    public Collection<AVPDefinition> getDefinitions() {
        return List.of(
            new AVPDefinition(DiameterNasConstants.AVP_SERVICE_TYPE, "Service-Type", Integer.class, true, false, 0),
            new AVPDefinition(DiameterNasConstants.AVP_FRAMED_PROTOCOL, "Framed-Protocol", Integer.class, true, false, 0),
            new AVPDefinition(DiameterNasConstants.AVP_FRAMED_IP_ADDRESS, "Framed-IP-Address", byte[].class, true, false, 0),
            new AVPDefinition(DiameterNasConstants.AVP_FRAMED_IP_NETMASK, "Framed-IP-Netmask", byte[].class, true, false, 0),
            new AVPDefinition(DiameterNasConstants.AVP_FRAMED_ROUTING, "Framed-Routing", Integer.class, true, false, 0),
            new AVPDefinition(DiameterNasConstants.AVP_FILTER_ID, "Filter-Id", String.class, true, false, 0),
            new AVPDefinition(DiameterNasConstants.AVP_FRAMED_MTU, "Framed-MTU", Long.class, true, false, 0),
            new AVPDefinition(DiameterNasConstants.AVP_FRAMED_COMPRESSION, "Framed-Compression", Integer.class, true, false, 0),
            new AVPDefinition(DiameterNasConstants.AVP_LOGIN_IP_HOST, "Login-IP-Host", byte[].class, true, false, 0),
            new AVPDefinition(DiameterNasConstants.AVP_LOGIN_SERVICE, "Login-Service", Integer.class, true, false, 0),
            new AVPDefinition(DiameterNasConstants.AVP_LOGIN_TCP_PORT, "Login-TCP-Port", Long.class, true, false, 0),
            new AVPDefinition(DiameterNasConstants.AVP_CALLBACK_NUMBER, "Callback-Number", String.class, true, false, 0),
            new AVPDefinition(DiameterNasConstants.AVP_CALLBACK_ID, "Callback-Id", String.class, true, false, 0),
            new AVPDefinition(DiameterNasConstants.AVP_FRAMED_ROUTE, "Framed-Route", String.class, true, false, 0),
            new AVPDefinition(DiameterNasConstants.AVP_FRAMED_IPX_NETWORK, "Framed-IPX-Network", String.class, true, false, 0),
            new AVPDefinition(DiameterNasConstants.AVP_IDLE_TIMEOUT, "Idle-Timeout", Long.class, true, false, 0),
            new AVPDefinition(DiameterNasConstants.AVP_LOGIN_LAT_SERVICE, "Login-LAT-Service", byte[].class, true, false, 0),
            new AVPDefinition(DiameterNasConstants.AVP_LOGIN_LAT_NODE, "Login-LAT-Node", byte[].class, true, false, 0),
            new AVPDefinition(DiameterNasConstants.AVP_LOGIN_LAT_GROUP, "Login-LAT-Group", byte[].class, true, false, 0),
            new AVPDefinition(DiameterNasConstants.AVP_FRAMED_APPLETALK_LINK, "Framed-Appletalk-Link", Long.class, true, false, 0),
            new AVPDefinition(DiameterNasConstants.AVP_FRAMED_APPLETALK_NETWORK, "Framed-Appletalk-Network", Long.class, true, false, 0),
            new AVPDefinition(DiameterNasConstants.AVP_FRAMED_APPLETALK_ZONE, "Framed-Appletalk-Zone", byte[].class, true, false, 0),
            new AVPDefinition(DiameterNasConstants.AVP_PORT_LIMIT, "Port-Limit", Long.class, true, false, 0),
            new AVPDefinition(DiameterNasConstants.AVP_LOGIN_LAT_PORT, "Login-LAT-Port", byte[].class, true, false, 0),
            new AVPDefinition(DiameterNasConstants.AVP_ARAP_FEATURES, "ARAP-Features", byte[].class, true, false, 0),
            new AVPDefinition(DiameterNasConstants.AVP_ARAP_ZONE_ACCESS, "ARAP-Zone-Access", Integer.class, true, false, 0),
            new AVPDefinition(DiameterNasConstants.AVP_CONFIGURATION_TOKEN, "Configuration-Token", byte[].class, true, false, 0),
            new AVPDefinition(DiameterNasConstants.AVP_FRAMED_POOL, "Framed-Pool", byte[].class, true, false, 0),
            new AVPDefinition(DiameterNasConstants.AVP_FRAMED_INTERFACE_ID, "Framed-Interface-Id", BigInteger.class, true, false, 0),
            new AVPDefinition(DiameterNasConstants.AVP_FRAMED_IPV6_PREFIX, "Framed-IPv6-Prefix", byte[].class, true, false, 0),
            new AVPDefinition(DiameterNasConstants.AVP_LOGIN_IPV6_HOST, "Login-IPv6-Host", byte[].class, true, false, 0),
            new AVPDefinition(DiameterNasConstants.AVP_FRAMED_IPV6_ROUTE, "Framed-IPv6-Route", String.class, true, false, 0),
            new AVPDefinition(DiameterNasConstants.AVP_FRAMED_IPV6_POOL, "Framed-IPv6-Pool", byte[].class, true, false, 0),
            new AVPDefinition(DiameterNasConstants.AVP_NAS_FILTER_RULE, "NAS-Filter-Rule", String.class, true, false, 0)
        );
    }
}
