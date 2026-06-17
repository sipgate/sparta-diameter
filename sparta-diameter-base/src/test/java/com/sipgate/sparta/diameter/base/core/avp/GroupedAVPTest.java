package com.sipgate.sparta.diameter.base.core.avp;

import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class GroupedAVPTest {
    private static final int AVP_CODE = 1337;
    private static final int VENDOR_ID = 4711;

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void it_honors_all_constructor_args_when_vendor_id_given(final boolean mandatory) {
        // GIVEN
        final AVP child = mock(AVP.class);

        // WHEN
        final var underTest = new GroupedAVP(new AVPKey(AVP_CODE, VENDOR_ID), mandatory, List.of(child));

        // THEN
        assertThat(underTest.getCode()).isEqualTo(AVP_CODE);
        assertThat(underTest.isVendorSpecific()).isTrue();
        assertThat(underTest.isMandatory()).isEqualTo(mandatory);
        assertThat(underTest.isProtected()).isFalse();
        assertThat(underTest.getVendorId()).isEqualTo(VENDOR_ID);
        assertThat(underTest.getAVPs()).containsExactly(child);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void it_honors_all_constructor_args_when_vendor_id_is_zero(final boolean mandatory) {
        // GIVEN
        final var vendorId = 0;
        final AVP child = mock(AVP.class);

        // WHEN
        final var underTest = new GroupedAVP(new AVPKey(AVP_CODE, vendorId), mandatory, List.of(child));

        // THEN
        assertThat(underTest.getCode()).isEqualTo(AVP_CODE);
        assertThat(underTest.isVendorSpecific()).isFalse();
        assertThat(underTest.isMandatory()).isEqualTo(mandatory);
        assertThat(underTest.isProtected()).isFalse();
        assertThat(underTest.getVendorId()).isEqualTo(vendorId);
        assertThat(underTest.getAVPs()).containsExactly(child);
    }

    @Test
    void it_serializes_avps_that_are_added_after_constructor() throws IOException, AVPParseException {
        // GIVEN: no AVP in constructor
        final var groupedAvp = new GroupedAVP(new AVPKey(DiameterConstants.AVP_PROXY_INFO, 0), false, List.of());

        // WHEN: child AVP is added
        final var avp = new AVP(0, false, new byte[]{'a', 'v', 'p'});
        groupedAvp.addAVP(avp);

        // WHEN: grouped avp is encoded
        final var baos = new ByteArrayOutputStream();
        groupedAvp.writeTo(new DataOutputStream(baos));

        // THEN: child AVP is decoded
        final var decodedGroupedAvp = (GroupedAVP) AVP.readFrom(ByteBuffer.wrap(baos.toByteArray()));
        assertThat(decodedGroupedAvp.getAVPs()).usingRecursiveFieldByFieldElementComparator().containsExactly(avp);
    }
}
