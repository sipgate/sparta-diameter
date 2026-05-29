package com.sipgate.sparta.diameter._3gpp.s6a;

import com.sipgate.sparta.diameter.base.core.avp.AVPDefinition;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class S6aAVPProviderTest {

    @Test
    void it_has_a_definition_for_every_avp_constant() throws IllegalAccessException {
        // GIVEN
        final var provider = new S6aAVPProvider();
        final var registeredCodes = provider.getDefinitions().stream()
            .map(AVPDefinition::code)
            .collect(Collectors.toUnmodifiableSet());

        // WHEN
        final var missing = new ArrayList<String>();
        for (final var field : S6aConstants.class.getDeclaredFields()) {
            if (!field.getName().startsWith("AVP_") || field.getType() != int.class) {
                continue;
            }
            if (!Modifier.isStatic(field.getModifiers()) || !Modifier.isFinal(field.getModifiers())) {
                continue;
            }
            final int code = field.getInt(null);
            if (!registeredCodes.contains(code)) {
                missing.add(field.getName() + " (code=" + code + ")");
            }
        }

        // THEN
        assertThat(missing)
            .as("AVP_* constants in S6aConstants without a definition in S6aAVPProvider")
            .isEmpty();
    }

    @Test
    void it_defines_representative_avps_with_correct_type_and_flags() {
        // GIVEN
        final var provider = new S6aAVPProvider();
        final Function<Integer, AVPDefinition> byCode = code -> provider.getDefinitions().stream()
            .filter(d -> d.code() == code)
            .findFirst().orElseThrow();

        // WHEN / THEN — one per type, plus the V-only AVPs and the M,V Alert-Reason (must register)
        assertDefinition(byCode.apply(1400), "Subscription-Data", GroupedAVP.class, true, true);
        assertDefinition(byCode.apply(1402), "IMEI", String.class, true, true);
        assertDefinition(byCode.apply(1405), "ULR-Flags", Long.class, true, true);
        assertDefinition(byCode.apply(1407), "Visited-PLMN-Id", byte[].class, true, true);
        assertDefinition(byCode.apply(1420), "Cancellation-Type", Integer.class, true, true);
        assertDefinition(byCode.apply(1434), "Alert-Reason", Integer.class, true, true);
        // V-only AVPs (M-bit must NOT be set)
        assertDefinition(byCode.apply(1613), "SIPTO-Permission", Integer.class, false, true);
        assertDefinition(byCode.apply(1615), "UE-SRVCC-Capability", Integer.class, false, true);
        assertDefinition(byCode.apply(1618), "LIPA-Permission", Integer.class, false, true);
        assertDefinition(byCode.apply(1638), "CLR-Flags", Long.class, false, true);
    }

    private static void assertDefinition(final AVPDefinition definition, final String name,
                                         final Class<?> type, final boolean mandatory,
                                         final boolean vendorSpecific) {
        assertThat(definition.name()).isEqualTo(name);
        assertThat(definition.dataType()).isEqualTo(type);
        assertThat(definition.mandatory()).as("%s mandatory", name).isEqualTo(mandatory);
        assertThat(definition.vendorSpecific()).as("%s vendorSpecific", name).isEqualTo(vendorSpecific);
        assertThat(definition.vendorId()).as("%s vendorId", name).isEqualTo(10415);
    }
}
