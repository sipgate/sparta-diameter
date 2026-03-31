package com.sipgate.sparta.diameter.base.core.avp;

/**
 * Composite key identifying a Diameter AVP by code and vendor ID.
 * <p>
 * Per RFC 6733 §4, the AVP code space is per-vendor: the same code under
 * different vendor IDs denotes distinct AVPs. Use vendor ID 0 for IETF base
 * AVPs that carry no vendor-specific flag on the wire.
 * </p>
 *
 * @param code     the AVP code
 * @param vendorId the vendor ID, or 0 for IETF base AVPs (i.e. not vendor-specific)
 */
public record AVPKey(int code, int vendorId) {
    // We don't have an overloaded constructor with code only for not vendor-specific, because we want to be explicit.
    // If we had such constructor, one could accidentally forget to set a vendor ID.
}
