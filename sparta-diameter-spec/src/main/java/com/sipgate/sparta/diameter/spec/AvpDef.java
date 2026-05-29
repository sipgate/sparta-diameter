package com.sipgate.sparta.diameter.spec;

public record AvpDef(
    long applicationId,
    long avpCode,
    String attributeName,
    String valueType,
    AvpFlagRule mandatoryBit,
    AvpFlagRule vendorSpecificBit,
    boolean mayBeEncrypted
) {
}
