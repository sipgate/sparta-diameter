package com.sipgate.sparta.diameter.core.avp;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteBuffer;
import java.util.Date;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

    // Factory functionality merged from AVPFactory
    private static final Map<Integer, AVPDefinition> registry = new ConcurrentHashMap<>();

    static {
        // Register core protocol AVPs by default
        registerProvider(new CoreAVPProvider());
    }

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
    AVP(final int code, final boolean vendorSpecific, final boolean mandatory, final boolean protectedAVP,
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
    AVP(final int code, final boolean mandatory, final byte[] data) {
        this(code, false, mandatory, false, 0, data);
    }

    /**
     * Creates an AVP with full manual specification. Ideally you should use one of the type-specific
     * create() methods instead, and register custom types via registerProvider() if needed.
     *
     * @param code The AVP code.
     * @param vendorSpecific Whether the AVP is vendor-specific.
     * @param mandatory Whether the AVP is mandatory.
     * @param protectedAVP Whether the AVP is protected.
     * @param vendorId The vendor ID (0 if not vendor-specific).
     * @param data The AVP data.
     * @return The created AVP.
     */
    public static AVP createRaw(final int code, final boolean vendorSpecific, final boolean mandatory, final boolean protectedAVP, final int vendorId, final byte[] data) {
        return new AVP(code, vendorSpecific, mandatory, protectedAVP, vendorId, data);
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
     * Gets the data as a 64-bit signed integer (network byte order).
     * @return the long value, or throws IllegalArgumentException if data length < 8
     */
    public long getDataAsLong() {
        if (data.length < 8) {
            throw new IllegalArgumentException("AVP data must be at least 8 bytes to read as long");
        }
        return ((long)(data[0] & 0xFF) << 56) |
               ((long)(data[1] & 0xFF) << 48) |
               ((long)(data[2] & 0xFF) << 40) |
               ((long)(data[3] & 0xFF) << 32) |
               ((long)(data[4] & 0xFF) << 24) |
               ((long)(data[5] & 0xFF) << 16) |
               ((long)(data[6] & 0xFF) << 8) |
               ((long)(data[7] & 0xFF));
    }
    /**
     * Gets the data as a 32-bit unsigned integer (network byte order).
     * @return the unsigned integer value as long, or throws IllegalArgumentException if data length < 4
     */
    public long getDataAsUnsignedInt() {
        if (data.length < 4) {
            throw new IllegalArgumentException("AVP data must be at least 4 bytes to read as unsigned integer");
        }
        return ((long)(data[0] & 0xFF) << 24) |
               ((long)(data[1] & 0xFF) << 16) |
               ((long)(data[2] & 0xFF) << 8) |
               ((long)(data[3] & 0xFF));
    }

    /**
     * Gets the data as a 64-bit unsigned integer (network byte order).
     * @return the unsigned long value as BigInteger, or throws IllegalArgumentException if data length < 8
     */
    public BigInteger getDataAsUnsignedLong() {
        if (data.length < 8) {
            throw new IllegalArgumentException("AVP data must be at least 8 bytes to read as unsigned long");
        }
        // Create BigInteger from unsigned byte array
        final byte[] unsignedBytes = new byte[9]; // 9 bytes to ensure positive value
        unsignedBytes[0] = 0; // Sign byte
        System.arraycopy(data, 0, unsignedBytes, 1, 8);
        return new BigInteger(unsignedBytes);
    }

    /**
     * Gets the data as a 32-bit IEEE 754 float (network byte order).
     * @return the float value, or throws IllegalArgumentException if data length < 4
     */
    public float getDataAsFloat() {
        if (data.length < 4) {
            throw new IllegalArgumentException("AVP data must be at least 4 bytes to read as float");
        }
        return Float.intBitsToFloat(getDataAsInt());
    }

    /**
     * Gets the data as a 64-bit IEEE 754 double (network byte order).
     * @return the double value, or throws IllegalArgumentException if data length < 8
     */
    public double getDataAsDouble() {
        if (data.length < 8) {
            throw new IllegalArgumentException("AVP data must be at least 8 bytes to read as double");
        }
        return Double.longBitsToDouble(getDataAsLong());
    }

    /**
     * Gets the data as an enumerated value (32-bit integer).
     * @return the enumerated value, or throws IllegalArgumentException if data length < 4
     */
    public int getDataAsEnumerated() {
        return getDataAsInt();
    }

    /**
     * Gets the data as a Diameter Time (NTP timestamp format).
     * NTP timestamp is seconds since January 1, 1900 00:00 UTC.
     * @return the Date value, or throws IllegalArgumentException if data length < 4
     */
    public Date getDataAsTime() {
        if (data.length < 4) {
            throw new IllegalArgumentException("AVP data must be at least 4 bytes to read as time");
        }
        final long ntpTime = getDataAsUnsignedInt();
        // Convert NTP time (seconds since 1900) to Unix time (seconds since 1970)
        // NTP epoch is 70 years before Unix epoch: 70 * 365.25 * 24 * 3600 = 2208988800L
        final long unixTime = ntpTime - 2208988800L;
        return new Date(unixTime * 1000);
    }

    /**
     * Gets the data as a Diameter Identity (domain name string).
     * @return the domain name string
     */
    public String getDataAsDiameterIdentity() {
        return getDataAsString();
    }

    /**
     * Gets the data as a Diameter URI string.
     * @return the URI string
     */
    public String getDataAsDiameterURI() {
        return getDataAsString();
    }

    /**
     * Gets the data as an IP Address (IPv4 or IPv6).
     * @return the InetAddress, or throws IllegalArgumentException if data is not valid IP address format
     */
    public InetAddress getDataAsIPAddress() {
        try {
            if (data.length == 4) {
                // IPv4 address
                return Inet4Address.getByAddress(data);
            } else if (data.length == 16) {
                // IPv6 address
                return Inet6Address.getByAddress(data);
            } else {
                throw new IllegalArgumentException("AVP data must be 4 bytes (IPv4) or 16 bytes (IPv6) to read as IP address");
            }
        } catch (final UnknownHostException e) {
            throw new IllegalArgumentException("Invalid IP address data", e);
        }
    }

    /**
     * Gets the data as raw bytes (OctetString).
     * @return a copy of the raw byte data
     */
    public byte[] getDataAsOctetString() {
        return getData();
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
    /**
     * Creates an AVP with an integer value.
     *
     * @param code the AVP code
     * @param mandatory whether the AVP is mandatory
     * @param value the integer value
     * @return the created AVP
     */
    private static AVP createIntegerAVP(final int code, final boolean mandatory, final int value) {
        return new AVP(code, mandatory, intToBytes(value));
    }

    /**
     * Creates an AVP with a string value.
     *
     * @param code the AVP code
     * @param mandatory whether the AVP is mandatory
     * @param value the string value
     * @return the created AVP
     */
    private static AVP createStringAVP(final int code, final boolean mandatory, final String value) {
        return new AVP(code, mandatory, value.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Creates an AVP with a 64-bit signed integer value.
     * @param code the AVP code
     * @param mandatory whether the AVP is mandatory
     * @param value the long value
     * @return the created AVP
     */
    private static AVP createLongAVP(final int code, final boolean mandatory, final long value) {
        return new AVP(code, mandatory, longToBytes(value));
    }

    /**
     * Creates an AVP with a 32-bit unsigned integer value.
     * @param code the AVP code
     * @param mandatory whether the AVP is mandatory
     * @param value the unsigned integer value as long
     * @return the created AVP
     */
    private static AVP createUnsignedIntAVP(final int code, final boolean mandatory, final long value) {
        return new AVP(code, mandatory, unsignedIntToBytes(value));
    }

    /**
     * Creates an AVP with a 64-bit unsigned integer value.
     * @param code the AVP code
     * @param mandatory whether the AVP is mandatory
     * @param value the unsigned long value as BigInteger
     * @return the created AVP
     */
    private static AVP createUnsignedLongAVP(final int code, final boolean mandatory, final BigInteger value) {
        return new AVP(code, mandatory, unsignedLongToBytes(value));
    }

    /**
     * Creates an AVP with a 32-bit float value.
     * @param code the AVP code
     * @param mandatory whether the AVP is mandatory
     * @param value the float value
     * @return the created AVP
     */
    private static AVP createFloatAVP(final int code, final boolean mandatory, final float value) {
        return new AVP(code, mandatory, floatToBytes(value));
    }

    /**
     * Creates an AVP with a 64-bit double value.
     * @param code the AVP code
     * @param mandatory whether the AVP is mandatory
     * @param value the double value
     * @return the created AVP
     */
    private static AVP createDoubleAVP(final int code, final boolean mandatory, final double value) {
        return new AVP(code, mandatory, doubleToBytes(value));
    }

    /**
     * Creates an AVP with an enumerated value.
     * @param code the AVP code
     * @param mandatory whether the AVP is mandatory
     * @param value the enumerated value
     * @return the created AVP
     */
    private static AVP createEnumeratedAVP(final int code, final boolean mandatory, final int value) {
        return new AVP(code, mandatory, intToBytes(value));
    }

    /**
     * Creates an AVP with a Diameter Time value.
     * @param code the AVP code
     * @param mandatory whether the AVP is mandatory
     * @param value the Date value
     * @return the created AVP
     */
    private static AVP createTimeAVP(final int code, final boolean mandatory, final Date value) {
        return new AVP(code, mandatory, timeToBytes(value));
    }

    /**
     * Creates an AVP with a Diameter Identity value.
     * @param code the AVP code
     * @param mandatory whether the AVP is mandatory
     * @param value the domain name string
     * @return the created AVP
     */
    private static AVP createDiameterIdentityAVP(final int code, final boolean mandatory, final String value) {
        return new AVP(code, mandatory, value.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Creates an AVP with a Diameter URI value.
     * @param code the AVP code
     * @param mandatory whether the AVP is mandatory
     * @param value the URI string
     * @return the created AVP
     */
    private static AVP createDiameterURIAVP(final int code, final boolean mandatory, final String value) {
        return new AVP(code, mandatory, value.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Creates an AVP with an IP Address value.
     * @param code the AVP code
     * @param mandatory whether the AVP is mandatory
     * @param value the InetAddress value
     * @return the created AVP
     */
    private static AVP createIPAddressAVP(final int code, final boolean mandatory, final InetAddress value) {
        return new AVP(code, mandatory, ipAddressToBytes(value));
    }

    /**
     * Creates an AVP with an OctetString value.
     * @param code the AVP code
     * @param mandatory whether the AVP is mandatory
     * @param value the byte array value
     * @return the created AVP
     */
    private static AVP createOctetStringAVP(final int code, final boolean mandatory, final byte[] value) {
        return new AVP(code, mandatory, value);
    }


    /**
     * Creates a byte array from a 32-bit integer in network byte order.
     * @param value the integer value
     * @return byte array representation
     */
    private static byte[] intToBytes(final int value) {
        final byte[] data = new byte[4];
        data[0] = (byte) ((value >> 24) & 0xFF);
        data[1] = (byte) ((value >> 16) & 0xFF);
        data[2] = (byte) ((value >> 8) & 0xFF);
        data[3] = (byte) (value & 0xFF);
        return data;
    }

    /**
     * Creates a byte array from a 64-bit long in network byte order.
     * @param value the long value
     * @return byte array representation
     */
    private static byte[] longToBytes(final long value) {
        final byte[] data = new byte[8];
        data[0] = (byte) ((value >> 56) & 0xFF);
        data[1] = (byte) ((value >> 48) & 0xFF);
        data[2] = (byte) ((value >> 40) & 0xFF);
        data[3] = (byte) ((value >> 32) & 0xFF);
        data[4] = (byte) ((value >> 24) & 0xFF);
        data[5] = (byte) ((value >> 16) & 0xFF);
        data[6] = (byte) ((value >> 8) & 0xFF);
        data[7] = (byte) (value & 0xFF);
        return data;
    }
    /**
     * Creates a byte array from a 32-bit unsigned integer in network byte order.
     * @param value the unsigned integer value as long
     * @return byte array representation
     */
    private static byte[] unsignedIntToBytes(final long value) {
        if (value < 0 || value > 0xFFFFFFFFL) {
            throw new IllegalArgumentException("Value must be between 0 and 4294967295");
        }
        return intToBytes((int) value);
    }

    /**
     * Creates a byte array from a 64-bit unsigned integer in network byte order.
     * @param value the unsigned long value as BigInteger
     * @return byte array representation
     */
    private static byte[] unsignedLongToBytes(final BigInteger value) {
        if (value.signum() < 0 || value.bitLength() > 64) {
            throw new IllegalArgumentException("Value must be between 0 and 18446744073709551615");
        }
        final byte[] bytes = value.toByteArray();
        if (bytes.length <= 8) {
            // Pad with leading zeros if necessary
            final byte[] result = new byte[8];
            System.arraycopy(bytes, 0, result, 8 - bytes.length, bytes.length);
            return result;
        } else {
            // Remove leading zero byte if present
            final byte[] result = new byte[8];
            System.arraycopy(bytes, bytes.length - 8, result, 0, 8);
            return result;
        }
    }

    /**
     * Creates a byte array from a 32-bit float in network byte order.
     * @param value the float value
     * @return byte array representation
     */
    private static byte[] floatToBytes(final float value) {
        return intToBytes(Float.floatToIntBits(value));
    }

    /**
     * Creates a byte array from a 64-bit double in network byte order.
     * @param value the double value
     * @return byte array representation
     */
    private static byte[] doubleToBytes(final double value) {
        return longToBytes(Double.doubleToLongBits(value));
    }

    /**
     * Creates a byte array from a Date for Diameter Time (NTP timestamp format).
     * @param value the Date value
     * @return byte array representation
     */
    private static byte[] timeToBytes(final Date value) {
        // Convert Unix time to NTP time
        final long unixTime = value.getTime() / 1000;
        final long ntpTime = unixTime + 2208988800L; // Add NTP epoch offset
        return unsignedIntToBytes(ntpTime);
    }

    /**
     * Creates a byte array from an InetAddress for IPAddress format.
     * @param value the InetAddress value
     * @return byte array representation
     */
    private static byte[] ipAddressToBytes(final InetAddress value) {
        return value.getAddress();
    }

    /**
     * Registers all AVP definitions from the given provider.
     *
     * @param provider The AVP provider to register
     */
    public static void registerProvider(final AVPProvider provider) {
        for (final AVPDefinition definition : provider.getDefinitions()) {
            registry.put(definition.code(), definition);
        }
    }

    /**
     * Creates an AVP with an integer value using automatic type deduction and flag handling.
     *
     * @param avpCode The AVP code constant from DiameterConstants
     * @param value   The integer value
     * @return The created AVP with appropriate flags
     * @throws IllegalArgumentException if AVP code is unknown or type mismatch
     */
    public static AVP create(final int avpCode, final int value) {
        final AVPDefinition definition = getDefinition(avpCode);
        validateType(definition, Integer.class);
        return createIntegerAVP(avpCode, definition.mandatory(), value);
    }

    /**
     * Creates an AVP with a long value using automatic type deduction and flag handling.
     *
     * @param avpCode The AVP code constant from DiameterConstants
     * @param value   The long value
     * @return The created AVP with appropriate flags
     * @throws IllegalArgumentException if AVP code is unknown or type mismatch
     */
    public static AVP create(final int avpCode, final long value) {
        final AVPDefinition definition = getDefinition(avpCode);
        validateType(definition, Long.class);
        return createLongAVP(avpCode, definition.mandatory(), value);
    }

    /**
     * Creates an AVP with a string value using automatic type deduction and flag handling.
     *
     * @param avpCode The AVP code constant from DiameterConstants
     * @param value   The string value
     * @return The created AVP with appropriate flags
     * @throws IllegalArgumentException if AVP code is unknown or type mismatch
     */
    public static AVP create(final int avpCode, final String value) {
        final AVPDefinition definition = getDefinition(avpCode);
        validateType(definition, String.class);
        return new AVP(avpCode, definition.vendorSpecific(), definition.mandatory(),
                      false, definition.vendorId(), value.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Creates an AVP with a byte array value using automatic type deduction and flag handling.
     *
     * @param avpCode The AVP code constant from DiameterConstants
     * @param value   The byte array value
     * @return The created AVP with appropriate flags
     * @throws IllegalArgumentException if AVP code is unknown or type mismatch
     */
    public static AVP create(final int avpCode, final byte[] value) {
        final AVPDefinition definition = getDefinition(avpCode);
        validateType(definition, byte[].class);
        return new AVP(avpCode, definition.vendorSpecific(), definition.mandatory(),
                      false, definition.vendorId(), value);
    }

    /**
     * Gets the definition for the given AVP code.
     *
     * @param avpCode The AVP code
     * @return The AVP definition
     * @throws IllegalArgumentException if AVP code is not registered
     */
    private static AVPDefinition getDefinition(final int avpCode) {
        final AVPDefinition definition = registry.get(avpCode);
        if (definition == null) {
            throw new IllegalArgumentException("Unknown AVP code: " + avpCode +
                ". Make sure the appropriate AVPProvider is registered.");
        }
        return definition;
    }

    /**
     * Validates that the provided value type matches the expected AVP type.
     *
     * @param definition The AVP definition
     * @param valueType  The actual value type
     * @throws IllegalArgumentException if types don't match
     */
    private static void validateType(final AVPDefinition definition, final Class<?> valueType) {
        if (!definition.dataType().equals(valueType)) {
            throw new IllegalArgumentException(String.format(
                "Type mismatch for AVP %s (code %d): expected %s, got %s",
                definition.name(), definition.code(),
                definition.dataType().getSimpleName(), valueType.getSimpleName()));
        }
    }
}
