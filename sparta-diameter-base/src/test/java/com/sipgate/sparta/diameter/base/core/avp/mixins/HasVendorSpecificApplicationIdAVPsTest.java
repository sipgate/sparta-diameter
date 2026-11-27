package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.messages.CapabilitiesExchangeRequest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class HasVendorSpecificApplicationIdAVPsTest {

    @Test
    void it_accumulates_multiple_vendor_specific_application_ids() {
        // GIVEN
        final var cer = new CapabilitiesExchangeRequest.Out();

        // WHEN
        cer.addVendorSpecificApplicationId(List.of());
        cer.addVendorSpecificApplicationId(List.of());

        // THEN
        assertThat(cer.getVendorSpecificApplicationIds()).hasSize(2);
    }

    @Test
    void it_returns_empty_list_when_none_present() {
        // GIVEN
        final var cer = new CapabilitiesExchangeRequest.Out();

        // WHEN / THEN
        assertThat(cer.getVendorSpecificApplicationIds()).isEmpty();
    }

    @Test
    void it_returns_first_vendor_specific_application_id() {
        // GIVEN
        final var cer = new CapabilitiesExchangeRequest.Out();

        // WHEN
        cer.addVendorSpecificApplicationId(List.of());
        cer.addVendorSpecificApplicationId(List.of());

        // THEN
        assertThat(cer.getFirstVendorSpecificApplicationId()).isNotNull();
    }

    @Test
    void it_returns_null_for_first_when_empty() {
        // GIVEN
        final var cer = new CapabilitiesExchangeRequest.Out();

        // WHEN / THEN
        assertThat(cer.getFirstVendorSpecificApplicationId()).isNull();
    }

    @Test
    void it_returns_the_auth_and_acct_values_inside_vendor_specific_groups() {
        // GIVEN
        final var cer = new CapabilitiesExchangeRequest.Out();
        cer.addVendorSpecificApplicationId(List.of(
                AVP.create(new AVPKey(DiameterConstants.AVP_VENDOR_ID, 0), 10415L),
                AVP.create(new AVPKey(DiameterConstants.AVP_AUTH_APPLICATION_ID, 0), 16777251L)));
        cer.addVendorSpecificApplicationId(List.of(
                AVP.create(new AVPKey(DiameterConstants.AVP_ACCT_APPLICATION_ID, 0), 3L)));

        // WHEN
        final List<Long> values = cer.getVendorSpecificApplicationIdValues();

        // THEN the Vendor-Id is informational and not part of the result
        assertThat(values).containsExactly(16777251L, 3L);
    }

    @Test
    void it_returns_no_vendor_specific_values_without_groups() {
        // GIVEN
        final var cer = new CapabilitiesExchangeRequest.Out();

        // WHEN / THEN
        assertThat(cer.getVendorSpecificApplicationIdValues()).isEmpty();
    }

    @Test
    void it_adds_all_vendor_specific_application_ids_from_collection() {
        // GIVEN
        final var cer = new CapabilitiesExchangeRequest.Out();

        // WHEN
        cer.addAllVendorSpecificApplicationIds(List.of(List.of(), List.of()));

        // THEN
        assertThat(cer.getVendorSpecificApplicationIds()).hasSize(2);
    }
}
