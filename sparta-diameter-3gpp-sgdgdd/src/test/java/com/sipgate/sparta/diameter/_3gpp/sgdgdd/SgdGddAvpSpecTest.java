package com.sipgate.sparta.diameter._3gpp.sgdgdd;

import com.sipgate.sparta.diameter.base.AvpSpecTestBase;
import com.sipgate.sparta.diameter.spec.AvpDef;
import org.junit.jupiter.params.provider.Arguments;

import java.util.Set;
import java.util.stream.Stream;

public class SgdGddAvpSpecTest extends AvpSpecTestBase {

    private static final Set<AvpDef> AVP_DEFS = loadAvpDefs("/sgdgdd-avps.json");

    static Stream<Arguments> provideAvpDefs() {
        return named(AVP_DEFS.stream());
    }

    @Override
    protected String mixinsPackage() {
        return "com.sipgate.sparta.diameter._3gpp.sgdgdd.mixins";
    }
}
