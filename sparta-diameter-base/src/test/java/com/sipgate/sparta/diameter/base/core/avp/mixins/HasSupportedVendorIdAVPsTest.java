package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.messages.CapabilitiesExchangeRequest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class HasSupportedVendorIdAVPsTest {

    @Test
    void it_accumulates_multiple_supported_vendor_ids() {
        // GIVEN
        final var cer = new CapabilitiesExchangeRequest.Out();

        // WHEN
        cer.addSupportedVendorId(10415L);
        cer.addSupportedVendorId(13019L);

        // THEN
        assertThat(cer.getSupportedVendorIds()).containsExactly(10415L, 13019L);
    }

    @Test
    void it_returns_empty_list_when_no_supported_vendor_id_present() {
        // GIVEN
        final var cer = new CapabilitiesExchangeRequest.Out();

        // WHEN / THEN
        assertThat(cer.getSupportedVendorIds()).isEmpty();
    }

    @Test
    void it_returns_first_supported_vendor_id() {
        // GIVEN
        final var cer = new CapabilitiesExchangeRequest.Out();

        // WHEN
        cer.addSupportedVendorId(10415L);
        cer.addSupportedVendorId(13019L);

        // THEN
        assertThat(cer.getFirstSupportedVendorId()).isEqualTo(10415L);
    }

    @Test
    void it_returns_null_for_first_when_empty() {
        // GIVEN
        final var cer = new CapabilitiesExchangeRequest.Out();

        // WHEN / THEN
        assertThat(cer.getFirstSupportedVendorId()).isNull();
    }

    @Test
    void it_adds_all_supported_vendor_ids_from_collection() {
        // GIVEN
        final var cer = new CapabilitiesExchangeRequest.Out();

        // WHEN
        cer.addAllSupportedVendorIds(List.of(10415L, 13019L, 5535L));

        // THEN
        assertThat(cer.getSupportedVendorIds()).containsExactly(10415L, 13019L, 5535L);
    }
}
