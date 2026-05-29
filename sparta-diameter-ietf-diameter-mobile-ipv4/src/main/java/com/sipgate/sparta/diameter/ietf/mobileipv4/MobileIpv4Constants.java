package com.sipgate.sparta.diameter.ietf.mobileipv4;

/**
 * Constants for the Diameter Mobile IPv4 Application (RFC 4004).
 */
public final class MobileIpv4Constants {

    /** MIP-Home-Agent-Address AVP code (RFC 4004 §7.4). Address, M flag, MUST NOT set V. */
    public static final int AVP_MIP_HOME_AGENT_ADDRESS = 334;

    /** MIP-Home-Agent-Host AVP code (RFC 4004 §7.11). Grouped, M flag, MUST NOT set V. */
    public static final int AVP_MIP_HOME_AGENT_HOST = 348;

    private MobileIpv4Constants() {}
}
