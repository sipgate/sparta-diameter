package com.sipgate.sparta.diameter._3gpp.cxdx;

/**
 * Constants for the Cx/Dx Diameter interface (3GPP TS 29.229 / ETSI TS 129 229 v18.1.0).
 */
public final class CxDxConstants {

    /** Diameter application id for the Cx/Dx interface (TS 29.229 §6, IANA-allocated). */
    public static final int APP_ID_CX_DX = 16777216;

    // Command codes (TS 29.229 Table 6.1.1)
    public static final int CMD_SERVER_ASSIGNMENT = 301;
    public static final int CMD_MULTIMEDIA_AUTH = 303;
    public static final int CMD_REGISTRATION_TERMINATION = 304;

    // Cx/Dx-specific AVP codes (TS 29.229 Table 6.3.0.1). Vendor 3GPP (10415).
    public static final int AVP_PUBLIC_IDENTITY = 601;
    public static final int AVP_SERVER_NAME = 602;
    public static final int AVP_USER_DATA = 606;
    public static final int AVP_SIP_NUMBER_AUTH_ITEMS = 607;
    public static final int AVP_SIP_AUTHENTICATION_SCHEME = 608;
    public static final int AVP_SIP_AUTHENTICATE = 609;
    public static final int AVP_SIP_AUTHORIZATION = 610;
    public static final int AVP_SIP_AUTHENTICATION_CONTEXT = 611;
    public static final int AVP_SIP_AUTH_DATA_ITEM = 612;
    public static final int AVP_SIP_ITEM_NUMBER = 613;
    public static final int AVP_SERVER_ASSIGNMENT_TYPE = 614;
    public static final int AVP_DEREGISTRATION_REASON = 615;
    public static final int AVP_REASON_CODE = 616;
    public static final int AVP_REASON_INFO = 617;
    public static final int AVP_CHARGING_INFORMATION = 618;
    public static final int AVP_PRIMARY_EVENT_CHARGING_FUNCTION_NAME = 619;
    public static final int AVP_SECONDARY_EVENT_CHARGING_FUNCTION_NAME = 620;
    public static final int AVP_PRIMARY_CHARGING_COLLECTION_FUNCTION_NAME = 621;
    public static final int AVP_SECONDARY_CHARGING_COLLECTION_FUNCTION_NAME = 622;
    public static final int AVP_USER_DATA_ALREADY_AVAILABLE = 624;
    public static final int AVP_CONFIDENTIALITY_KEY = 625;
    public static final int AVP_INTEGRITY_KEY = 626;
    public static final int AVP_ASSOCIATED_IDENTITIES = 632;
    public static final int AVP_WILDCARDED_PUBLIC_IDENTITY = 634;
    public static final int AVP_SIP_DIGEST_AUTHENTICATE = 635;
    public static final int AVP_LOOSE_ROUTE_INDICATION = 638;
    public static final int AVP_SCSCF_RESTORATION_INFO = 639;
    public static final int AVP_PATH = 640;
    public static final int AVP_CONTACT = 641;
    public static final int AVP_SUBSCRIPTION_INFO = 642;
    public static final int AVP_CALL_ID_SIP_HEADER = 643;
    public static final int AVP_FROM_SIP_HEADER = 644;
    public static final int AVP_TO_SIP_HEADER = 645;
    public static final int AVP_RECORD_ROUTE = 646;
    public static final int AVP_ASSOCIATED_REGISTERED_IDENTITIES = 647;
    public static final int AVP_MULTIPLE_REGISTRATION_INDICATION = 648;
    public static final int AVP_RESTORATION_INFO = 649;
    public static final int AVP_SESSION_PRIORITY = 650;
    public static final int AVP_IDENTITY_WITH_EMERGENCY_REGISTRATION = 651;
    public static final int AVP_PRIVILEDGED_SENDER_INDICATION = 652;
    public static final int AVP_INITIAL_CSEQ_SEQUENCE_NUMBER = 654;
    public static final int AVP_SAR_FLAGS = 655;
    public static final int AVP_ALLOWED_WAF_WWSF_IDENTITIES = 656;
    public static final int AVP_WEBRTC_AUTHENTICATION_FUNCTION_NAME = 657;
    public static final int AVP_WEBRTC_WEB_SERVER_FUNCTION_NAME = 658;
    public static final int AVP_RTR_FLAGS = 659;
    public static final int AVP_PCSCF_SUBSCRIPTION_INFO = 660;
    public static final int AVP_REGISTRATION_TIME_OUT = 661;
    public static final int AVP_ALTERNATE_DIGEST_ALGORITHM = 662;
    public static final int AVP_ALTERNATE_DIGEST_HA1 = 663;
    public static final int AVP_FAILED_PCSCF = 664;
    public static final int AVP_PCSCF_FQDN = 665;
    public static final int AVP_PCSCF_IP_ADDRESS = 666;

    // re-used AVPs in Cx/Dx base on Radius Digest Authentication (RFC 5090)
    public static final int AVP_DIGEST_REALM = 104;
    public static final int AVP_DIGEST_QOP = 110;
    public static final int AVP_DIGEST_ALGORITHM = 111;
    public static final int AVP_DIGEST_HA1 = 121;

    // Server-Assignment-Type values (TS 29.229 §6.3.15)
    public static final int SERVER_ASSIGNMENT_NO_ASSIGNMENT = 0;
    public static final int SERVER_ASSIGNMENT_REGISTRATION = 1;
    public static final int SERVER_ASSIGNMENT_RE_REGISTRATION = 2;
    public static final int SERVER_ASSIGNMENT_UNREGISTERED_USER = 3;
    public static final int SERVER_ASSIGNMENT_TIMEOUT_DEREGISTRATION = 4;
    public static final int SERVER_ASSIGNMENT_USER_DEREGISTRATION = 5;
    public static final int SERVER_ASSIGNMENT_TIMEOUT_DEREGISTRATION_STORE_SERVER_NAME = 6;
    public static final int SERVER_ASSIGNMENT_USER_DEREGISTRATION_STORE_SERVER_NAME = 7;
    public static final int SERVER_ASSIGNMENT_ADMINISTRATIVE_DEREGISTRATION = 8;
    public static final int SERVER_ASSIGNMENT_AUTHENTICATION_FAILURE = 9;
    public static final int SERVER_ASSIGNMENT_AUTHENTICATION_TIMEOUT = 10;
    public static final int SERVER_ASSIGNMENT_DEREGISTRATION_TOO_MUCH_DATA = 11;

    // Reason-Code values (TS 29.229 §6.3.17)
    public static final int REASON_CODE_PERMANENT_TERMINATION = 0;
    public static final int REASON_CODE_NEW_SERVER_ASSIGNED = 1;
    public static final int REASON_CODE_SERVER_CHANGE = 2;
    public static final int REASON_CODE_REMOVE_SCSCF = 3;

    // User-Data-Already-Available values (TS 29.229 §6.3.26)
    public static final int USER_DATA_NOT_AVAILABLE = 0;
    public static final int USER_DATA_ALREADY_AVAILABLE = 1;

    // Experimental-Result values (TS 29.229 §6.2). 5001 (USER_UNKNOWN) is in _3gppConstants.
    public static final long EXP_RES_DIAMETER_FIRST_REGISTRATION = 2001L;
    public static final long EXP_RES_DIAMETER_SUBSEQUENT_REGISTRATION = 2002L;
    public static final long EXP_RES_DIAMETER_UNREGISTERED_SERVICE = 2003L;
    public static final long EXP_RES_DIAMETER_SUCCESS_SERVER_NAME_NOT_STORED = 2004L;
    public static final long EXP_RES_DIAMETER_ERROR_IDENTITIES_DONT_MATCH = 5002L;
    public static final long EXP_RES_DIAMETER_ERROR_IDENTITY_NOT_REGISTERED = 5003L;
    public static final long EXP_RES_DIAMETER_ERROR_ROAMING_NOT_ALLOWED = 5004L;
    public static final long EXP_RES_DIAMETER_ERROR_IDENTITY_ALREADY_REGISTERED = 5005L;
    public static final long EXP_RES_DIAMETER_ERROR_AUTH_SCHEME_NOT_SUPPORTED = 5006L;
    public static final long EXP_RES_DIAMETER_ERROR_IN_ASSIGNMENT_TYPE = 5007L;
    public static final long EXP_RES_DIAMETER_ERROR_TOO_MUCH_DATA = 5008L;
    public static final long EXP_RES_DIAMETER_ERROR_NOT_SUPPORTED_USER_DATA = 5009L;
    public static final long EXP_RES_DIAMETER_ERROR_FEATURE_UNSUPPORTED = 5011L;
    public static final long EXP_RES_DIAMETER_ERROR_SERVING_NODE_FEATURE_UNSUPPORTED = 5012L;

    private CxDxConstants() {}
}
