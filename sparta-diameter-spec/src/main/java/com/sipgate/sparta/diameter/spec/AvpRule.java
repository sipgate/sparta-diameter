package com.sipgate.sparta.diameter.spec;

/**
 * An AVP occurrence rule from a CCF (RFC 6733 §3.2): cardinality bounds
 * plus the AVP name.
 *
 * <p>{@code max} is {@link Long#MAX_VALUE} when unbounded ("infinity" in
 * the RFC).
 */
public record AvpRule(long min, long max, String avpName) {
}
