package com.sipgate.sparta.diameter.ietf.load;

/**
 * Constants for Diameter Load Information Conveyance (RFC 8583).
 */
public final class LoadConstants {

    // AVP codes (RFC 8583 §7.5). M not set, V MUST NOT be set; vendor id 0.
    public static final int AVP_LOAD = 650;
    public static final int AVP_LOAD_TYPE = 651;
    public static final int AVP_LOAD_VALUE = 652;

    // Load-Type values (RFC 8583 §7.2)
    public static final int LOAD_TYPE_HOST = 0;
    public static final int LOAD_TYPE_PEER = 1;

    /** A node with zero load reports 65535; a fully loaded node reports 0 (RFC 8583 §7.3). */
    public static final int LOAD_VALUE_MAX = 65535;

    private LoadConstants() {}
}
