package com.sipgate.sparta.diameter.spec;

import java.util.Set;

public record AvpDef(
    long vendorId,
    long avpCode,
    String attributeName,
    String valueType,
    AvpFlagRule mandatoryBit,
    AvpFlagRule vendorSpecificBit,
    boolean mayBeEncrypted
) {

    public static final Set<String> KNOWN_TYPES = Set.of(
            "OctetString", "Integer32", "Integer64", "Unsigned32", "Unsigned64",
            "Float32", "Float64", "Grouped", "Address", "Time", "UTF8String",
            "DiameterIdentity", "DiameterURI", "Enumerated", "IPFilterRule", "QoSFilterRule");

    public AvpDef {
        if (!KNOWN_TYPES.contains(valueType)) {
            throw new IllegalArgumentException(
                    "Unknown valueType '" + valueType + "' for AVP " + attributeName
                            + " (code=" + avpCode + ", vendorId=" + vendorId + "); expected one of " + KNOWN_TYPES);
        }
    }
}
