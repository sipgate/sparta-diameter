package com.sipgate.sparta.diameter.core.avp;

/**
 * Defines the specification for a Diameter AVP.
 * Contains all metadata needed to properly construct AVPs with the correct flags and types.
 */
public final class AVPDefinition {
    private final int code;
    private final String name;
    private final Class<?> dataType;
    private final boolean mandatory;
    private final boolean vendorSpecific;
    private final int vendorId;

    public AVPDefinition(final int code, final String name, final Class<?> dataType,
                        final boolean mandatory, final boolean vendorSpecific, final int vendorId) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("AVP name cannot be null or empty");
        }
        if (dataType == null) {
            throw new IllegalArgumentException("Data type cannot be null");
        }
        if (vendorSpecific && vendorId == 0) {
            throw new IllegalArgumentException("Vendor-specific AVPs must have a non-zero vendor ID");
        }

        this.code = code;
        this.name = name;
        this.dataType = dataType;
        this.mandatory = mandatory;
        this.vendorSpecific = vendorSpecific;
        this.vendorId = vendorId;
    }

    public int code() {
        return code;
    }

    public String name() {
        return name;
    }

    public Class<?> dataType() {
        return dataType;
    }

    public boolean mandatory() {
        return mandatory;
    }

    public boolean vendorSpecific() {
        return vendorSpecific;
    }

    public int vendorId() {
        return vendorId;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final AVPDefinition that = (AVPDefinition) obj;
        return code == that.code &&
               mandatory == that.mandatory &&
               vendorSpecific == that.vendorSpecific &&
               vendorId == that.vendorId &&
               name.equals(that.name) &&
               dataType.equals(that.dataType);
    }

    @Override
    public int hashCode() {
        int result = code;
        result = 31 * result + name.hashCode();
        result = 31 * result + dataType.hashCode();
        result = 31 * result + (mandatory ? 1 : 0);
        result = 31 * result + (vendorSpecific ? 1 : 0);
        result = 31 * result + vendorId;
        return result;
    }

    @Override
    public String toString() {
        return "AVPDefinition{" +
               "code=" + code +
               ", name='" + name + '\'' +
               ", dataType=" + dataType.getSimpleName() +
               ", mandatory=" + mandatory +
               ", vendorSpecific=" + vendorSpecific +
               ", vendorId=" + vendorId +
               '}';
    }
}
