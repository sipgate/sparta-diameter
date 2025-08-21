package com.sipgate.sparta.diameter.base;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Parser for Diameter messages from binary data.
 * Designed to work with Netty ByteBufs and other binary sources.
 */
public final class DiameterMessageParser {

    /**
     * Extracts the message length from the Diameter header.
     * Useful for Netty frame detection without full parsing.
     */
    public static int getMessageLength(final byte[] headerBytes) throws DiameterException {
        if (headerBytes == null || headerBytes.length < 4) {
            throw new DiameterException("Need at least 4 bytes to read message length");
        }

        // Skip version byte, read 3-byte length field
        return ((headerBytes[1] & 0xFF) << 16) |
               ((headerBytes[2] & 0xFF) << 8) |
               (headerBytes[3] & 0xFF);
    }

    /**
     * Extracts the message length from a ByteBuffer.
     * Does not modify the buffer position.
     */
    public static int getMessageLength(final ByteBuffer buffer) throws DiameterException {
        if (buffer.remaining() < 4) {
            throw new DiameterException("Need at least 4 bytes to read message length");
        }

        // Read length without changing buffer position
        final int position = buffer.position();
        buffer.get(); // Skip version
        final int length = ((buffer.get() & 0xFF) << 16) |
                          ((buffer.get() & 0xFF) << 8) |
                          (buffer.get() & 0xFF);
        buffer.position(position); // Reset position

        return length;
    }

    /**
     * Parses a Diameter message from a byte array.
     */
    public static Command parseMessage(final byte[] data) throws DiameterException {
        if (data == null || data.length < 20) {
            throw new DiameterException("Invalid Diameter message: too short");
        }

        return parseMessage(new DataInputStream(new ByteArrayInputStream(data)));
    }

    /**
     * Parses a Diameter message from a ByteBuffer (useful for Netty integration).
     */
    public static Command parseMessage(final ByteBuffer buffer) throws DiameterException {
        if (buffer.remaining() < 20) {
            throw new DiameterException("Invalid Diameter message: too short");
        }

        final byte[] data = new byte[buffer.remaining()];
        buffer.get(data);
        return parseMessage(data);
    }

    /**
     * Parses a Diameter message from a DataInputStream.
     */
    public static Command parseMessage(final DataInputStream inputStream) throws DiameterException {
        try {
            // Read Diameter header (20 bytes)
            final int version = inputStream.readUnsignedByte();
            if (version != 1) {
                throw new DiameterException("Unsupported Diameter version: " + version);
            }

            // Message Length (3 bytes)
            final int messageLength = (inputStream.readUnsignedByte() << 16) |
                                    (inputStream.readUnsignedByte() << 8) |
                                    inputStream.readUnsignedByte();

            // Flags (1 byte)
            final int flags = inputStream.readUnsignedByte();
            final boolean isRequest = (flags & 0x80) != 0;
            final boolean isProxiable = (flags & 0x40) != 0;
            final boolean isError = (flags & 0x20) != 0;

            // Command Code (3 bytes)
            final int commandCode = (inputStream.readUnsignedByte() << 16) |
                                  (inputStream.readUnsignedByte() << 8) |
                                  inputStream.readUnsignedByte();

            // Application-Id (4 bytes)
            final int applicationId = inputStream.readInt();

            // Hop-by-Hop Identifier (4 bytes)
            final int hopByHopId = inputStream.readInt();

            // End-to-End Identifier (4 bytes)
            final int endToEndId = inputStream.readInt();

            // Parse AVPs (remaining bytes)
            final List<AVP> avps = parseAVPs(inputStream, messageLength - 20);

            // Create appropriate message type
            final Command command = createCommand(commandCode, isRequest, isProxiable,
                                                isError, applicationId,
                                                hopByHopId, endToEndId);

            // Add parsed AVPs
            for (final AVP avp : avps) {
                command.addAVP(avp);
            }

            return command;

        } catch (final IOException e) {
            throw new DiameterException("Error parsing Diameter message", e);
        }
    }

    /**
     * Parses AVPs from the input stream.
     */
    private static List<AVP> parseAVPs(final DataInputStream inputStream, final int remainingLength)
            throws IOException, DiameterException {
        final List<AVP> avps = new ArrayList<>();
        int bytesRead = 0;

        while (bytesRead < remainingLength) {
            if (remainingLength - bytesRead < 8) {
                throw new DiameterException("Invalid AVP: not enough bytes for header");
            }

            final AVP avp = parseAVP(inputStream);
            avps.add(avp);

            final int avpLength = avp.getLength();
            bytesRead += avpLength;

            // Skip padding to 4-byte boundary
            final int padding = (4 - (avpLength % 4)) % 4;
            if (padding > 0) {
                inputStream.skipBytes(padding);
                bytesRead += padding;
            }
        }

        return avps;
    }

    /**
     * Parses a single AVP from the input stream.
     */
    private static AVP parseAVP(final DataInputStream inputStream) throws IOException, DiameterException {
        // AVP Code (4 bytes)
        final int code = inputStream.readInt();

        // Flags (1 byte)
        final int flags = inputStream.readUnsignedByte();
        final boolean vendorSpecific = (flags & 0x80) != 0;
        final boolean mandatory = (flags & 0x40) != 0;
        final boolean protectedAVP = (flags & 0x20) != 0;

        // Length (3 bytes)
        final int length = (inputStream.readUnsignedByte() << 16) |
                          (inputStream.readUnsignedByte() << 8) |
                          inputStream.readUnsignedByte();

        // Vendor-Id (4 bytes, if vendor-specific)
        int vendorId = 0;
        int dataLength = length - 8; // Base header is 8 bytes
        if (vendorSpecific) {
            vendorId = inputStream.readInt();
            dataLength -= 4; // Subtract vendor-id field
        }

        // Data
        final byte[] data = new byte[dataLength];
        inputStream.readFully(data);

        return new AVP(code, vendorSpecific, mandatory, protectedAVP, vendorId, data);
    }

    /**
     * Creates the appropriate Command subclass based on the message parameters.
     */
    private static Command createCommand(final int commandCode, final boolean isRequest,
                                       final boolean isProxiable, final boolean isError,
                                       final int applicationId, final int hopByHopId,
                                       final int endToEndId) {

        // Handle known command codes
        switch (commandCode) {
            case DiameterConstants.CAPABILITIES_EXCHANGE_REQUEST:
                if (isRequest) {
                    return new CapabilitiesExchangeRequest(hopByHopId, endToEndId);
                } else {
                    return new CapabilitiesExchangeAnswer(hopByHopId, endToEndId, isError);
                }

            case DiameterConstants.DEVICE_WATCHDOG_REQUEST:
                if (isRequest) {
                    return new DeviceWatchdogRequest(hopByHopId, endToEndId);
                } else {
                    return new DeviceWatchdogAnswer(hopByHopId, endToEndId, isError);
                }

            default:
                // For unknown command codes, create a generic command
                return new GenericCommand(commandCode, isRequest, isProxiable, isError,
                                        applicationId, hopByHopId, endToEndId);
        }
    }
}
