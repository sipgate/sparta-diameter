package com.sipgate.sparta.diameter.ietf.radiusdigestauthentication;

import com.sipgate.sparta.diameter.base.core.avp.AVPDefinition;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class RadiusDigestAuthenticationAVPProviderTest {

    private final RadiusDigestAuthenticationAVPProvider provider = new RadiusDigestAuthenticationAVPProvider();

    @Test
    void it_defines_the_four_digest_avps_as_vendor_zero_utf8_strings() {
        // GIVEN the RFC 5090 digest provider
        // WHEN the definitions are collected by code
        final Map<Integer, AVPDefinition> byCode = provider.getDefinitions().stream()
            .collect(Collectors.toMap(AVPDefinition::code, Function.identity()));

        // THEN all four digest AVPs are present, UTF8String, vendor 0, mandatory
        assertThat(byCode.keySet()).containsExactlyInAnyOrder(104, 110, 111, 121);
        assertThat(byCode.values()).allSatisfy(def -> {
            assertThat(def.dataType()).isEqualTo(String.class);
            assertThat(def.vendorSpecific()).isFalse();
            assertThat(def.vendorId()).isZero();
            assertThat(def.mandatory()).isTrue();
        });
        assertThat(byCode.get(104).name()).isEqualTo("Digest-Realm");
    }
}
