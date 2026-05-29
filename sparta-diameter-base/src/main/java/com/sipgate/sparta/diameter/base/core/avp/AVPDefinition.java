package com.sipgate.sparta.diameter.base.core.avp;

/**
 * Defines the specification for a Diameter AVP.
 * Contains all metadata needed to properly construct AVPs with the correct flags and types.
 */
public record AVPDefinition(long code, String name, Class<?> dataType,
                             boolean mandatory, boolean vendorSpecific, long vendorId) {

    public AVPDefinition {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("AVP name cannot be null or empty");
        }
        if (dataType == null) {
            throw new IllegalArgumentException("Data type cannot be null");
        }
        if (vendorSpecific && vendorId == 0) {
            throw new IllegalArgumentException("Vendor-specific AVPs must have a non-zero vendor ID");
        }
    }
}
