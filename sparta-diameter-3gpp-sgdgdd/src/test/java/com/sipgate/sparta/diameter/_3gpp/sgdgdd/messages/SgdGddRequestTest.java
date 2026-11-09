package com.sipgate.sparta.diameter._3gpp.sgdgdd.messages;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.common._3gppRequest;
import com.sipgate.sparta.diameter._3gpp.sgdgdd.SgdGddConstants;
import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.OutgoingRequest;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.nio.ByteBuffer;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SgdGddRequestTest {

    private static final int FIRST_AVP_OFFSET = 20;

    @Test
    void it_carries_the_vendor_specific_application_id_in_an_outgoing_ofr_and_tfr() {
        // GIVEN
        final List<_3gppRequest> requests = List.of(
            new MoForwardShortMessageRequest.Out(),
            new MtForwardShortMessageRequest.Out());

        for (final var request : requests) {
            // WHEN
            final var vendorSpecificApplicationId = request.getVendorSpecificApplicationId();

            // THEN the OFR/TFR ABNFs require it (TS 29.338 §6.3.2.3, §6.3.2.5)
            assertThat(vendorSpecificApplicationId).as("Vendor-Specific-Application-Id of %s", request).isNotNull();
            assertThat(vendorSpecificApplicationId.findAVP(new AVPKey(DiameterConstants.AVP_VENDOR_ID, 0)).getDataAsUnsignedInt())
                .isEqualTo(_3gppConstants.VENDOR_ID_3GPP);
            assertThat(vendorSpecificApplicationId.findAVP(new AVPKey(DiameterConstants.AVP_AUTH_APPLICATION_ID, 0)).getDataAsUnsignedInt())
                .isEqualTo(SgdGddConstants.APP_ID_SGD_GDD);
        }
    }

    @Test
    void it_keeps_the_session_id_first_when_it_is_set_on_a_new_ofr_and_tfr() throws Exception {
        // GIVEN
        final List<OutgoingRequest<?>> requests = List.of(
            new MoForwardShortMessageRequest.Out(),
            new MtForwardShortMessageRequest.Out());

        for (final var request : requests) {
            // WHEN
            ((_3gppRequest) request).setSessionId("host;1;2");
            final var bytes = new ByteArrayOutputStream();
            request.writeTo(new DataOutputStream(bytes), new HopByHopId(1), new EndToEndId(2));

            // THEN RFC 6733 section 8.8: Session-Id follows the header directly; the first AVP code is at byte 20
            assertThat(ByteBuffer.wrap(bytes.toByteArray()).getInt(FIRST_AVP_OFFSET))
                .as("first AVP of %s", request)
                .isEqualTo(DiameterConstants.AVP_SESSION_ID);
        }
    }
}
