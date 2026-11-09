package com.sipgate.sparta.diameter._3gpp.gx;

import com.sipgate.sparta.diameter.base.core.avp.AVPDefinition;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class GxAVPProviderTest {

    private static final long VENDOR_3GPP = 10415L;

    @Test
    void it_has_a_definition_for_every_avp_constant() throws IllegalAccessException {
        // GIVEN
        final var registeredCodes = new GxAVPProvider().getDefinitions().stream()
            .map(AVPDefinition::code)
            .collect(Collectors.toUnmodifiableSet());

        // WHEN
        final var missing = new ArrayList<String>();
        for (final var field : GxConstants.class.getDeclaredFields()) {
            if (!field.getName().startsWith("AVP_") || field.getType() != int.class) {
                continue;
            }
            if (!Modifier.isStatic(field.getModifiers()) || !Modifier.isFinal(field.getModifiers())) {
                continue;
            }
            if (!registeredCodes.contains(field.getLong(null))) {
                missing.add(field.getName());
            }
        }

        // THEN
        assertThat(missing).as("AVP_* constants in GxConstants without a definition").isEmpty();
    }

    @Test
    void it_defines_the_avps_that_open5gs_sends_in_gx_messages() {
        // GIVEN
        final Collection<AVPDefinition> definitions = new GxAVPProvider().getDefinitions();

        // WHEN/THEN codes, names, types and flags match the Rx, S6a, SWx and Gy definitions of the same AVPs
        assertDefined(definitions, 13, "3GPP-Charging-Characteristics", String.class, false);
        assertDefined(definitions, 18, "3GPP-SGSN-MCC-MNC", String.class, false);
        assertDefined(definitions, 22, "3GPP-User-Location-Info", byte[].class, false);
        assertDefined(definitions, 23, "3GPP-MS-TimeZone", byte[].class, false);
        assertDefined(definitions, 501, "Access-Network-Charging-Address", InetAddress.class, true);
        assertDefined(definitions, 503, "Access-Network-Charging-Identifier-Value", byte[].class, true);
        assertDefined(definitions, 507, "Flow-Description", String.class, true);
        assertDefined(definitions, 511, "Flow-Status", Integer.class, true);
        assertDefined(definitions, 515, "Max-Requested-Bandwidth-DL", Long.class, true);
        assertDefined(definitions, 516, "Max-Requested-Bandwidth-UL", Long.class, true);
        assertDefined(definitions, 1040, "APN-Aggregate-Max-Bitrate-DL", Long.class, false);
        assertDefined(definitions, 1041, "APN-Aggregate-Max-Bitrate-UL", Long.class, false);
        assertDefined(definitions, 1049, "Default-EPS-Bearer-QoS", GroupedAVP.class, false);
        assertDefined(definitions, 1050, "AN-GW-Address", InetAddress.class, false);
        assertDefined(definitions, 1058, "Flow-Information", GroupedAVP.class, false);
        assertDefined(definitions, 1080, "Flow-Direction", Integer.class, false);
        assertDefined(definitions, 1503, "AN-Trusted", Integer.class, false);
    }

    private static void assertDefined(
        final Collection<AVPDefinition> definitions, final int code, final String name,
        final Class<?> dataType, final boolean mandatory) {
        final var expected = new AVPDefinition(code, name, dataType, mandatory, true, VENDOR_3GPP);
        assertThat(definitions).as(name).contains(expected);
    }
}
