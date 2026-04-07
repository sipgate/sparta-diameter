package com.sipgate.sparta.diameter.base.core;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.AVPParseException;
import com.sipgate.sparta.diameter.base.messages.DeviceWatchdogRequest;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.nio.ByteBuffer;

import static com.sipgate.sparta.diameter.base.core.DiameterConstants.RES_DIAMETER_INVALID_AVP_LENGTH;
import static com.sipgate.sparta.diameter.base.core.DiameterConstants.RES_DIAMETER_UNSUPPORTED_VERSION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CommandTest {

    private static final String DWR_HEX = "0100004c80000118000000002405603024056030000001084000001e74737463636e63642e63636e2e696e746d65742e69650000000001284000001874737463636e2e696e746d65742e6965";

    @Test
    void it_parses_a_DWR_from_binary_input() throws Exception {
        // GIVEN
        final byte[] messageData = hexStringToByteArray(DWR_HEX);
        final ByteBuffer buffer = ByteBuffer.wrap(messageData);

        // WHEN
        final IncomingCommand command = Command.parseMessage(buffer);

        // THEN
        assertThat(command).isInstanceOf(DeviceWatchdogRequest.In.class);
        final DeviceWatchdogRequest.In dwr = (DeviceWatchdogRequest.In) command;

        assertThat(dwr.getVersion()).isEqualTo(1);
        assertThat(dwr.getCommandCode()).isEqualTo(DiameterConstants.CMD_DEVICE_WATCHDOG);
        assertThat(dwr.isRequest()).isTrue();
        assertThat(dwr.isProxiable()).isFalse();
        assertThat(dwr.isError()).isFalse();
        assertThat(dwr.isRetransmitted()).isFalse();
        assertThat(dwr.getApplicationId()).isEqualTo(DiameterConstants.APP_DIAMETER_COMMON_MESSAGES);
        assertThat(dwr.hopByHopId()).isEqualTo(new HopByHopId(0x24056030));
        assertThat(dwr.endToEndId()).isEqualTo(new EndToEndId(0x24056030));

        assertThat(dwr.getOriginHost()).isEqualTo("tstccncd.ccn.intmet.ie");
        assertThat(dwr.getOriginRealm()).isEqualTo("tstccn.intmet.ie");

        final AVP originHostAVP = dwr.findAVP(new AVPKey(DiameterConstants.AVP_ORIGIN_HOST, 0));
        assertThat(originHostAVP).isNotNull();
        assertThat(originHostAVP.isMandatory()).isTrue();
        assertThat(originHostAVP.isVendorSpecific()).isFalse();

        final AVP originRealmAVP = dwr.findAVP(new AVPKey(DiameterConstants.AVP_ORIGIN_REALM, 0));
        assertThat(originRealmAVP).isNotNull();
        assertThat(originRealmAVP.isMandatory()).isTrue();
        assertThat(originRealmAVP.isVendorSpecific()).isFalse();
    }

    @Test
    void it_serializes_an_outgoing_DWR_to_binary() throws Exception {
        // GIVEN: parse the reference bytes to extract AVPs, then re-encode via an Out instance
        final byte[] originalBytes = hexStringToByteArray(DWR_HEX);
        final DeviceWatchdogRequest.In parsedIn = (DeviceWatchdogRequest.In)
                Command.parseMessage(ByteBuffer.wrap(originalBytes));

        // Build an Out DWR with the same AVPs and identifiers
        final DeviceWatchdogRequest.Out dwrOut = new DeviceWatchdogRequest.Out();
        for (final AVP avp : parsedIn.getAVPs()) {
            dwrOut.addAVP(avp);
        }

        // WHEN
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        dwrOut.writeTo(new DataOutputStream(baos),
                new HopByHopId(0x24056030), new EndToEndId(0x24056030));
        final String serializedHex = byteArrayToHexString(baos.toByteArray());

        // THEN
        assertThat(serializedHex.toLowerCase()).isEqualTo(DWR_HEX.toLowerCase());
    }

    @Test
    void it_throws_when_setting_avp_on_incoming_command() throws Exception {
        // GIVEN
        final byte[] messageData = hexStringToByteArray(DWR_HEX);
        final DeviceWatchdogRequest.In dwr = (DeviceWatchdogRequest.In)
                Command.parseMessage(ByteBuffer.wrap(messageData));

        // WHEN / THEN
        assertThatThrownBy(() -> dwr.addAVP(AVP.create(new AVPKey(DiameterConstants.AVP_ORIGIN_HOST, 0), "x")))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void it_parses_command_code_bytes_without_sign_extension() {
        // GIVEN: version 2 forces DiameterResultCodeException, so the exception carries the parsed
        // commandCode. Middle byte of command 0x008001 is 0x80 — sign extension would yield -32767.
        final byte[] message = hexStringToByteArray(
                "0200001480008001000000000000ABCD00001234");
        final ByteBuffer buffer = ByteBuffer.wrap(message);

        // WHEN / THEN
        assertThatThrownBy(() -> Command.parseMessage(buffer))
                .isInstanceOf(DiameterResultCodeException.class)
                .satisfies(ex -> {
                    final DiameterResultCodeException rcEx = (DiameterResultCodeException) ex;
                    assertThat(rcEx.getCommandCode()).isEqualTo(0x008001);
                });
    }

    @Test
    void it_throws_DiameterResultCodeException_5011_for_unsupported_version() {
        // GIVEN: a Diameter message header with version 2, command 280, hop-by-hop 0xABCD, end-to-end 0x1234
        final byte[] message = hexStringToByteArray(
                "0200001480000118000000000000ABCD00001234");
        final ByteBuffer buffer = ByteBuffer.wrap(message);

        // WHEN / THEN
        assertThatThrownBy(() -> Command.parseMessage(buffer))
                .isInstanceOf(DiameterResultCodeException.class)
                .satisfies(ex -> {
                    final DiameterResultCodeException rcEx = (DiameterResultCodeException) ex;
                    assertThat(rcEx.getResultCode()).isEqualTo(RES_DIAMETER_UNSUPPORTED_VERSION);
                    assertThat(rcEx.getCommandCode()).isEqualTo(280);
                    assertThat(rcEx.getHopByHop()).isEqualTo(new HopByHopId(0x0000ABCD));
                    assertThat(rcEx.getEndToEnd()).isEqualTo(new EndToEndId(0x00001234));
                });
    }

    @Test
    void it_throws_for_first_bad_avp_only_when_multiple_violations_exist() {
        // GIVEN: a message with two AVPs that both have invalid lengths (< 8)
        final byte[] message = hexStringToByteArray(
                "0100002480000118000000000000ABCD00001234" + // 20-byte header
                "0000000140000007" +                         // first AVP: code 1, length 7 (invalid)
                "0000000240000007");                          // second AVP: code 2, length 7 (invalid)
        final ByteBuffer buffer = ByteBuffer.wrap(message);

        // WHEN / THEN: single-error rule — only the first violation is reported
        assertThatThrownBy(() -> Command.parseMessage(buffer))
                .isInstanceOf(AVPParseException.class)
                .satisfies(ex -> {
                    final AVPParseException avpEx = (AVPParseException) ex;
                    assertThat(avpEx.getResultCode()).isEqualTo(RES_DIAMETER_INVALID_AVP_LENGTH);
                    assertThat(avpEx.getOffendingAvp().getCode()).isEqualTo(1);
                });
    }

    @Test
    void it_does_not_change_bytebuffer_position_when_getting_message_length() throws Exception {
        // GIVEN
        final byte[] data = hexStringToByteArray(DWR_HEX);
        final ByteBuffer buffer = ByteBuffer.wrap(data);
        final int initialPosition = buffer.position();

        // WHEN
        Command.getMessageLength(buffer);

        // THEN
        assertThat(buffer.position()).isEqualTo(initialPosition);
    }

    private byte[] hexStringToByteArray(final String hexString) {
        final int length = hexString.length();
        final byte[] data = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }
        return data;
    }

    private String byteArrayToHexString(final byte[] data) {
        final StringBuilder sb = new StringBuilder();
        for (final byte b : data) {
            sb.append(String.format("%02x", b & 0xFF));
        }
        return sb.toString();
    }
}
