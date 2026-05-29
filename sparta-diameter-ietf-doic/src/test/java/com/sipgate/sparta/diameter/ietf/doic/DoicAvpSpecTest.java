package com.sipgate.sparta.diameter.ietf.doic;

import com.sipgate.sparta.diameter.base.AvpSpecTestBase;
import com.sipgate.sparta.diameter.base.core.avp.AVPDefinition;
import com.sipgate.sparta.diameter.spec.AvpDef;
import com.sipgate.sparta.diameter.spec.AvpRfcTableParser;
import org.junit.jupiter.params.provider.Arguments;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Stream;

public class DoicAvpSpecTest extends AvpSpecTestBase {

    /// from RFC 7683, §7.8 AVP Flag Rules
    private static final Set<AvpDef> AVP_DEFS = AvpRfcTableParser.parse("""
        --------------------------------------------------+----+----+
        OC-Supported-Features  621   7.1      Grouped     |    | V  |
        --------------------------------------------------+----+----+
        OC-Feature-Vector      622   7.2      Unsigned64  |    | V  |
        --------------------------------------------------+----+----+
        OC-OLR                 623   7.3      Grouped     |    | V  |
        --------------------------------------------------+----+----+
        OC-Sequence-Number     624   7.4      Unsigned64  |    | V  |
        --------------------------------------------------+----+----+
        OC-Validity-Duration   625   7.5      Unsigned32  |    | V  |
        --------------------------------------------------+----+----+
        OC-Report-Type         626   7.6      Enumerated  |    | V  |
        --------------------------------------------------+----+----+
        OC-Reduction                                      |    |    |
          -Percentage          627   7.7      Unsigned32  |    | V  |
        --------------------------------------------------+----+----+
        """);

    private static final Collection<AVPDefinition> DEFINITIONS = new DoicAVPProvider().getDefinitions();

    static Stream<Arguments> provideAvpDefs() {
        return named(AVP_DEFS.stream());
    }

    @Override
    protected String mixinsPackage() {
        return "com.sipgate.sparta.diameter.ietf.doic.mixins";
    }

    @Override
    protected Collection<AVPDefinition> getDefinitions() {
        return DEFINITIONS;
    }
}
