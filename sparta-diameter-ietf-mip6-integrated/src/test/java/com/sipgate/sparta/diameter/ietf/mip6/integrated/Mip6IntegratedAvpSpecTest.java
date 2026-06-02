package com.sipgate.sparta.diameter.ietf.mip6.integrated;

import com.sipgate.sparta.diameter.base.AvpSpecTestBase;
import com.sipgate.sparta.diameter.base.core.avp.AVPDefinition;
import com.sipgate.sparta.diameter.spec.AvpDef;
import com.sipgate.sparta.diameter.spec.AvpRfcTableParser;
import org.junit.jupiter.params.provider.Arguments;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Stream;

public class Mip6IntegratedAvpSpecTest extends AvpSpecTestBase {

    /// from RFC 5778, §6 AVP Flag Rules
    private static final Set<AvpDef> AVP_DEFS = AvpRfcTableParser.parse("""
        ------------------------------------------+----+---+------+----+----+
        Service-        493  6.2       UTF8String |  M | P |      | V  | Y  |
          Selection                               |    |   |      |    |    |
        ------------------------------------------+----+---+------+----+----+
        """);

    private static final Collection<AVPDefinition> DEFINITIONS = new Mip6IntegratedAVPProvider().getDefinitions();

    static Stream<Arguments> provideAvpDefs() {
        return named(AVP_DEFS.stream());
    }

    @Override
    protected String mixinsPackage() {
        return "com.sipgate.sparta.diameter.ietf.mip6.integrated.mixins";
    }

    @Override
    protected Collection<AVPDefinition> getDefinitions() {
        return DEFINITIONS;
    }
}
