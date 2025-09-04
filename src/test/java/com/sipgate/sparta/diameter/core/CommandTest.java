package com.sipgate.sparta.diameter.core;

import com.sipgate.sparta.diameter.core.avp.AVP;
import com.sipgate.sparta.diameter.messages.rfc6733.*;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.nio.ByteBuffer;

import static org.assertj.core.api.Assertions.*;

public class CommandTest {
    private static final String DWR_HEX = "0100004c80000118000000002405603024056030000001084000001e74737463636e63642e63636e2e696e746d65742e69650000000001284000001874737463636e2e696e746d65742e6965";

    @Test
    void it_parses_a_DWR_from_binary_input() throws Exception {
        // GIVEN
        final byte[] messageData = hexStringToByteArray(DWR_HEX);
        final ByteBuffer buffer = ByteBuffer.wrap(messageData);

        // WHEN
        final Command<DeviceWatchdogRequest> command = Command.parseMessage(buffer);

        // THEN
        assertThat(command).isInstanceOf(DeviceWatchdogRequest.class);
        final DeviceWatchdogRequest dwr = (DeviceWatchdogRequest) command;

        assertThat(dwr.getVersion()).isEqualTo(1);
        assertThat(dwr.getCommandCode()).isEqualTo(DiameterConstants.CMD_DEVICE_WATCHDOG);
        assertThat(dwr.isRequest()).isTrue();
        assertThat(dwr.isProxiable()).isFalse();
        assertThat(dwr.isError()).isFalse();
        assertThat(dwr.isRetransmitted()).isFalse();
        assertThat(dwr.getApplicationId()).isEqualTo(DiameterConstants.APP_DIAMETER_COMMON_MESSAGES);
        assertThat(dwr.getHopByHopIdentifier()).isEqualTo(0x24056030);
        assertThat(dwr.getEndToEndIdentifier()).isEqualTo(0x24056030);

        assertThat(dwr.getOriginHost()).isEqualTo("tstccncd.ccn.intmet.ie");
        assertThat(dwr.getOriginRealm()).isEqualTo("tstccn.intmet.ie");

        final AVP originHostAVP = dwr.findAVP(DiameterConstants.AVP_ORIGIN_HOST);
        assertThat(originHostAVP).isNotNull();
        assertThat(originHostAVP.isMandatory()).isTrue();
        assertThat(originHostAVP.isVendorSpecific()).isFalse();

        final AVP originRealmAVP = dwr.findAVP(DiameterConstants.AVP_ORIGIN_REALM);
        assertThat(originRealmAVP).isNotNull();
        assertThat(originRealmAVP.isMandatory()).isTrue();
        assertThat(originRealmAVP.isVendorSpecific()).isFalse();
    }

    @Test
    void it_serializes_a_DWR_to_binary() throws Exception {
        // GIVEN
        final ByteBuffer originalData = ByteBuffer.wrap(hexStringToByteArray(DWR_HEX));

        // WHEN
        final Command command = Command.parseMessage(originalData);
        final DeviceWatchdogRequest dwr = (DeviceWatchdogRequest) command;
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        dwr.writeTo(dataOutputStream);
        dataOutputStream.flush();
        final byte[] serializedData = outputStream.toByteArray();
        final String serializedHex = byteArrayToHexString(serializedData);

        // THEN
        assertThat(serializedHex.toLowerCase()).isEqualTo(DWR_HEX.toLowerCase());
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
