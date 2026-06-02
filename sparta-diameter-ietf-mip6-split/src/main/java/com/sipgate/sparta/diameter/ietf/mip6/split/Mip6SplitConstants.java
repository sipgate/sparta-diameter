package com.sipgate.sparta.diameter.ietf.mip6.split;

/**
 * Constants for Diameter Mobile IPv6 — NAS to Diameter Server, split scenario (RFC 5447).
 * Limited to AVPs reused by other interfaces; no command messages.
 */
public final class Mip6SplitConstants {

    /** MIP6-Agent-Info — RFC 5447 §4.2.1. */
    public static final int AVP_MIP6_AGENT_INFO = 486;

    private Mip6SplitConstants() {}
}
