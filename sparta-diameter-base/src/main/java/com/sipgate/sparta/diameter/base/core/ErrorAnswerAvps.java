package com.sipgate.sparta.diameter.base.core;

/**
 * Which session-scoped AVPs of the request a locally generated E-bit error answer
 * mirrors.
 *
 * <p>Proxy-Info is not part of this choice: RFC 6733 §6.2 requires every locally
 * generated answer to copy it.</p>
 */
public enum ErrorAnswerAvps {

    /** Mirror Auth-Session-State and Vendor-Specific-Application-Id. */
    ALL,

    /**
     * Mirror Auth-Session-State only. For relays that mimic a peer which omits
     * Vendor-Specific-Application-Id in locally generated errors.
     */
    AUTH_SESSION_STATE_ONLY
}
