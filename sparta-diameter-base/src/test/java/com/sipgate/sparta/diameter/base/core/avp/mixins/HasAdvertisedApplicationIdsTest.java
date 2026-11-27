package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.messages.CapabilitiesExchangeRequest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class HasAdvertisedApplicationIdsTest {

    @Test
    void it_unions_auth_acct_and_vendor_specific_application_ids() {
        // GIVEN a CER as a peer sends it: top-level auth + acct, two vendor-specific groups
        final var cer = new CapabilitiesExchangeRequest.Out();
        cer.addAuthApplicationId(4L);
        cer.addAcctApplicationId(3L);
        cer.addVendorSpecificApplicationId(List.of(
                AVP.create(new AVPKey(DiameterConstants.AVP_VENDOR_ID, 0), 10415L),
                AVP.create(new AVPKey(DiameterConstants.AVP_AUTH_APPLICATION_ID, 0), 16777251L)));
        cer.addVendorSpecificApplicationId(List.of(
                AVP.create(new AVPKey(DiameterConstants.AVP_VENDOR_ID, 0), 10415L),
                AVP.create(new AVPKey(DiameterConstants.AVP_ACCT_APPLICATION_ID, 0), 16777216L)));

        // WHEN
        final var advertised = cer.getAdvertisedApplicationIds();

        // THEN
        assertThat(advertised).containsExactlyInAnyOrder(4L, 3L, 16777251L, 16777216L);
    }

    @Test
    void it_returns_an_immutable_empty_set_when_nothing_is_advertised() {
        // GIVEN
        final var cer = new CapabilitiesExchangeRequest.Out();

        // WHEN
        final var advertised = cer.getAdvertisedApplicationIds();

        // THEN
        assertThat(advertised).isEmpty();
        assertThat(advertised).isUnmodifiable();
    }
}
