package com.sipgate.sparta.diameter.base.core;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.AVPParseException;
import com.sipgate.sparta.diameter.base.messages.DeviceWatchdogRequest;
import com.sipgate.sparta.diameter.base.messages.SessionTerminationRequest;
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
    void it_throws_only_for_first_bad_avp_when_multiple_violations_exist() {
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
                    assertThat(avpEx.getSessionId()).isNull();
                });
    }

    @Test
    void it_throws_for_bad_avp_with_session_id() {
        // GIVEN: a message with a valid session id AVP and a bad AVP afterwards
        final byte[] message = hexStringToByteArray(
            "0100002480000118000000000000ABCD00001234" + // 20-byte header
                "000001074000000961000000" +             // session id: "a" + 3-byte padding for 32-bit boundaries, RFC 6733 § 4.2., OctetString
                "0000000240000007");                     // second AVP: code 2, length 7 (invalid)
        final ByteBuffer buffer = ByteBuffer.wrap(message);

        // WHEN / THEN: single-error rule — only the first violation is reported
        assertThatThrownBy(() -> Command.parseMessage(buffer))
            .isInstanceOf(AVPParseException.class)
            .satisfies(ex -> {
                final AVPParseException avpEx = (AVPParseException) ex;
                assertThat(avpEx.getResultCode()).isEqualTo(RES_DIAMETER_INVALID_AVP_LENGTH);
                assertThat(avpEx.getOffendingAvp().getCode()).isEqualTo(2);
                assertThat(avpEx.getSessionId()).isEqualTo("a");
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

    // unknown 3GPP vendor AVP 1504 (ANID) with V+M bits, 4 bytes data
    private static final String UNKNOWN_MANDATORY_AVP = "000005e0" + "c0000010" + "000028af" + "61626364";

    @Test
    void it_keeps_an_unknown_mandatory_avp_of_an_unknown_request_for_relaying() throws Exception {
        // GIVEN: request of a command no factory knows (8388999, app SWx), carrying an
        // AVP outside the dictionary with the M-bit set
        final byte[] message = hexStringToByteArray(
                "01000024" + "c0800287" + "01000031" + "0000abcd" + "00001234" + UNKNOWN_MANDATORY_AVP);

        // WHEN
        final IncomingCommand command = Command.parseMessage(ByteBuffer.wrap(message));

        // THEN: parsed (no 5001) and the AVP is retained unchanged for the relay
        assertThat(command).isInstanceOf(GenericRequest.class);
        final AVP avp = ((Command) command).findAVP(new AVPKey(1504, 10415));
        assertThat(avp).isNotNull();
        assertThat(avp.isMandatory()).isTrue();
        assertThat(avp.getData()).isEqualTo("abcd".getBytes());
    }

    @Test
    void it_keeps_an_unknown_mandatory_avp_of_an_unknown_answer_for_relaying() throws Exception {
        // GIVEN: the downstream answer to such a relayed request
        final byte[] message = hexStringToByteArray(
                "01000024" + "40800287" + "01000031" + "0000abcd" + "00001234" + UNKNOWN_MANDATORY_AVP);

        // WHEN
        final IncomingCommand command = Command.parseMessage(ByteBuffer.wrap(message));

        // THEN
        assertThat(command).isInstanceOf(GenericAnswer.In.class);
        assertThat(((Command) command).findAVP(new AVPKey(1504, 10415))).isNotNull();
    }

    @Test
    void it_still_rejects_an_unknown_mandatory_avp_in_a_known_command_with_5001() {
        // GIVEN: a DWR (typed, interpreted locally) with the same AVP
        final byte[] message = hexStringToByteArray(
                "01000024" + "80000118" + "00000000" + "0000abcd" + "00001234" + UNKNOWN_MANDATORY_AVP);

        // WHEN / THEN
        assertThatThrownBy(() -> Command.parseMessage(ByteBuffer.wrap(message)))
                .isInstanceOf(AVPParseException.class)
                .satisfies(ex -> assertThat(((AVPParseException) ex).getResultCode())
                        .isEqualTo(DiameterConstants.RES_DIAMETER_AVP_UNSUPPORTED));
    }

    @Test
    void it_keeps_an_unknown_mandatory_avp_of_a_known_proxiable_command_on_a_relay() throws Exception {
        // GIVEN: an STR (typed, P-bit set) carrying an AVP outside the dictionary with the M-bit
        final byte[] message = hexStringToByteArray(
                "01000024" + "c0000113" + "00000004" + "0000abcd" + "00001234" + UNKNOWN_MANDATORY_AVP);

        // WHEN: parsed by a relay node
        final IncomingCommand command = Command.parseMessage(ByteBuffer.wrap(message), true);

        // THEN: typed, no 5001, AVP retained for the forward copy (RFC 6733 §2.8.1)
        assertThat(command).isInstanceOf(SessionTerminationRequest.In.class);
        assertThat(((Command) command).findAVP(new AVPKey(1504, 10415))).isNotNull();
    }

    @Test
    void it_rejects_an_unknown_mandatory_avp_of_a_known_proxiable_command_on_an_endpoint() {
        // GIVEN: the same STR
        final byte[] message = hexStringToByteArray(
                "01000024" + "c0000113" + "00000004" + "0000abcd" + "00001234" + UNKNOWN_MANDATORY_AVP);

        // WHEN / THEN: strict endpoint parsing (default)
        assertThatThrownBy(() -> Command.parseMessage(ByteBuffer.wrap(message)))
                .isInstanceOf(AVPParseException.class)
                .satisfies(ex -> assertThat(((AVPParseException) ex).isRequest()).isTrue());
    }

    @Test
    void it_rejects_an_unknown_mandatory_avp_of_a_non_proxiable_command_even_on_a_relay() {
        // GIVEN: a DWR (consumed locally, never relayed) with the same AVP
        final byte[] message = hexStringToByteArray(
                "01000024" + "80000118" + "00000000" + "0000abcd" + "00001234" + UNKNOWN_MANDATORY_AVP);

        // WHEN / THEN
        assertThatThrownBy(() -> Command.parseMessage(ByteBuffer.wrap(message), true))
                .isInstanceOf(AVPParseException.class)
                .satisfies(ex -> assertThat(((AVPParseException) ex).getResultCode())
                        .isEqualTo(DiameterConstants.RES_DIAMETER_AVP_UNSUPPORTED));
    }

    @Test
    void it_marks_a_parse_failure_of_an_answer_as_not_a_request() {
        // GIVEN: an STA (R-bit clear) with the unknown M-bit AVP, parsed strictly
        final byte[] message = hexStringToByteArray(
                "01000024" + "40000113" + "00000004" + "0000abcd" + "00001234" + UNKNOWN_MANDATORY_AVP);

        // WHEN / THEN: the session must be able to tell it is an answer (never answered)
        assertThatThrownBy(() -> Command.parseMessage(ByteBuffer.wrap(message)))
                .isInstanceOf(AVPParseException.class)
                .satisfies(ex -> assertThat(((AVPParseException) ex).isRequest()).isFalse());
    }

    @Test
    void it_presents_a_typed_request_with_the_P_bit_clear_as_not_proxiable() throws Exception {
        // GIVEN: an STR (typed class defaults to proxiable) received with R=1, P=0
        final byte[] message = hexStringToByteArray(
                "01000014" + "80000113" + "00000004" + "0000abcd" + "00001234");

        // WHEN
        final IncomingCommand command = Command.parseMessage(ByteBuffer.wrap(message), true);

        // THEN: the wire flags win - a relay must not forward it as proxiable
        assertThat(command).isInstanceOf(SessionTerminationRequest.In.class);
        final Command parsed = (Command) command;
        assertThat(parsed.isRequest()).isTrue();
        assertThat(parsed.isProxiable()).isFalse();
        assertThat(parsed.isError()).isFalse();
        assertThat(parsed.isRetransmitted()).isFalse();
    }

    @Test
    void it_keeps_the_wire_application_id_of_a_typed_command() throws Exception {
        // GIVEN: an STR (typed class constant: Application-Id 0) received with Application-Id 4
        final byte[] message = hexStringToByteArray(
                "01000014" + "c0000113" + "00000004" + "0000abcd" + "00001234");

        // WHEN
        final IncomingCommand command = Command.parseMessage(ByteBuffer.wrap(message));

        // THEN
        assertThat(command).isInstanceOf(SessionTerminationRequest.In.class);
        assertThat(command.getApplicationId()).isEqualTo(4);
        assertThat(((Command) command).isProxiable()).isTrue();
    }

    @Test
    void it_keeps_the_wire_T_bit_of_a_typed_command() throws Exception {
        // GIVEN: an STR with R, P and T set; and a DWR with R, T set
        final byte[] str = hexStringToByteArray(
                "01000014" + "d0000113" + "00000000" + "0000abcd" + "00001234");
        final byte[] dwr = hexStringToByteArray(
                "01000014" + "90000118" + "00000000" + "0000abcd" + "00001234");

        // WHEN
        final Command parsedStr = (Command) Command.parseMessage(ByteBuffer.wrap(str));
        final Command parsedDwr = (Command) Command.parseMessage(ByteBuffer.wrap(dwr));

        // THEN
        assertThat(parsedStr.isRetransmitted()).isTrue();
        assertThat(parsedStr.isProxiable()).isTrue();
        assertThat(parsedDwr).isInstanceOf(DeviceWatchdogRequest.In.class);
        assertThat(parsedDwr.isRetransmitted()).isTrue();
        assertThat(parsedDwr.isProxiable()).isFalse();
    }

    @Test
    void it_keeps_the_wire_header_flags_of_a_generic_request() throws Exception {
        // GIVEN: an unknown command with R=1, P=0, T=1
        final byte[] message = hexStringToByteArray(
                "01000014" + "90800287" + "01000031" + "0000abcd" + "00001234");

        // WHEN
        final Command parsed = (Command) Command.parseMessage(ByteBuffer.wrap(message), true);

        // THEN
        assertThat(parsed).isInstanceOf(GenericRequest.class);
        assertThat(parsed.isProxiable()).isFalse();
        assertThat(parsed.isRetransmitted()).isTrue();
        assertThat(parsed.getApplicationId()).isEqualTo(0x01000031);
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
