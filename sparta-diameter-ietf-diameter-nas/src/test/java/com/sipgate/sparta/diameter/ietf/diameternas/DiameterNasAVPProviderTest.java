package com.sipgate.sparta.diameter.ietf.diameternas;

import com.sipgate.sparta.diameter.base.core.avp.AVPDefinition;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class DiameterNasAVPProviderTest {

    private final DiameterNasAVPProvider provider = new DiameterNasAVPProvider();

    @Test
    void it_defines_framed_avps_with_interface_id_as_unsigned64() {
        // GIVEN the RFC 7155 framed provider
        // WHEN definitions are collected by code
        final Map<Integer, AVPDefinition> byCode = provider.getDefinitions().stream()
            .collect(Collectors.toMap(AVPDefinition::code, Function.identity()));

        // THEN Framed-IP-Address/IPv6-Prefix are OctetString and Framed-Interface-Id is Unsigned64
        assertThat(byCode.keySet()).containsExactlyInAnyOrder(8, 96, 97);
        assertThat(byCode.get(8).dataType()).isEqualTo(byte[].class);
        assertThat(byCode.get(97).dataType()).isEqualTo(byte[].class);
        assertThat(byCode.get(96).dataType()).isEqualTo(BigInteger.class);
        assertThat(byCode.values()).allSatisfy(def -> assertThat(def.vendorId()).isZero());
    }
}
