package com.sipgate.sparta.diameter.ietf.nas;

/**
 * Framed-* AVP codes (IETF RFC 7155, which obsoletes RFC 4005).
 * <p>
 * Imported into Cx/Dx by 3GPP TS 29.229 §6.3.53–6.3.55 inside SIP-Auth-Data-Item.
 * Vendor 0. Note Framed-Interface-Id is Unsigned64 (RFC 7155 §4.4.10.5.5).
 * </p>
 */
public final class DiameterNasConstants {

    public static final int AVP_SERVICE_TYPE = 6;              // Enumerated
    public static final int AVP_FRAMED_PROTOCOL = 7;          // Enumerated
    public static final int AVP_FRAMED_IP_ADDRESS = 8;       // OctetString
    public static final int AVP_FRAMED_IP_NETMASK = 9;       // OctetString
    public static final int AVP_FRAMED_ROUTING = 10;         // Enumerated
    public static final int AVP_FILTER_ID = 11;              // UTF8String
    public static final int AVP_FRAMED_MTU = 12;             // Unsigned32
    public static final int AVP_FRAMED_COMPRESSION = 13;     // Enumerated
    public static final int AVP_LOGIN_IP_HOST = 14;          // OctetString
    public static final int AVP_LOGIN_SERVICE = 15;          // Enumerated
    public static final int AVP_LOGIN_TCP_PORT = 16;         // Unsigned32
    public static final int AVP_CALLBACK_NUMBER = 19;        // UTF8String
    public static final int AVP_CALLBACK_ID = 20;            // UTF8String
    public static final int AVP_FRAMED_ROUTE = 22;           // UTF8String
    public static final int AVP_FRAMED_IPX_NETWORK = 23;     // UTF8String
    public static final int AVP_IDLE_TIMEOUT = 28;           // Unsigned32
    public static final int AVP_LOGIN_LAT_SERVICE = 34;      // OctetString
    public static final int AVP_LOGIN_LAT_NODE = 35;         // OctetString
    public static final int AVP_LOGIN_LAT_GROUP = 36;        // OctetString
    public static final int AVP_FRAMED_APPLETALK_LINK = 37;  // Unsigned32
    public static final int AVP_FRAMED_APPLETALK_NETWORK = 38; // Unsigned32
    public static final int AVP_FRAMED_APPLETALK_ZONE = 39;  // OctetString
    public static final int AVP_PORT_LIMIT = 62;             // Unsigned32
    public static final int AVP_LOGIN_LAT_PORT = 63;         // OctetString
    public static final int AVP_ARAP_FEATURES = 71;          // OctetString
    public static final int AVP_ARAP_ZONE_ACCESS = 72;       // Enumerated
    public static final int AVP_CONFIGURATION_TOKEN = 78;    // OctetString
    public static final int AVP_FRAMED_POOL = 88;            // OctetString
    public static final int AVP_FRAMED_INTERFACE_ID = 96;    // Unsigned64
    public static final int AVP_FRAMED_IPV6_PREFIX = 97;     // OctetString
    public static final int AVP_LOGIN_IPV6_HOST = 98;        // OctetString
    public static final int AVP_FRAMED_IPV6_ROUTE = 99;      // UTF8String
    public static final int AVP_FRAMED_IPV6_POOL = 100;      // OctetString
    public static final int AVP_NAS_FILTER_RULE = 400;       // IPFilterRule

    private DiameterNasConstants() {}
}
