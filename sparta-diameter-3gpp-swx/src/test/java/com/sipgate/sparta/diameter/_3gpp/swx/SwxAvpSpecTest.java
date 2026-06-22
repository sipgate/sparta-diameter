package com.sipgate.sparta.diameter._3gpp.swx;

import com.sipgate.sparta.diameter.base.AvpSpecTestBase;
import com.sipgate.sparta.diameter.base.core.avp.AVPDefinition;
import com.sipgate.sparta.diameter.ietf.mip6.integrated.Mip6IntegratedAVPProvider;
import com.sipgate.sparta.diameter.spec.AvpDef;
import org.junit.jupiter.params.provider.Arguments;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class SwxAvpSpecTest extends AvpSpecTestBase {

    private static final Set<AvpDef> AVP_DEFS = loadAvpDefs("/swx-avps.json");
    private static final Collection<AVPDefinition> DEFINITIONS = buildDefinitions();

    private static Collection<AVPDefinition> buildDefinitions() {
        final List<AVPDefinition> all = new ArrayList<>();
        for (final AVPDefinition d : new SwxAVPProvider().getDefinitions()) {
            all.add(d);
        }
        for (final AVPDefinition d : new Mip6IntegratedAVPProvider().getDefinitions()) {
            all.add(d);
        }
        return all;
    }

    static Stream<Arguments> provideAvpDefs() {
        return named(AVP_DEFS.stream());
    }

    @Override
    protected String mixinsPackage() {
        return "com.sipgate.sparta.diameter._3gpp.swx.mixins";
    }

    @Override
    protected Collection<AVPDefinition> getDefinitions() {
        return DEFINITIONS;
    }
}
