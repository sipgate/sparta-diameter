package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;
import com.sipgate.sparta.diameter.base.messages.CapabilitiesExchangeRequest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class HasVendorSpecificApplicationIdAVPsTest {

    private static GroupedAVP vsai(final int appId) {
        return new GroupedAVP(DiameterConstants.AVP_VENDOR_SPECIFIC_APPLICATION_ID, true,
                List.of());
    }

    @Test
    void it_accumulates_multiple_vendor_specific_application_ids() {
        // GIVEN
        final var cer = new CapabilitiesExchangeRequest.Out();
        final var vsai1 = vsai(1);
        final var vsai2 = vsai(2);

        // WHEN
        cer.addVendorSpecificApplicationId(vsai1);
        cer.addVendorSpecificApplicationId(vsai2);

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
        final var vsai1 = vsai(1);
        final var vsai2 = vsai(2);

        // WHEN
        cer.addVendorSpecificApplicationId(vsai1);
        cer.addVendorSpecificApplicationId(vsai2);

        // THEN
        assertThat(cer.getFirstVendorSpecificApplicationId()).isSameAs(vsai1);
    }

    @Test
    void it_returns_null_for_first_when_empty() {
        // GIVEN
        final var cer = new CapabilitiesExchangeRequest.Out();

        // WHEN / THEN
        assertThat(cer.getFirstVendorSpecificApplicationId()).isNull();
    }

    @Test
    void it_adds_all_vendor_specific_application_ids_from_collection() {
        // GIVEN
        final var cer = new CapabilitiesExchangeRequest.Out();
        final var vsai1 = vsai(1);
        final var vsai2 = vsai(2);

        // WHEN
        cer.addAllVendorSpecificApplicationIds(List.of(vsai1, vsai2));

        // THEN
        assertThat(cer.getVendorSpecificApplicationIds()).hasSize(2);
    }
}
