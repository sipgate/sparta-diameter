package com.sipgate.sparta.diameter.core.avp;

import com.sipgate.sparta.diameter.core.DiameterConstants;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AVPFactoryTest {

    @Test
    void it_creates_integer_avp_with_correct_flags() {
        // GIVEN
        final int resultCode = DiameterConstants.RES_DIAMETER_SUCCESS;

        // WHEN
        final AVP avp = AVPFactory.create(DiameterConstants.AVP_RESULT_CODE, resultCode);

        // THEN
        assertThat(avp.getCode()).isEqualTo(DiameterConstants.AVP_RESULT_CODE);
        assertThat(avp.getDataAsInt()).isEqualTo(resultCode);
        assertThat(avp.isMandatory()).isTrue(); // Result-Code is mandatory
        assertThat(avp.isVendorSpecific()).isFalse();
        assertThat(avp.isProtected()).isFalse();
        assertThat(avp.getVendorId()).isEqualTo(0);
    }

    @Test
    void it_creates_string_avp_with_correct_flags() {
        // GIVEN
        final String hostname = "diameter.example.com";

        // WHEN
        final AVP avp = AVPFactory.create(DiameterConstants.AVP_ORIGIN_HOST, hostname);

        // THEN
        assertThat(avp.getCode()).isEqualTo(DiameterConstants.AVP_ORIGIN_HOST);
        assertThat(avp.getDataAsString()).isEqualTo(hostname);
        assertThat(avp.isMandatory()).isTrue(); // Origin-Host is mandatory
        assertThat(avp.isVendorSpecific()).isFalse();
        assertThat(avp.isProtected()).isFalse();
    }

    @Test
    void it_creates_optional_string_avp_correctly() {
        // GIVEN
        final String productName = "Sparta Diameter";

        // WHEN
        final AVP avp = AVPFactory.create(DiameterConstants.AVP_PRODUCT_NAME, productName);

        // THEN
        assertThat(avp.getCode()).isEqualTo(DiameterConstants.AVP_PRODUCT_NAME);
        assertThat(avp.getDataAsString()).isEqualTo(productName);
        assertThat(avp.isMandatory()).isFalse(); // Product-Name is optional
        assertThat(avp.isVendorSpecific()).isFalse();
    }

    @Test
    void it_creates_long_avp_correctly() {
        // GIVEN
        final long subSessionId = 123456789L;

        // WHEN
        final AVP avp = AVPFactory.create(DiameterConstants.AVP_ACCOUNTING_SUB_SESSION_ID, subSessionId);

        // THEN
        assertThat(avp.getCode()).isEqualTo(DiameterConstants.AVP_ACCOUNTING_SUB_SESSION_ID);
        assertThat(avp.isMandatory()).isFalse(); // Accounting-Sub-Session-Id is optional
        assertThat(avp.isVendorSpecific()).isFalse();
        // Note: We'd need a getDataAsLong() method in AVP to fully test this
    }

    @Test
    void it_creates_byte_array_avp_correctly() {
        // GIVEN
        final byte[] data = {0x01, 0x02, 0x03, 0x04};

        // WHEN
        final AVP avp = AVPFactory.create(DiameterConstants.AVP_CLASS, data);

        // THEN
        assertThat(avp.getCode()).isEqualTo(DiameterConstants.AVP_CLASS);
        assertThat(avp.getData()).isEqualTo(data);
        assertThat(avp.isMandatory()).isTrue(); // Class is mandatory
        assertThat(avp.isVendorSpecific()).isFalse();
    }

    @Test
    void it_throws_exception_for_unknown_avp_code() {
        // GIVEN
        final int unknownCode = 99999;

        // WHEN & THEN
        assertThatThrownBy(() -> AVPFactory.create(unknownCode, "test"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Unknown AVP code: " + unknownCode);
    }

    @Test
    void it_throws_exception_for_type_mismatch_integer_expected() {
        // GIVEN & WHEN & THEN
        assertThatThrownBy(() -> AVPFactory.create(DiameterConstants.AVP_RESULT_CODE, "not an integer"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Type mismatch")
            .hasMessageContaining("expected Integer")
            .hasMessageContaining("got String");
    }

    @Test
    void it_throws_exception_for_type_mismatch_string_expected() {
        // GIVEN & WHEN & THEN
        assertThatThrownBy(() -> AVPFactory.create(DiameterConstants.AVP_ORIGIN_HOST, 12345))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Type mismatch")
            .hasMessageContaining("expected String")
            .hasMessageContaining("got Integer");
    }

    @Test
    void it_allows_external_provider_registration() {
        // GIVEN
        final int customCode = 10001;
        final String customValue = "custom value";
        final AVPProvider customProvider = new AVPProvider() {
            @Override
            public Collection<AVPDefinition> getDefinitions() {
                return Arrays.asList(new AVPDefinition(customCode, "Custom-AVP", String.class, false, true, 12345));
            }

            @Override
            public String getProtocolName() {
                return "Test Protocol";
            }
        };

        // WHEN
        AVPFactory.registerProvider(customProvider);
        final AVP avp = AVPFactory.create(customCode, customValue);

        // THEN
        assertThat(avp.getCode()).isEqualTo(customCode);
        assertThat(avp.getDataAsString()).isEqualTo(customValue);
        assertThat(avp.isMandatory()).isFalse();
        assertThat(avp.isVendorSpecific()).isTrue();
        assertThat(avp.getVendorId()).isEqualTo(12345);
    }
}
