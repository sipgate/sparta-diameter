package com.sipgate.sparta.diameter._3gpp.cxdx;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVPDefinition;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class CxDxAVPProviderTest {

    private final CxDxAVPProvider provider = new CxDxAVPProvider();

    private Map<Long, AVPDefinition> byCode() {
        return provider.getDefinitions().stream()
            .collect(Collectors.toMap(AVPDefinition::code, Function.identity()));
    }

    @Test
    void it_defines_all_53_cxdx_avps_as_3gpp_vendor() {
        // GIVEN the Cx/Dx provider
        // WHEN definitions are collected
        final var defs = provider.getDefinitions();

        // THEN there are 53 definitions, all vendor 3GPP
        assertThat(defs).hasSize(53);
        assertThat(defs).allSatisfy(def -> {
            assertThat(def.vendorSpecific()).isTrue();
            assertThat(def.vendorId()).isEqualTo(_3gppConstants.VENDOR_ID_3GPP);
        });
    }

    @Test
    void it_maps_representative_avps_to_their_spec_types_and_flags() {
        // GIVEN the definitions by code
        final Map<Long, AVPDefinition> byCode = byCode();

        // THEN types and M-bit match TS 29.229 Table 6.3.0.1
        assertThat(byCode.get(601).dataType()).isEqualTo(String.class);   // Public-Identity UTF8String
        assertThat(byCode.get(601).mandatory()).isTrue();                 // M,V
        assertThat(byCode.get(606).dataType()).isEqualTo(byte[].class);   // User-Data OctetString
        assertThat(byCode.get(612).dataType()).isEqualTo(GroupedAVP.class); // SIP-Auth-Data-Item
        assertThat(byCode.get(614).dataType()).isEqualTo(Integer.class);  // Server-Assignment-Type Enumerated
        assertThat(byCode.get(619).dataType()).isEqualTo(String.class);   // charging fn name DiameterURI
        assertThat(byCode.get(655).dataType()).isEqualTo(Long.class);     // SAR-Flags Unsigned32
        assertThat(byCode.get(655).mandatory()).isFalse();                // V only
        assertThat(byCode.get(661).dataType()).isEqualTo(Date.class);     // Registration-Time-Out Time
        assertThat(byCode.get(665).dataType()).isEqualTo(String.class);   // PCSCF-FQDN DiameterIdentity
        assertThat(byCode.get(666).dataType()).isEqualTo(java.net.InetAddress.class); // PCSCF-IP-Address Address
    }
}
