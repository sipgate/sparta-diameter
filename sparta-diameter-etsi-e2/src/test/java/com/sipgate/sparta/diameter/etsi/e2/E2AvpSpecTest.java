package com.sipgate.sparta.diameter.etsi.e2;

import com.sipgate.sparta.diameter.base.AvpSpecTestBase;
import com.sipgate.sparta.diameter.base.core.avp.AVPDefinition;
import com.sipgate.sparta.diameter.spec.AvpDef;
import org.junit.jupiter.params.provider.Arguments;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Stream;

public class E2AvpSpecTest extends AvpSpecTestBase {

    private static final Set<AvpDef> AVP_DEFS = loadAvpDefs("/e2-avps.json");
    private static final Collection<AVPDefinition> DEFINITIONS = new E2AVPProvider().getDefinitions();

    static Stream<Arguments> provideAvpDefs() {
        return named(AVP_DEFS.stream());
    }

    @Override
    protected String mixinsPackage() {
        return "com.sipgate.sparta.diameter.etsi.e2.mixins";
    }

    @Override
    protected Collection<AVPDefinition> getDefinitions() {
        return DEFINITIONS;
    }
}
