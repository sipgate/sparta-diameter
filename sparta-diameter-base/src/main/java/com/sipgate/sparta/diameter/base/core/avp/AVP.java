package com.sipgate.sparta.diameter.base.core.avp;

import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(AVP.class);

    private final long code;
    private final boolean vendorSpecific;
    private final boolean mandatory;
    private final boolean protectedAVP;
    private final long vendorId;
    protected final byte[] data;

    // Factory functionality merged from AVPFactory
    private static final Map<AVPKey, AVPDefinition> registry = new ConcurrentHashMap<>();

    static {
        final var reflections = new Reflections("com.sipgate.sparta.diameter");
        for (final var cls : reflections.getSubTypesOf(AVPProvider.class)) {
            try {
                registerProvider(cls.getDeclaredConstructor().newInstance());
            } catch (final Exception e) {
                throw new IllegalStateException("Cannot instantiate " + cls.getName(), e);
            }
        }
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
    AVP(final long code, final boolean vendorSpecific, final boolean mandatory, final boolean protectedAVP,
        final long vendorId, final byte[] data) {
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
    AVP(final long code, final boolean mandatory, final byte[] data) {
        this(code, false, mandatory, false, 0L, data);
    }

    /**
     * Creates an AVP with full manual specification. Ideally you should use one of the type-specific
     * create() methods instead, and register custom types via registerProvider() if needed.
     *
     * @param key The AVP key (code + vendor ID).
     * @param vendorSpecific Whether the AVP is vendor-specific.
     * @param mandatory Whether the AVP is mandatory.
     * @param protectedAVP Whether the AVP is protected.
     * @param data The AVP data.
     * @return The created AVP.
     */
    public static AVP createRaw(final AVPKey key, final boolean vendorSpecific, final boolean mandatory, final boolean protectedAVP, final byte[] data) {
        return new AVP(key.code(), vendorSpecific, mandatory, protectedAVP, key.vendorId(), data);
    }

    /**
     * Retrieves the AVP code.
     *
     * @return The AVP code.
     */
    public long getCode() {
        return code;
    }

    public boolean isSameKey(final AVP other) {
        return other.getCode() == this.getCode() && other.getVendorId() == this.getVendorId();
    }

    public boolean isSameKey(final AVPKey key) {
        return key.code() == this.getCode() && key.vendorId() == this.getVendorId();
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
    public long getVendorId() {
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
            if (data.length < 6) { // Minimum length for Address AVP is 6 bytes
                throw new IllegalArgumentException("AVP data must be at least 6 bytes to read as IP address");
            }

            final int addressType = ((data[0] & 0xFF) << 8) | (data[1] & 0xFF);

            if (addressType == 1) {
                return InetAddress.getByAddress(new byte[] {data[2], data[3], data[4], data[5]});
            }

            if (addressType == 2) {
                if (data.length < 18) { // IPv6 address requires at least 18 bytes
                    throw new IllegalArgumentException("AVP data must be at least 18 bytes to read as IPv6 address");
                }
                final byte[] ipv6Bytes = new byte[16];
                System.arraycopy(data, 2, ipv6Bytes, 0, 16);
                return InetAddress.getByAddress(ipv6Bytes);
            }

            throw new IllegalArgumentException("Unsupported address type: " + addressType);
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


    public int calculateLength() {
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
        // AVP Code (4 bytes, 32-bit unsigned)
        outputStream.writeInt((int) code);

        // Flags (1 byte)
        int flags = 0;
        if (vendorSpecific) flags |= 0x80;
        if (mandatory) flags |= 0x40;
        if (protectedAVP) flags |= 0x20;
        outputStream.writeByte(flags);

        // Length (3 bytes) - includes header + data
        final int length = calculateLength();
        outputStream.writeByte((length >> 16) & 0xFF);
        outputStream.writeByte((length >> 8) & 0xFF);
        outputStream.writeByte(length & 0xFF);

        // Vendor-Id (4 bytes, 32-bit unsigned, if vendor-specific)
        if (vendorSpecific) {
            outputStream.writeInt((int) vendorId);
        }

        // Data
        writeDataTo(outputStream);

        // Padding to 4-byte boundary
        final int padding = (4 - (length % 4)) % 4;
        for (int i = 0; i < padding; i++) {
            outputStream.writeByte(0);
        }
    }

    protected void writeDataTo(final DataOutputStream outputStream) throws IOException {
        outputStream.write(data);
    }

    /**
     * Creates an AVP with a 32-bit signed integer value.
     *
     * @param definition the AVP definition providing code, flags, and vendor ID
     * @param value      the integer value
     * @return the created AVP
     */
    private static AVP createIntegerAVP(final AVPDefinition definition, final int value) {
        return new AVP(definition.code(), definition.vendorSpecific(), definition.mandatory(),
                       false, definition.vendorId(), intToBytes(value));
    }

    /**
     * Creates an AVP with a UTF-8 string value.
     *
     * @param definition the AVP definition providing code, flags, and vendor ID
     * @param value      the string value
     * @return the created AVP
     */
    private static AVP createStringAVP(final AVPDefinition definition, final String value) {
        return new AVP(definition.code(), definition.vendorSpecific(), definition.mandatory(),
                       false, definition.vendorId(), value.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Creates an AVP with a 32-bit unsigned integer value.
     *
     * @param definition the AVP definition providing code, flags, and vendor ID
     * @param value      the unsigned integer value as long
     * @return the created AVP
     */
    private static AVP createUnsignedIntAVP(final AVPDefinition definition, final long value) {
        return new AVP(definition.code(), definition.vendorSpecific(), definition.mandatory(),
                       false, definition.vendorId(), unsignedIntToBytes(value));
    }

    /**
     * Creates an AVP with a 64-bit unsigned integer value.
     *
     * @param definition the AVP definition providing code, flags, and vendor ID
     * @param value      the unsigned long value as BigInteger
     * @return the created AVP
     */
    private static AVP createUnsignedLongAVP(final AVPDefinition definition, final BigInteger value) {
        return new AVP(definition.code(), definition.vendorSpecific(), definition.mandatory(),
                       false, definition.vendorId(), unsignedLongToBytes(value));
    }

    /**
     * Creates an AVP with a 32-bit IEEE 754 float value.
     *
     * @param definition the AVP definition providing code, flags, and vendor ID
     * @param value      the float value
     * @return the created AVP
     */
    private static AVP createFloatAVP(final AVPDefinition definition, final float value) {
        return new AVP(definition.code(), definition.vendorSpecific(), definition.mandatory(),
                       false, definition.vendorId(), floatToBytes(value));
    }

    /**
     * Creates an AVP with a 64-bit IEEE 754 double value.
     *
     * @param definition the AVP definition providing code, flags, and vendor ID
     * @param value      the double value
     * @return the created AVP
     */
    private static AVP createDoubleAVP(final AVPDefinition definition, final double value) {
        return new AVP(definition.code(), definition.vendorSpecific(), definition.mandatory(),
                       false, definition.vendorId(), doubleToBytes(value));
    }

    /**
     * Creates an AVP with an enumerated value (encoded as a 32-bit integer).
     *
     * @param definition the AVP definition providing code, flags, and vendor ID
     * @param value      the enumerated value
     * @return the created AVP
     */
    private static AVP createEnumeratedAVP(final AVPDefinition definition, final int value) {
        return new AVP(definition.code(), definition.vendorSpecific(), definition.mandatory(),
                       false, definition.vendorId(), intToBytes(value));
    }

    /**
     * Creates an AVP with a Diameter Time value (NTP timestamp).
     *
     * @param definition the AVP definition providing code, flags, and vendor ID
     * @param value      the Date value
     * @return the created AVP
     */
    private static AVP createTimeAVP(final AVPDefinition definition, final Date value) {
        return new AVP(definition.code(), definition.vendorSpecific(), definition.mandatory(),
                       false, definition.vendorId(), timeToBytes(value));
    }

    /**
     * Creates an AVP with a Diameter Identity value (domain name string).
     *
     * @param definition the AVP definition providing code, flags, and vendor ID
     * @param value      the domain name string
     * @return the created AVP
     */
    private static AVP createDiameterIdentityAVP(final AVPDefinition definition, final String value) {
        return new AVP(definition.code(), definition.vendorSpecific(), definition.mandatory(),
                       false, definition.vendorId(), value.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Creates an AVP with a Diameter URI value.
     *
     * @param definition the AVP definition providing code, flags, and vendor ID
     * @param value      the URI string
     * @return the created AVP
     */
    private static AVP createDiameterURIAVP(final AVPDefinition definition, final String value) {
        return new AVP(definition.code(), definition.vendorSpecific(), definition.mandatory(),
                       false, definition.vendorId(), value.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Creates an AVP with an IP Address value.
     * The address is encoded with a 2-byte address-family prefix (1 = IPv4, 2 = IPv6)
     * followed by the raw address bytes, per RFC 6733.
     *
     * @param definition the AVP definition providing code, flags, and vendor ID
     * @param value      the InetAddress value
     * @return the created AVP
     * @throws IllegalArgumentException if the address type is neither IPv4 nor IPv6
     */
    private static AVP createIPAddressAVP(final AVPDefinition definition, final InetAddress value) {
        final int addressType = value instanceof Inet4Address ? 1 :
                                value instanceof Inet6Address ? 2 : 0;

        if (addressType == 0) {
            throw new IllegalArgumentException("Unsupported InetAddress type");
        }

        final byte[] addressBytes = ipAddressToBytes(value);
        final byte[] addressData = new byte[2 + addressBytes.length];
        addressData[0] = 0x00;
        addressData[1] = (byte) (addressType & 0xFF);
        System.arraycopy(addressBytes, 0, addressData, 2, addressBytes.length);

        return new AVP(definition.code(), definition.vendorSpecific(), definition.mandatory(),
                       false, definition.vendorId(), addressData);
    }

    /**
     * Creates an AVP with an OctetString value.
     *
     * @param definition the AVP definition providing code, flags, and vendor ID
     * @param value      the byte array value
     * @return the created AVP
     */
    private static AVP createOctetStringAVP(final AVPDefinition definition, final byte[] value) {
        return new AVP(definition.code(), definition.vendorSpecific(), definition.mandatory(),
                       false, definition.vendorId(), value);
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
     * Registers all AVP definitions from the given provider. Re-registering an identical definition
     * (same key, name, type and flags) is a no-op, so a common AVP may be declared by several
     * providers (e.g. an interface-specific provider and the shared 3GPP provider). Only a genuine
     * conflict — two different definitions for the same (code, vendorId) — is rejected.
     *
     * @param provider The AVP provider to register
     * @throws IllegalStateException if two different definitions share a (code, vendorId)
     */
    public static void registerProvider(final AVPProvider provider) {
        for (final AVPDefinition definition : provider.getDefinitions()) {
            final var key = new AVPKey(definition.code(), definition.vendorId());
            final var existing = registry.putIfAbsent(key, definition);
            if (existing != null && !existing.equals(definition)) {
                throw new IllegalStateException(String.format(
                    "Duplicate AVP registration for key (code=%d, vendorId=%d): '%s' conflicts with already-registered '%s'",
                    key.code(), key.vendorId(), definition.name(), existing.name()));
            }
        }
        LOGGER.debug("registered {}", provider.getClass().getName());
    }

    /**
     * Creates an AVP with a long value (Unsigned32) using automatic type deduction and flag handling.
     *
     * @param key   The AVP key (code + vendor ID)
     * @param value The long value
     * @return The created AVP with appropriate flags
     * @throws IllegalArgumentException if the key is unknown or there is a type mismatch
     */
    public static AVP create(final AVPKey key, final long value) {
        final var definition = getDefinition(key);
        validateType(definition, Long.class);
        return createUnsignedIntAVP(definition, value);
    }

    /**
     * Creates an AVP with a string value using automatic type deduction and flag handling.
     *
     * @param key   The AVP key (code + vendor ID)
     * @param value The string value
     * @return The created AVP with appropriate flags
     * @throws IllegalArgumentException if the key is unknown or there is a type mismatch
     */
    public static AVP create(final AVPKey key, final String value) {
        final var definition = getDefinition(key);
        validateType(definition, String.class);
        return new AVP(key.code(), definition.vendorSpecific(), definition.mandatory(),
                      false, key.vendorId(), value.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Creates an AVP with a byte array value using automatic type deduction and flag handling.
     *
     * @param key   The AVP key (code + vendor ID)
     * @param value The byte array value
     * @return The created AVP with appropriate flags
     * @throws IllegalArgumentException if the key is unknown or there is a type mismatch
     */
    public static AVP create(final AVPKey key, final byte[] value) {
        final var definition = getDefinition(key);
        validateType(definition, byte[].class);
        return new AVP(key.code(), definition.vendorSpecific(), definition.mandatory(),
                      false, key.vendorId(), value);
    }

    /**
     * Creates an AVP with a BigInteger value (Unsigned64) using automatic type deduction and flag handling.
     *
     * @param key   The AVP key (code + vendor ID)
     * @param value The BigInteger value
     * @return The created AVP with appropriate flags
     * @throws IllegalArgumentException if the key is unknown or there is a type mismatch
     */
    public static AVP create(final AVPKey key, final BigInteger value) {
        final var definition = getDefinition(key);
        validateType(definition, BigInteger.class);
        return createUnsignedLongAVP(definition, value);
    }

    /**
     * Creates an AVP with an enumerated integer value using automatic type deduction and flag handling.
     *
     * @param key   The AVP key (code + vendor ID)
     * @param value The enumerated integer value
     * @return The created AVP with appropriate flags
     * @throws IllegalArgumentException if the key is unknown or there is a type mismatch
     */
    public static AVP create(final AVPKey key, final int value) {
        final var definition = getDefinition(key);
        validateType(definition, Integer.class);
        return createEnumeratedAVP(definition, value);
    }

    /**
     * Creates an AVP with an InetAddress value using automatic type deduction and flag handling.
     *
     * @param key   The AVP key (code + vendor ID)
     * @param value The InetAddress value
     * @return The created AVP with appropriate flags
     * @throws IllegalArgumentException if the key is unknown or there is a type mismatch
     */
    public static AVP create(final AVPKey key, final InetAddress value) {
        final var definition = getDefinition(key);
        validateType(definition, InetAddress.class);
        return createIPAddressAVP(definition, value);
    }

    /**
     * Creates an AVP with a Date value (Diameter Time) using automatic type deduction and flag handling.
     *
     * @param key   The AVP key (code + vendor ID)
     * @param value The Date value
     * @return The created AVP with appropriate flags
     * @throws IllegalArgumentException if the key is unknown or there is a type mismatch
     */
    public static AVP create(final AVPKey key, final Date value) {
        final var definition = getDefinition(key);
        validateType(definition, Date.class);
        return createTimeAVP(definition, value);
    }

    /**
     * Creates a grouped AVP with nested AVPs using automatic type deduction and flag handling.
     *
     * @param key  The AVP key (code + vendor ID)
     * @param avps The list of nested AVPs
     * @return The created AVP with appropriate flags
     * @throws IllegalArgumentException if the key is unknown or there is a type mismatch
     */
    public static AVP create(final AVPKey key, final List<AVP> avps) {
        final var definition = getDefinition(key);
        validateType(definition, GroupedAVP.class);
        return new GroupedAVP(key.code(), definition.vendorSpecific(), definition.mandatory(),
                              false, key.vendorId(), avps);
    }

    /**
     * Gets the definition for the given AVP key.
     *
     * @param key The AVP key (code + vendor ID)
     * @return The AVP definition
     * @throws IllegalArgumentException if the key is not registered
     */
    private static AVPDefinition getDefinition(final AVPKey key) {
        final var definition = registry.get(key);
        if (definition == null) {
            throw new IllegalArgumentException(String.format(
                "Unknown AVP key (code=%d, vendorId=%d). Make sure the appropriate AVPProvider is registered.",
                key.code(), key.vendorId()));
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

    /**
     * Reads an AVP from the given ByteBuffer.
     *
     * <p>Detects three generic parse-time violations and throws {@link AVPParseException}:
     * <ul>
     *   <li>Reserved flag bits set (bits 4–0 non-zero) → result code 5016</li>
     *   <li>AVP length field out of range → result code 5014</li>
     *   <li>Unrecognized AVP with M-bit set → result code 5001</li>
     * </ul>
     *
     * <p>The exception thrown here carries only the offending AVP; the message header
     * context (command code, hop-by-hop, etc.) is injected by
     * {@link com.sipgate.sparta.diameter.base.core.Command#parseMessage} before the
     * exception reaches the session layer.
     *
     * @param buffer The ByteBuffer to read from.
     * @return The constructed AVP.
     * @throws EOFException       If the buffer does not contain enough bytes for an AVP header.
     * @throws AVPParseException  If the AVP violates RFC 6733 in a detectable way.
     */
    public static AVP readFrom(final ByteBuffer buffer) throws EOFException, AVPParseException {
        if (buffer.remaining() < 8) {
            throw new EOFException("Buffer does not contain enough data for an AVP header");
        }

        // AVP Code (4 bytes, 32-bit unsigned)
        final long code = Integer.toUnsignedLong(buffer.getInt());

        // Flags (1 byte)
        final byte flags = buffer.get();
        final boolean vendorSpecific = (flags & 0x80) != 0;
        final boolean mandatory = (flags & 0x40) != 0;
        final boolean protectedAVP = (flags & 0x20) != 0;
        // Length (3 bytes) - read the next 3 bytes after flags
        final int length = ((buffer.get() & 0xFF) << 16) | ((buffer.get() & 0xFF) << 8) | (buffer.get() & 0xFF);
        if (length < 8 || length > buffer.remaining() + 8) {
            // RFC 6733 §4.1: length field out of valid range
            // We think that this is an edge case and the RFC doesn't cover all cases.
            // We have decided to simply answer with a stub AVP without payload.
            final var stub = new AVP(code, vendorSpecific, mandatory, protectedAVP, 0L, new byte[0]);
            throw new AVPParseException(DiameterConstants.RES_DIAMETER_INVALID_AVP_LENGTH, stub);
        }

        // Vendor-Id (4 bytes, 32-bit unsigned, if vendor-specific)
        final long vendorId = vendorSpecific ? Integer.toUnsignedLong(buffer.getInt()) : 0L;

        // Data
        final int dataLength = length - (vendorSpecific ? 12 : 8);
        final byte[] data = new byte[dataLength];
        buffer.get(data);

        // Skip padding to 4-byte boundary
        final int padding = (4 - (dataLength % 4)) % 4;
        buffer.position(buffer.position() + padding);

        final var key = new AVPKey(code, vendorId);
        final AVPDefinition definition = registry.get(key);
        if (definition == null) {
            LOGGER.warn("AVP unsupported: {}", key);
            if (mandatory) {
                // RFC 6733 §1.3.4: unrecognized AVP with M-bit set MUST trigger 5001
                throw new AVPParseException(DiameterConstants.RES_DIAMETER_AVP_UNSUPPORTED,
                    new AVP(code, vendorSpecific, mandatory, protectedAVP, vendorId, data));
            }
        } else if (definition.dataType().equals(GroupedAVP.class)) {
            // Parse grouped AVP
            final ByteBuffer dataBuffer = ByteBuffer.wrap(data);
            final List<AVP> nestedAVPs = new ArrayList<>();
            while (dataBuffer.remaining() >= 8) {
                nestedAVPs.add(readFrom(dataBuffer));
            }
            return new GroupedAVP(code, vendorSpecific, mandatory, protectedAVP, vendorId, nestedAVPs);
        }

        return new AVP(code, vendorSpecific, mandatory, protectedAVP, vendorId, data);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();

        final AVPDefinition definition = registry.get(new AVPKey(this.code, this.vendorId));
        if (definition != null) {
            sb.append(definition.name());
        } else {
            sb.append("Unknown-AVP-").append(this.code);
        }

        sb.append(" <Code: 0x").append(Long.toHexString(code).toLowerCase());
        sb.append(", Flags: 0x").append(Integer.toHexString(getFlagsValue()).toLowerCase());
        sb.append(" (").append(getFlagsString()).append(")");
        sb.append(", Length: ").append(calculateLength());
        sb.append(", Val: ").append(getValueString()).append(">");

        return sb.toString();
    }

    private int getFlagsValue() {
        int flags = 0;
        if (vendorSpecific) flags |= 0x80;
        if (mandatory) flags |= 0x40;
        if (protectedAVP) flags |= 0x20;
        return flags;
    }

    private String getFlagsString() {
        final StringBuilder flagsStr = new StringBuilder();
        flagsStr.append(vendorSpecific ? "V" : "-");
        flagsStr.append(mandatory ? "M" : "-");
        flagsStr.append(protectedAVP ? "P" : "-");
        return flagsStr.toString();
    }

    private String getValueString() {
        try {
            final var definition = getDefinition(new AVPKey(this.code, this.vendorId));
            final Class<?> dataType = definition.dataType();

            if (dataType.equals(String.class)) {
                return "'" + getDataAsString() + "'";
            } else if (dataType.equals(Integer.class)) {
                return String.valueOf(getDataAsInt());
            } else if (dataType.equals(Long.class)) {
                return String.valueOf(getDataAsUnsignedInt());
            } else if (dataType.equals(InetAddress.class)) {
                final InetAddress addr = getDataAsIPAddress();
                final int family = addr instanceof Inet4Address ? 1 : 2;
                return "(" + family + ", '" + addr.getHostAddress() + "')";
            } else if (dataType.equals(byte[].class)) {
                return "b'" + new String(data, StandardCharsets.UTF_8) + "'";
            } else if (dataType.equals(GroupedAVP.class)) {
                return "[Grouped AVP with " + ((GroupedAVP) this).getAVPs().size() + " AVPs]";
            }
        } catch (final Exception e) {
            // Fall back to raw data representation
        }

        // Default: show as raw bytes or integer if possible
        if (data.length == 4) {
            return String.valueOf(getDataAsInt());
        } else {
            return "b'" + new String(data, StandardCharsets.UTF_8) + "'";
        }
    }
}
