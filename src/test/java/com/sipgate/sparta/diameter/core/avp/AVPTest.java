package com.sipgate.sparta.diameter.core.avp;

import com.sipgate.sparta.diameter.core.DiameterConstants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class AVPTest {

    @Test
    void it_creates_integer_avp_with_correct_flags() {
        // GIVEN
        final long resultCode = DiameterConstants.RES_DIAMETER_SUCCESS; // Result-Code is Unsigned32 (Long in Java)

        // WHEN
        final AVP avp = AVP.create(DiameterConstants.AVP_RESULT_CODE, resultCode);

        // THEN
        assertThat(avp.getCode()).isEqualTo(DiameterConstants.AVP_RESULT_CODE);
        assertThat(avp.getDataAsUnsignedInt()).isEqualTo(resultCode);
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
        final AVP avp = AVP.create(DiameterConstants.AVP_ORIGIN_HOST, hostname);

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
        final AVP avp = AVP.create(DiameterConstants.AVP_PRODUCT_NAME, productName);

        // THEN
        assertThat(avp.getCode()).isEqualTo(DiameterConstants.AVP_PRODUCT_NAME);
        assertThat(avp.getDataAsString()).isEqualTo(productName);
        assertThat(avp.isMandatory()).isFalse(); // Product-Name is optional
        assertThat(avp.isVendorSpecific()).isFalse();
    }

    @Test
    void it_creates_a_uint64_avp_correctly() {
        // GIVEN - Using BigInteger since Accounting-Sub-Session-Id is Unsigned64 in RFC 6733
        final BigInteger subSessionId = BigInteger.valueOf(123456789L);

        // WHEN
        final AVP avp = AVP.create(DiameterConstants.AVP_ACCOUNTING_SUB_SESSION_ID, subSessionId);

        // THEN
        assertThat(avp.getCode()).isEqualTo(DiameterConstants.AVP_ACCOUNTING_SUB_SESSION_ID);
        assertThat(avp.getDataAsUnsignedLong()).isEqualTo(subSessionId);
        assertThat(avp.isMandatory()).isTrue(); // Accounting-Sub-Session-Id is mandatory
        assertThat(avp.isVendorSpecific()).isFalse();
    }

    @Test
    void it_creates_enumerated_avp_correctly() {
        // GIVEN
        final Integer disconnectCause = DiameterConstants.DCC_REBOOTING;

        // WHEN
        final AVP avp = AVP.create(DiameterConstants.AVP_DISCONNECT_CAUSE, disconnectCause);

        // THEN
        assertThat(avp.getCode()).isEqualTo(DiameterConstants.AVP_DISCONNECT_CAUSE);
        assertThat(avp.getDataAsEnumerated()).isEqualTo(disconnectCause);
        assertThat(avp.isMandatory()).isTrue(); // Disconnect-Cause is mandatory
        assertThat(avp.isVendorSpecific()).isFalse();
    }

    @Test
    void it_creates_ip_address_avp_correctly() throws Exception {
        // GIVEN
        final InetAddress ipAddress = InetAddress.getByName("192.168.1.1");

        // WHEN
        final AVP avp = AVP.create(DiameterConstants.AVP_HOST_IP_ADDRESS, ipAddress);

        // THEN
        assertThat(avp.getCode()).isEqualTo(DiameterConstants.AVP_HOST_IP_ADDRESS);
        assertThat(avp.getDataAsIPAddress()).isEqualTo(ipAddress);
        assertThat(avp.isMandatory()).isTrue(); // Host-IP-Address is mandatory
        assertThat(avp.isVendorSpecific()).isFalse();
    }

    @Test
    void it_creates_time_avp_correctly() {
        // GIVEN
        final Date eventTime = new Date();

        // WHEN
        final AVP avp = AVP.create(DiameterConstants.AVP_EVENT_TIMESTAMP, eventTime);

        // THEN
        assertThat(avp.getCode()).isEqualTo(DiameterConstants.AVP_EVENT_TIMESTAMP);
        // Time precision is only to the second in Diameter Time format (NTP timestamp)
        assertThat(avp.getDataAsTime().getTime() / 1000).isEqualTo(eventTime.getTime() / 1000);
        assertThat(avp.isMandatory()).isTrue(); // Event-Timestamp is mandatory
        assertThat(avp.isVendorSpecific()).isFalse();
    }

    @Test
    void it_creates_ipv6_address_avp_correctly() throws Exception {
        // GIVEN
        final InetAddress ipv6Address = InetAddress.getByName("2001:db8::1");

        // WHEN
        final AVP avp = AVP.create(DiameterConstants.AVP_HOST_IP_ADDRESS, ipv6Address);

        // THEN
        assertThat(avp.getCode()).isEqualTo(DiameterConstants.AVP_HOST_IP_ADDRESS);
        assertThat(avp.getDataAsIPAddress()).isEqualTo(ipv6Address);
        assertThat(avp.isMandatory()).isTrue();
        assertThat(avp.isVendorSpecific()).isFalse();
    }

    @Test
    void it_throws_exception_for_unknown_avp_code() {
        // GIVEN
        final int unknownCode = 99999;

        // WHEN & THEN
        assertThatThrownBy(() -> AVP.create(unknownCode, "test"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Unknown AVP code: " + unknownCode);
    }

    @ParameterizedTest
    @MethodSource("typeMismatchTestCases")
    void it_throws_exception_for_type_mismatch(final String testName, final String expectedType, final String actualType) {
        // GIVEN & WHEN & THEN - Test specific type mismatch scenarios
        switch (testName) {
            case "Long expected, String provided":
                assertThatThrownBy(() -> AVP.create(DiameterConstants.AVP_RESULT_CODE, "wrong type"))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("Type mismatch")
                        .hasMessageContaining("expected " + expectedType)
                        .hasMessageContaining("got " + actualType);
                break;
            case "String expected, Long provided":
                assertThatThrownBy(() -> AVP.create(DiameterConstants.AVP_ORIGIN_HOST, 12345L))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("Type mismatch")
                        .hasMessageContaining("expected " + expectedType)
                        .hasMessageContaining("got " + actualType);
                break;
            case "BigInteger expected, String provided":
                assertThatThrownBy(() -> AVP.create(DiameterConstants.AVP_ACCOUNTING_SUB_SESSION_ID, "wrong type"))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("Type mismatch")
                        .hasMessageContaining("expected " + expectedType)
                        .hasMessageContaining("got " + actualType);
                break;
            case "InetAddress expected, String provided":
                assertThatThrownBy(() -> AVP.create(DiameterConstants.AVP_HOST_IP_ADDRESS, "wrong type"))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("Type mismatch")
                        .hasMessageContaining("expected " + expectedType)
                        .hasMessageContaining("got " + actualType);
                break;
            case "byte[] expected, Integer provided":
                // Use explicit Integer to ensure correct method resolution
                final Integer wrongValue = 12345;
                assertThatThrownBy(() -> AVP.create(DiameterConstants.AVP_CLASS, wrongValue))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("Type mismatch")
                        .hasMessageContaining("expected " + expectedType)
                        .hasMessageContaining("got " + actualType);
                break;
            case "Integer expected, Long provided":
                assertThatThrownBy(() -> AVP.create(DiameterConstants.AVP_DISCONNECT_CAUSE, 123L))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("Type mismatch")
                        .hasMessageContaining("expected " + expectedType)
                        .hasMessageContaining("got " + actualType);
                break;
            case "Date expected, String provided":
                assertThatThrownBy(() -> AVP.create(DiameterConstants.AVP_EVENT_TIMESTAMP, "wrong type"))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("Type mismatch")
                        .hasMessageContaining("expected " + expectedType)
                        .hasMessageContaining("got " + actualType);
                break;
        }
    }

    private static Stream<Arguments> typeMismatchTestCases() {
        return Stream.of(
            arguments("Long expected, String provided", "Long", "String"),
            arguments("String expected, Long provided", "String", "Long"),
            arguments("BigInteger expected, String provided", "BigInteger", "String"),
            arguments("InetAddress expected, String provided", "InetAddress", "String"),
            arguments("byte[] expected, Integer provided", "byte[]", "Integer"),
            arguments("Integer expected, Long provided", "Integer", "Long"),
            arguments("Date expected, String provided", "Date", "String")
        );
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
        AVP.registerProvider(customProvider);
        final AVP avp = AVP.create(customCode, customValue);

        // THEN
        assertThat(avp.getCode()).isEqualTo(customCode);
        assertThat(avp.getDataAsString()).isEqualTo(customValue);
        assertThat(avp.isMandatory()).isFalse();
        assertThat(avp.isVendorSpecific()).isTrue();
        assertThat(avp.getVendorId()).isEqualTo(12345);
    }

    @Test
    void it_creates_grouped_avp_with_correct_flags() {
        // GIVEN
        final List<AVP> nestedAvps = Arrays.asList(
            AVP.create(DiameterConstants.AVP_VENDOR_ID, 123L),
            AVP.create(DiameterConstants.AVP_AUTH_APPLICATION_ID, 456L)
        );

        // WHEN
        final AVP avp = AVP.create(DiameterConstants.AVP_VENDOR_SPECIFIC_APPLICATION_ID, nestedAvps);

        // THEN
        assertThat(avp.getCode()).isEqualTo(DiameterConstants.AVP_VENDOR_SPECIFIC_APPLICATION_ID);
        assertThat(avp.isMandatory()).isTrue(); // Vendor-Specific-Application-Id is mandatory
        assertThat(avp.isVendorSpecific()).isFalse();
        assertThat(avp.isProtected()).isFalse();
        assertThat(avp).isInstanceOf(GroupedAVP.class);
        
        final GroupedAVP groupedAVP = (GroupedAVP) avp;
        assertThat(groupedAVP.getAVPs()).hasSize(2);
    }

    @Test
    void it_creates_experimental_result_grouped_avp() {
        // GIVEN
        final List<AVP> nestedAvps = Arrays.asList(
            AVP.create(DiameterConstants.AVP_VENDOR_ID, 999L),
            AVP.create(DiameterConstants.AVP_EXPERIMENTAL_RESULT_CODE, 5000L)
        );

        // WHEN
        final AVP avp = AVP.create(DiameterConstants.AVP_EXPERIMENTAL_RESULT, nestedAvps);

        // THEN
        assertThat(avp.getCode()).isEqualTo(DiameterConstants.AVP_EXPERIMENTAL_RESULT);
        assertThat(avp.isMandatory()).isTrue(); // Experimental-Result is mandatory
        assertThat(avp.isVendorSpecific()).isFalse();
        assertThat(avp).isInstanceOf(GroupedAVP.class);
        
        final GroupedAVP groupedAVP = (GroupedAVP) avp;
        assertThat(groupedAVP.getAVPs()).hasSize(2);
        assertThat(groupedAVP.findAVP(DiameterConstants.AVP_VENDOR_ID)).isNotNull();
        assertThat(groupedAVP.findAVP(DiameterConstants.AVP_EXPERIMENTAL_RESULT_CODE)).isNotNull();
    }

    @Test
    void it_can_parse_itself_from_bytes() throws IOException {
        // GIVEN
        final AVP originalAvp = AVP.create(DiameterConstants.AVP_RESULT_CODE, DiameterConstants.RES_DIAMETER_SUCCESS);
        final ByteArrayOutputStream out = new ByteArrayOutputStream(8);
        final DataOutputStream dos = new DataOutputStream(out);
        originalAvp.writeTo(dos);
        final byte[] avpBytes = out.toByteArray();

        // WHEN
        final AVP parsedAvp = AVP.readFrom(ByteBuffer.wrap(avpBytes));

        // THEN
        assertThat(parsedAvp.getCode()).isEqualTo(originalAvp.getCode());
        assertThat(parsedAvp.getDataAsUnsignedInt()).isEqualTo(originalAvp.getDataAsUnsignedInt());
        assertThat(parsedAvp.isMandatory()).isEqualTo(originalAvp.isMandatory());
        assertThat(parsedAvp.isVendorSpecific()).isEqualTo(originalAvp.isVendorSpecific());
    }

    @Test
    void it_can_parse_grouped_avps_from_bytes() throws IOException {
        // GIVEN
        final List<AVP> nestedAvps = Arrays.asList(
            AVP.create(DiameterConstants.AVP_VENDOR_ID, 123L),
            AVP.create(DiameterConstants.AVP_AUTH_APPLICATION_ID, 456L)
        );
        final AVP originalAvp = AVP.create(DiameterConstants.AVP_VENDOR_SPECIFIC_APPLICATION_ID, nestedAvps);
        final ByteArrayOutputStream out = new ByteArrayOutputStream(64);
        final DataOutputStream dos = new DataOutputStream(out);
        originalAvp.writeTo(dos);
        final byte[] avpBytes = out.toByteArray();

        // WHEN
        final AVP parsedAvp = AVP.readFrom(ByteBuffer.wrap(avpBytes));

        // THEN
        assertThat(parsedAvp.getCode()).isEqualTo(originalAvp.getCode());
        assertThat(parsedAvp.isMandatory()).isEqualTo(originalAvp.isMandatory());
        assertThat(parsedAvp).isInstanceOf(GroupedAVP.class);

        final GroupedAVP parsedGrouped = (GroupedAVP) parsedAvp;
        assertThat(parsedGrouped.getAVPs()).hasSize(2);
        assertThat(parsedGrouped.findAVP(DiameterConstants.AVP_VENDOR_ID)).isNotNull();
        assertThat(parsedGrouped.findAVP(DiameterConstants.AVP_AUTH_APPLICATION_ID)).isNotNull();
        assertThat(parsedGrouped.findAVP(DiameterConstants.AVP_VENDOR_ID).getDataAsUnsignedInt()).isEqualTo(123L);
        assertThat(parsedGrouped.findAVP(DiameterConstants.AVP_AUTH_APPLICATION_ID).getDataAsUnsignedInt()).isEqualTo(456L);
    }
}
