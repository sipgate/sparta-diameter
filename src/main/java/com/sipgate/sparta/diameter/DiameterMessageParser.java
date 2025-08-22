package com.sipgate.sparta.diameter;

import com.sipgate.sparta.diameter.core.*;
import com.sipgate.sparta.diameter.messages.base.*;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses Diameter messages from binary data.
 * <p>
 * Provides methods to construct Diameter Command objects from binary sources.
 * Designed to handle Diameter protocol messages as defined in RFC 6733.
 * </p>
 */
public final class DiameterMessageParser {

    /**
     * Parses a Diameter message from a ByteBuffer (useful for Netty integration).
     * Does not change the buffer position.
     *
     * @param buffer the ByteBuffer containing the Diameter message
     * @return the parsed Command object representing the Diameter message
     * @throws DiameterException if the buffer does not contain the number of bytes required for a valid Diameter message
     */
    public static Command parseMessage(final ByteBuffer buffer) throws DiameterException {
        final int messageLength = getMessageLength(buffer);
        if (buffer.remaining() < messageLength) {
            throw new DiameterException("Invalid Diameter message: too short");
        }
        final byte[] data = new byte[messageLength];

        // Do not change the buffer position
        final int position = buffer.position();
        buffer.get(data);
        buffer.position(position);

        return parseMessage(new DataInputStream(new ByteArrayInputStream(data)), messageLength);
    }

    /**
     * Parses a Diameter message from a DataInputStream.
     *
     * @param inputStream the DataInputStream containing the Diameter message
     * @param messageLength the length of the Diameter message
     * @return the parsed Command object representing the Diameter message
     * @throws DiameterException if an error occurs while parsing the message
     */
    private static Command parseMessage(final DataInputStream inputStream, final int messageLength) throws DiameterException {
        try {
            // Read Diameter header (20 bytes)
            final int version = inputStream.readUnsignedByte();
            if (version != DiameterConstants.DIAMETER_VERSION) {
                throw new DiameterException("Unsupported Diameter version: " + version);
            }
            // Skip already-read messageLength
            inputStream.skipBytes(3);

            // Flags (1 byte)
            final int flags = inputStream.readUnsignedByte();
            final boolean isRequest = (flags & 0x80) != 0;
            final boolean isProxiable = (flags & 0x40) != 0;
            final boolean isError = (flags & 0x20) != 0;
            final boolean isRetransmitted = (flags & 0x10) != 0; // T flag (RFC 6733)

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
                                                isError, isRetransmitted, applicationId,
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
     * Extracts the message length from a ByteBuffer.
     * Does not modify the buffer position.
     *
     * @param buffer the ByteBuffer containing the Diameter message
     * @return the length of the Diameter message
     * @throws DiameterException if the buffer does not contain enough data to read the length
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
     * Parses AVPs from the input stream.
     *
     * @param inputStream the DataInputStream to read from
     * @param remainingLength the number of bytes remaining in the message after the header
     * @return a list of parsed AVPs
     * @throws IOException if an I/O error occurs while reading the AVPs
     * @throws DiameterException if the AVPs are invalid
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
     *
     * @param inputStream the DataInputStream to read from
     * @return the parsed AVP object
     * @throws IOException if an I/O error occurs while reading the AVP
     */
    private static AVP parseAVP(final DataInputStream inputStream) throws IOException {
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
     *
     * @param commandCode the command code of the Diameter message
     * @param isRequest whether the message is a request
     * @param isProxiable whether the message is proxiable
     * @param isError whether the message indicates an error
     * @param isRetransmitted whether the message is a retransmission
     * @param applicationId the application ID of the Diameter message
     * @param hopByHopId the hop-by-hop identifier of the Diameter message
     * @param endToEndId the end-to-end identifier of the Diameter message
     * @return the created Command object
     */
    private static Command createCommand(final int commandCode, final boolean isRequest,
                                       final boolean isProxiable, final boolean isError,
                                       final boolean isRetransmitted, final int applicationId,
                                       final int hopByHopId, final int endToEndId) {

        // Handle known command codes
        switch (commandCode) {
            case DiameterConstants.CMD_CAPABILITIES_EXCHANGE:
                if (isRequest) {
                    return new CapabilitiesExchangeRequest(isRetransmitted, hopByHopId, endToEndId);
                }

                if (isError) {
                    return new CapabilitiesExchangeAnswer(isRetransmitted, hopByHopId, endToEndId, isError);
                }

                return new CapabilitiesExchangeAnswer(isRetransmitted, hopByHopId, endToEndId);

            case DiameterConstants.CMD_DEVICE_WATCHDOG:
                if (isRequest) {
                    return new DeviceWatchdogRequest(isRetransmitted, hopByHopId, endToEndId);
                }

                if (isError) {
                    return new DeviceWatchdogAnswer(isRetransmitted, hopByHopId, endToEndId, isError);
                }

                return new DeviceWatchdogAnswer(isRetransmitted, hopByHopId, endToEndId);

            default:
                // For unknown command codes, create a generic command
                return new GenericCommand(commandCode, isRequest, isProxiable, isError,
                                        isRetransmitted, applicationId, hopByHopId, endToEndId);
        }
    }
}
