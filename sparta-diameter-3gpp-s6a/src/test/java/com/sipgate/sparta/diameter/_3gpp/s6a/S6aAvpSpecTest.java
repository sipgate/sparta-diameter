package com.sipgate.sparta.diameter._3gpp.s6a;

import com.sipgate.sparta.diameter.base.AvpSpecTestBase;
import com.sipgate.sparta.diameter.base.core.avp.AVPDefinition;
import com.sipgate.sparta.diameter.spec.AvpDef;
import org.junit.jupiter.params.provider.Arguments;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Stream;

public class S6aAvpSpecTest extends AvpSpecTestBase {

    private static final Set<AvpDef> AVP_DEFS = loadAvpDefs("/s6as6d-s7as7d-s13-avps.json");
    private static final Collection<AVPDefinition> DEFINITIONS = new S6aAVPProvider().getDefinitions();

    static Stream<Arguments> provideAvpDefs() {
        return named(AVP_DEFS.stream());
    }

    @Override
    protected String mixinsPackage() {
        return "com.sipgate.sparta.diameter._3gpp.s6a.mixins";
    }

    @Override
    protected int exampleEnumValueFor(final AvpDef def) {
        return switch (def.attributeName()) {
            case "Cancellation-Type" -> S6aConstants.CANCELLATION_TYPE_SUBSCRIPTION_WITHDRAWAL;
            case "Alert-Reason" -> S6aConstants.ALERT_REASON_UE_PRESENT;
            case "Error-Diagnostic" -> S6aConstants.ERROR_DIAGNOSTIC_GPRS_DATA_SUBSCRIBED;
            default -> super.exampleEnumValueFor(def);
        };
    }

    @Override
    protected Collection<AVPDefinition> getDefinitions() {
        return DEFINITIONS;
    }
}
