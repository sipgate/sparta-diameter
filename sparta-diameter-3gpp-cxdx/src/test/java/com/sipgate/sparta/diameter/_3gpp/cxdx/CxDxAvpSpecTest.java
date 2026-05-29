package com.sipgate.sparta.diameter._3gpp.cxdx;

import com.sipgate.sparta.diameter.base.AvpSpecTestBase;
import com.sipgate.sparta.diameter.base.core.avp.AVPDefinition;
import com.sipgate.sparta.diameter.spec.AvpDef;
import org.junit.jupiter.params.provider.Arguments;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Stream;

public class CxDxAvpSpecTest extends AvpSpecTestBase {

    private static final Set<AvpDef> AVP_DEFS = loadAvpDefs("/cxdx-avps.json");
    private static final Collection<AVPDefinition> DEFINITIONS = new CxDxAVPProvider().getDefinitions();

    static Stream<Arguments> provideAvpDefs() {
        return named(AVP_DEFS.stream());
    }

    @Override
    protected String mixinsPackage() {
        return "com.sipgate.sparta.diameter._3gpp.cxdx.mixins";
    }

    @Override
    protected Collection<AVPDefinition> getDefinitions() {
        return DEFINITIONS;
    }
}
