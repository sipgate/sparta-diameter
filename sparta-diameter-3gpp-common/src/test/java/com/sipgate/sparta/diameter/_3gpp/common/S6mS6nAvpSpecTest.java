package com.sipgate.sparta.diameter._3gpp.common;

import com.sipgate.sparta.diameter.base.AvpSpecTestBase;
import com.sipgate.sparta.diameter.spec.AvpDef;
import org.junit.jupiter.params.provider.Arguments;

import java.util.Set;
import java.util.stream.Stream;

public class S6mS6nAvpSpecTest extends AvpSpecTestBase {

    private static final Set<AvpDef> AVP_DEFS = loadAvpDefs("/s6ms6n-avps.json");
    private static final String PACKAGE = "com.sipgate.sparta.diameter._3gpp.common.mixins";

    static Stream<Arguments> provideAvpDefs() {
        return named(getImplementedAvpDefs(PACKAGE, AVP_DEFS));
    }

    @Override
    protected String mixinsPackage() {
        return PACKAGE;
    }
}
