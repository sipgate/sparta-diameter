package com.sipgate.sparta.diameter.core;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * Represents a Diameter Attribute-Value Pair (AVP).
 * <p>
 * This class represents an AVP as defined in
 * <a href="https://datatracker.ietf.org/doc/html/rfc6733#section-4">RFC 6733, Section 4</a>.
 * AVPs are used to encapsulate protocol-specific data in Diameter messages.
 * </p>
 */
public class AVP {
    private final int code;
    private final boolean vendorSpecific;
    private final boolean mandatory;
    private final boolean protectedAVP;
    private final int vendorId;
    private final byte[] data;

    /**
     * Constructs an AVP with the specified parameters.
     *
     * @param code           The AVP code.
     * @param vendorSpecific Indicates whether the AVP is vendor-specific.
     * @param mandatory      Indicates whether the AVP is mandatory.
     * @param protectedAVP   Indicates whether the AVP is protected.
     * @param vendorId       The vendor ID.
     * @param data           The AVP data.
     */
    public AVP(final int code, final boolean vendorSpecific, final boolean mandatory, final boolean protectedAVP,
               final int vendorId, final byte[] data) {
        this.code = code;
        this.vendorSpecific = vendorSpecific;
        this.mandatory = mandatory;
        this.protectedAVP = protectedAVP;
        this.vendorId = vendorId;
        this.data = data != null ? data.clone() : new byte[0];
    }

    /**
     * Constructs an AVP with the specified code, mandatory flag, and data.
     *
     * @param code      The AVP code.
     * @param mandatory Indicates whether the AVP is mandatory.
     * @param data      The AVP data.
     */
    public AVP(final int code, final boolean mandatory, final byte[] data) {
        this(code, false, mandatory, false, 0, data);
    }

    /**
     * Retrieves the AVP code.
     *
     * @return The AVP code.
     */
    public int getCode() {
        return code;
    }

    /**
     * Checks if the AVP is vendor-specific.
     *
     * @return True if the AVP is vendor-specific, false otherwise.
     */
    public boolean isVendorSpecific() {
        return vendorSpecific;
    }

    /**
     * Checks if the AVP is mandatory.
     *
     * @return True if the AVP is mandatory, false otherwise.
     */
    public boolean isMandatory() {
        return mandatory;
    }

    /**
     * Checks if the AVP is protected.
     *
     * @return True if the AVP is protected, false otherwise.
     */
    public boolean isProtected() {
        return protectedAVP;
    }

    /**
     * Retrieves the vendor ID.
     *
     * @return The vendor ID, or 0 if not vendor-specific.
     */
    public int getVendorId() {
        return vendorId;
    }

    /**
     * Retrieves the AVP data.
     *
     * @return A copy of the AVP data.
     */
    public byte[] getData() {
        return data.clone();
    }

    /**
     * Converts this AVP to a ByteBuffer.
     *
     * @return A ByteBuffer containing the serialized AVP.
     */
    public ByteBuffer toByteBuffer() {
        try {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final DataOutputStream dos = new DataOutputStream(baos);
            writeTo(dos);
            return ByteBuffer.wrap(baos.toByteArray());
        } catch (final IOException e) {
            throw new RuntimeException("Failed to convert AVP to ByteBuffer", e);
        }
    }

    /**
     * Gets the data as a 32-bit signed integer (network byte order).
     * @return the integer value, or throws IllegalArgumentException if data length < 4
     */
    public int getDataAsInt() {
        if (data.length < 4) {
            throw new IllegalArgumentException("AVP data must be at least 4 bytes to read as integer");
        }
        return ((data[0] & 0xFF) << 24) |
               ((data[1] & 0xFF) << 16) |
               ((data[2] & 0xFF) << 8) |
               (data[3] & 0xFF);
    }

    /**
     * Gets the data as a UTF-8 string.
     * @return the string value
     */
    public String getDataAsString() {
        return new String(data, StandardCharsets.UTF_8);
    }

    /**
     * Creates a byte array from a 32-bit integer in network byte order.
     * @param value the integer value
     * @return byte array representation
     */
    public static byte[] intToBytes(final int value) {
        final byte[] data = new byte[4];
        data[0] = (byte) ((value >> 24) & 0xFF);
        data[1] = (byte) ((value >> 16) & 0xFF);
        data[2] = (byte) ((value >> 8) & 0xFF);
        data[3] = (byte) (value & 0xFF);
        return data;
    }

    /**
     * Creates an AVP with an integer value.
     * @param code the AVP code
     * @param mandatory whether the AVP is mandatory
     * @param value the integer value
     * @return the created AVP
     */
    public static AVP createIntegerAVP(final int code, final boolean mandatory, final int value) {
        return new AVP(code, mandatory, intToBytes(value));
    }

    /**
     * Creates an AVP with a string value.
     * @param code the AVP code
     * @param mandatory whether the AVP is mandatory
     * @param value the string value
     * @return the created AVP
     */
    public static AVP createStringAVP(final int code, final boolean mandatory, final String value) {
        return new AVP(code, mandatory, value.getBytes(StandardCharsets.UTF_8));
    }

    public int getLength() {
        int length = 8; // AVP header is 8 bytes minimum
        if (vendorSpecific) {
            length += 4; // Vendor-Id field
        }
        length += data.length;
        return length;
    }

    /**
     * Writes this AVP to the given DataOutputStream.
     *
     * @param outputStream The DataOutputStream to write to.
     * @throws IOException If an I/O error occurs.
     */
    public void writeTo(final DataOutputStream outputStream) throws IOException {
        // AVP Code (4 bytes)
        outputStream.writeInt(code);

        // Flags (1 byte)
        int flags = 0;
        if (vendorSpecific) flags |= 0x80;
        if (mandatory) flags |= 0x40;
        if (protectedAVP) flags |= 0x20;
        outputStream.writeByte(flags);

        // Length (3 bytes) - includes header + data
        final int length = getLength();
        outputStream.writeByte((length >> 16) & 0xFF);
        outputStream.writeByte((length >> 8) & 0xFF);
        outputStream.writeByte(length & 0xFF);

        // Vendor-Id (4 bytes, if vendor-specific)
        if (vendorSpecific) {
            outputStream.writeInt(vendorId);
        }

        // Data
        outputStream.write(data);

        // Padding to 4-byte boundary
        final int padding = (4 - (data.length % 4)) % 4;
        for (int i = 0; i < padding; i++) {
            outputStream.writeByte(0);
        }
    }
}
