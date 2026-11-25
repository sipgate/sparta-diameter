package com.sipgate.sparta.diameter.base.transport;

/**
 * SCTP payload protocol identifiers for Diameter traffic, as registered in the IANA
 * PAYLOAD-PROTOCOL-ID registry and referenced by RFC 6733 §2.1.1.
 */
public final class SctpPayloadProtocolId {

    /**
     * Cleartext Diameter over SCTP. RFC 6733 §2.1.1: agents SHOULD use PPID 46
     * when sending Diameter messages unprotected over SCTP.
     */
    public static final int DIAMETER = 46;

    /**
     * Unspecified payload protocol (RFC 4960 §3.3.1). Many Diameter peers send PPID 0
     * instead of 46; the inbound decoder accepts it as cleartext Diameter. Never sent.
     */
    public static final int UNSPECIFIED = 0;

    /**
     * DTLS-protected Diameter over SCTP (RFC 6083). The plain-SCTP transport of this
     * module does not use it yet; the inbound decoder drops it with a warning.
     */
    public static final int DTLS = 47;

    private SctpPayloadProtocolId() {
        // constants only
    }
}
