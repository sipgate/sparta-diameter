package com.sipgate.sparta.diameter.base;

import com.sipgate.sparta.diameter.DiameterMessageParser;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DiameterMessageParserTest {
    private static final String DWR_HEX = "0100004c80000118000000002405603024056030000001084000001e74737463636e63642e63636e2e696e746d65742e69650000000001284000001874737463636e2e696e746d65742e6965";

    @Test
    void testParseDeviceWatchdogRequest() throws Exception {
        // Load the DWR message from the resource file
        final byte[] messageData = hexStringToByteArray(DWR_HEX);

        final Command command = DiameterMessageParser.parseMessage(messageData);

        // Verify it's a DeviceWatchdogRequest
        assertInstanceOf(DeviceWatchdogRequest.class, command, "Should be DeviceWatchdogRequest");
        final DeviceWatchdogRequest dwr = (DeviceWatchdogRequest) command;

        // Verify header fields
        assertEquals(1, dwr.getVersion(), "Version should be 1");
        assertEquals(DiameterConstants.DEVICE_WATCHDOG_REQUEST, dwr.getCommandCode(), "Command code should be 280");
        assertTrue(dwr.isRequest(), "Should be request");
        assertFalse(dwr.isProxiable(), "Should not be proxiable");
        assertFalse(dwr.isError(), "Should not be error");
        assertFalse(dwr.isRetransmitted(), "Should not be retransmitted");
        assertEquals(DiameterConstants.DIAMETER_COMMON_MESSAGES, dwr.getApplicationId(), "Application ID should be 0");
        assertEquals(0x24056030, dwr.getHopByHopIdentifier(), "Hop-by-Hop ID should match");
        assertEquals(0x24056030, dwr.getEndToEndIdentifier(), "End-to-End ID should match");

        // Verify Origin-Host AVP
        assertEquals("tstccncd.ccn.intmet.ie", dwr.getOriginHost(), "Origin-Host should match");

        // Verify Origin-Realm AVP
        assertEquals("tstccn.intmet.ie", dwr.getOriginRealm(), "Origin-Realm should match");

        // Verify AVP properties
        final AVP originHostAVP = dwr.findAVP(DiameterConstants.ORIGIN_HOST);
        assertNotNull(originHostAVP, "Origin-Host AVP should exist");
        assertTrue(originHostAVP.isMandatory(), "Origin-Host should be mandatory");
        assertFalse(originHostAVP.isVendorSpecific(), "Origin-Host should not be vendor-specific");

        final AVP originRealmAVP = dwr.findAVP(DiameterConstants.ORIGIN_REALM);
        assertNotNull(originRealmAVP, "Origin-Realm AVP should exist");
        assertTrue(originRealmAVP.isMandatory(), "Origin-Realm should be mandatory");
        assertFalse(originRealmAVP.isVendorSpecific(), "Origin-Realm should not be vendor-specific");
    }

    @Test
    void testDeviceWatchdogRequestRoundTrip() throws Exception {
        // Parse the DWR from hex data
        final byte[] originalData = hexStringToByteArray(DWR_HEX);
        final Command command = DiameterMessageParser.parseMessage(originalData);

        // Verify it's a DeviceWatchdogRequest
        assertInstanceOf(DeviceWatchdogRequest.class, command, "Should be DeviceWatchdogRequest");
        final DeviceWatchdogRequest dwr = (DeviceWatchdogRequest) command;

        // Serialize back to bytes
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        dwr.writeTo(dataOutputStream);
        dataOutputStream.flush();

        final byte[] serializedData = outputStream.toByteArray();
        final String serializedHex = byteArrayToHexString(serializedData);

        // Compare with original hex string (should be identical)
        assertEquals(DWR_HEX.toLowerCase(), serializedHex.toLowerCase(),
                "Round-trip serialization should produce identical result");
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
