package com.sipgate.sparta.diameter.ietf.mip6.split;

/**
 * Constants for Diameter Mobile IPv6 — NAS to Diameter Server, split scenario (RFC 5447).
 * Limited to AVPs reused by other interfaces; no command messages.
 */
public final class Mip6SplitConstants {

    /** MIP6-Agent-Info — RFC 5447 §4.2.1. */
    public static final int AVP_MIP6_AGENT_INFO = 486;

    /** MIP-Home-Agent-Host — RFC 4004 §7.11, reused inside MIP6-Agent-Info (RFC 5447 §4.2.3). */
    public static final int AVP_MIP_HOME_AGENT_HOST = 348;

    /** MIP-Home-Agent-Address — RFC 4004 §7.10, reused inside MIP6-Agent-Info (RFC 5447 §4.2.2). */
    public static final int AVP_MIP_HOME_AGENT_ADDRESS = 334;

    /** MIP6-Home-Link-Prefix — RFC 5447 §4.2.4. */
    public static final int AVP_MIP6_HOME_LINK_PREFIX = 125;

    /** MIP6-Feature-Vector — RFC 5447 §4.2.5. */
    public static final int AVP_MIP6_FEATURE_VECTOR = 124;

    private Mip6SplitConstants() {}
}
