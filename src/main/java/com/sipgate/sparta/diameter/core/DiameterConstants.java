package com.sipgate.sparta.diameter.core;

/**
 * Constants for Diameter protocol.
 */
public final class DiameterConstants {

    // Diameter version
    public static final int DIAMETER_VERSION = 1;

    // Common Command Codes
    public static final int CMD_CAPABILITIES_EXCHANGE = 257;
    public static final int CMD_DEVICE_WATCHDOG = 280;
    public static final int CMD_DISCONNECT_PEER = 282;

    // Common Application IDs
    public static final int DIAMETER_COMMON_MESSAGES = 0;

    // Common AVP Codes
    public static final int RESULT_CODE = 268;
    public static final int ORIGIN_HOST = 264;
    public static final int ORIGIN_REALM = 296;
    public static final int HOST_IP_ADDRESS = 257;
    public static final int VENDOR_ID = 266;
    public static final int PRODUCT_NAME = 269;
    public static final int SUPPORTED_VENDOR_ID = 265;
    public static final int AUTH_APPLICATION_ID = 258;
    public static final int ACCT_APPLICATION_ID = 259;
    public static final int VENDOR_SPECIFIC_APPLICATION_ID = 260;
    public static final int FIRMWARE_REVISION = 267;
    public static final int ORIGIN_STATE_ID = 278;
    public static final int ERROR_MESSAGE = 281;
    public static final int FAILED_AVP = 279;
    public static final int DISCONNECT_CAUSE = 273;

    // Result Codes
    public static final int DIAMETER_SUCCESS = 2001;
    public static final int DIAMETER_COMMAND_UNSUPPORTED = 3001;
    public static final int DIAMETER_UNABLE_TO_DELIVER = 3002;
    public static final int DIAMETER_REALM_NOT_SERVED = 3003;
    public static final int DIAMETER_TOO_BUSY = 3004;
    public static final int DIAMETER_LOOP_DETECTED = 3005;
    public static final int DIAMETER_REDIRECT_INDICATION = 3006;
    public static final int DIAMETER_APPLICATION_UNSUPPORTED = 3007;
    public static final int DIAMETER_INVALID_HDR_BITS = 3008;
    public static final int DIAMETER_INVALID_AVP_BITS = 3009;
    public static final int DIAMETER_UNKNOWN_PEER = 3010;

    // Disconnect Causes
    public static final int REBOOTING = 0;
    public static final int BUSY = 1;
    public static final int DO_NOT_WANT_TO_TALK_TO_YOU = 2;

    private DiameterConstants() {
        // Utility class - no instantiation
    }
}
