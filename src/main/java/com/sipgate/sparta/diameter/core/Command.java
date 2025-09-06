package com.sipgate.sparta.diameter.core;

import com.sipgate.sparta.diameter.DiameterException;
import com.sipgate.sparta.diameter.core.annotations.DiameterRequest;
import com.sipgate.sparta.diameter.core.annotations.DiameterResponse;
import com.sipgate.sparta.diameter.core.avp.AVP;
import com.sipgate.sparta.diameter.core.avp.mixins.HasDestinationHostAVP;
import com.sipgate.sparta.diameter.core.avp.mixins.HasDestinationRealmAVP;
import com.sipgate.sparta.diameter.core.avp.mixins.HasOriginHostAVP;
import com.sipgate.sparta.diameter.core.avp.mixins.HasOriginRealmAVP;
import org.reflections.Reflections;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * Base class for all Diameter commands (messages).
 * <p>
 * This class represents a Diameter command as defined in
 * <a href="https://datatracker.ietf.org/doc/html/rfc6733#section-3">RFC 6733, Section 3</a>.
 * It provides common functionality for handling the Diameter header and AVPs.
 * </p>
 */
@SuppressWarnings("rawtypes") // The parser cannot know ahead of time the exact type
public abstract class Command<T extends Command<T>> implements
    Selfable<T>,
    HasOriginHostAVP<T>,
    HasOriginRealmAVP<T>,
    HasDestinationHostAVP<T>,
    HasDestinationRealmAVP<T> {

    private static final Map<Integer, Class<? extends Request>> REQUEST_TYPES = new HashMap<>();
    private static final Map<Integer, Class<? extends Answer>> ANSWER_TYPES = new HashMap<>();
    private static final Set<String> PACKAGES_TO_SCAN = new HashSet<>();

    static {
        PACKAGES_TO_SCAN.add("com.sipgate.sparta.diameter.messages.rfc6733");
        initializeCommandTypes();
    }

    @SuppressWarnings("unchecked")
    private static void initializeCommandTypes() {
        // Clear existing mappings
        REQUEST_TYPES.clear();
        ANSWER_TYPES.clear();

        // Scan specified packages for classes annotated with @DiameterRequest and @DiameterResponse
        final Reflections reflections = new Reflections(PACKAGES_TO_SCAN);
        final Set<Class<?>> requestClasses = reflections.getTypesAnnotatedWith(DiameterRequest.class);
        for (final Class<?> cls : requestClasses) {
            if (Request.class.isAssignableFrom(cls)) {
                final DiameterRequest annotation = cls.getAnnotation(DiameterRequest.class);
                REQUEST_TYPES.put(annotation.value(), (Class<? extends Request>) cls);
            }
        }

        final Set<Class<?>> answerClasses = reflections.getTypesAnnotatedWith(DiameterResponse.class);
        for (final Class<?> cls : answerClasses) {
            if (Answer.class.isAssignableFrom(cls)) {
                final DiameterResponse annotation = cls.getAnnotation(DiameterResponse.class);
                ANSWER_TYPES.put(annotation.value(), (Class<? extends Answer>) cls);
            }
        }
    }

    // Diameter header fields
    private final int version;
    private final int commandCode;
    private final boolean request;
    private final boolean proxiable;
    private final boolean error;
    private boolean retransmitted;
    private final int applicationId;
    private final int hopByHopIdentifier;
    private final int endToEndIdentifier;

    // AVPs contained in this command
    private final List<AVP> avps;

    /**
     * Constructs a Diameter command with the specified parameters.
     *
     * @param commandCode        The command code of the message.
     * @param request            Indicates whether the message is a request.
     * @param proxiable          Indicates whether the message is proxiable.
     * @param error              Indicates whether the message is an error.
     * @param retransmitted      Indicates whether the message is retransmitted.
     * @param applicationId      The application ID of the message.
     * @param hopByHopIdentifier The hop-by-hop identifier.
     * @param endToEndIdentifier The end-to-end identifier.
     */
    protected Command(final int commandCode, final boolean request, final boolean proxiable,
                      final boolean error, final boolean retransmitted, final int applicationId,
                      final int hopByHopIdentifier, final int endToEndIdentifier) {
        this.version = 1; // Diameter version is always 1
        this.commandCode = commandCode;
        this.request = request;
        this.proxiable = proxiable;
        this.error = error;
        this.retransmitted = retransmitted;
        this.applicationId = applicationId;
        this.hopByHopIdentifier = hopByHopIdentifier;
        this.endToEndIdentifier = endToEndIdentifier;
        this.avps = new ArrayList<>();
    }

    /**
     * Retrieves the Diameter version.
     *
     * @return The Diameter version, which is always 1.
     */
    public int getVersion() { return version; }

    /**
     * Retrieves the command code of the message.
     *
     * @return The command code.
     */
    public int getCommandCode() { return commandCode; }

    /**
     * Checks if the message is a request.
     *
     * @return True if the message is a request, false otherwise.
     */
    public boolean isRequest() { return request; }

    /**
     * Checks if the message is proxiable.
     *
     * @return True if the message is proxiable, false otherwise.
     */
    public boolean isProxiable() { return proxiable; }

    /**
     * Checks if the message is an error.
     *
     * @return True if the message is an error, false otherwise.
     */
    public boolean isError() { return error; }

    /**
     * Checks if the message is retransmitted.
     *
     * @return True if the message is retransmitted, false otherwise.
     */
    public boolean isRetransmitted() { return retransmitted; }

    /**
     * Retrieves the application ID of the message.
     *
     * @return The application ID.
     */
    public int getApplicationId() { return applicationId; }

    /**
     * Retrieves the hop-by-hop identifier.
     *
     * @return The hop-by-hop identifier.
     */
    public int getHopByHopIdentifier() { return hopByHopIdentifier; }

    /**
     * Retrieves the end-to-end identifier.
     *
     * @return The end-to-end identifier.
     */
    public int getEndToEndIdentifier() { return endToEndIdentifier; }

    /**
     * Retrieves the list of AVPs contained in this command.
     *
     * @return A copy of the list of AVPs.
     */
    public List<AVP> getAVPs() { return new ArrayList<>(avps); }

    /**
     * Adds an AVP to this command.
     *
     * @param avp The AVP to add.
     */
    @Override
    public void addAVP(final AVP avp) {
        avps.add(avp);
    }

    /**
     * Adds or updates an AVP in this command, ensuring uniqueness by AVP code.
     * If an AVP with the same code already exists, it will be replaced.
     * Otherwise, the AVP will be added.
     *
     * @param avp The AVP to add or update.
     */
    @Override
    public void setAVP(final AVP avp) {
        // Find and remove existing AVP with the same code
        avps.removeIf(existingAvp -> existingAvp.getCode() == avp.getCode());
        // Add the new AVP
        avps.add(avp);
    }

    /**
     * Find an AVP by its code.
     *
     * @param code The AVP code to search for.
     * @return The AVP with the given code, or null if not found.
     */
    @Override
    public AVP findAVP(final int code) {
        for (final AVP avp : avps) {
            if (avp.getCode() == code) {
                return avp;
            }
        }
        return null;
    }

    /**
     * Find all AVPs with the given code.
     *
     * @param code The AVP code to search for.
     * @return A list of AVPs with the given code.
     */
    public List<AVP> findAVPs(final int code) {
        final List<AVP> result = new ArrayList<>();
        for (final AVP avp : avps) {
            if (avp.getCode() == code) {
                result.add(avp);
            }
        }
        return result;
    }

    /**
     * Calculate the total length of the message including header and all AVPs.
     *
     * @return The total length of the message in bytes.
     */
    protected int getMessageLength() {
        int length = 20; // Diameter header is 20 bytes
        for (final AVP avp : avps) {
            length += avp.getLength();
            // Add padding to 4-byte boundary
            final int padding = (4 - (avp.getLength() % 4)) % 4;
            length += padding;
        }
        return length;
    }

    protected void setRetransmissionFlag() {
        this.retransmitted = true;
    }

    /**
     * Writes this command to the given DataOutputStream.
     * This method serializes the Diameter header followed by all AVPs.
     *
     * @param outputStream The DataOutputStream to write to.
     * @throws IOException If an I/O error occurs while writing.
     */
    public void writeTo(final DataOutputStream outputStream) throws IOException {
        // Version (1 byte)
        outputStream.writeByte(version);

        // Message Length (3 bytes)
        final int messageLength = getMessageLength();
        outputStream.writeByte((messageLength >> 16) & 0xFF);
        outputStream.writeByte((messageLength >> 8) & 0xFF);
        outputStream.writeByte(messageLength & 0xFF);

        // Flags (1 byte)
        int flags = 0;
        if (request) flags |= 0x80;
        if (proxiable) flags |= 0x40;
        if (error) flags |= 0x20;
        if (retransmitted) flags |= 0x10; // T flag (RFC 6733)
        outputStream.writeByte(flags);

        // Command Code (3 bytes)
        outputStream.writeByte((commandCode >> 16) & 0xFF);
        outputStream.writeByte((commandCode >> 8) & 0xFF);
        outputStream.writeByte(commandCode & 0xFF);

        // Application-Id (4 bytes)
        outputStream.writeInt(applicationId);

        // Hop-by-Hop Identifier (4 bytes)
        outputStream.writeInt(hopByHopIdentifier);

        // End-to-End Identifier (4 bytes)
        outputStream.writeInt(endToEndIdentifier);

        // Write all AVPs
        for (final AVP avp : avps) {
            avp.writeTo(outputStream);
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
     * Parses a Diameter message from a ByteBuffer (useful for Netty integration).
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

        return parseMessage(buffer, messageLength);
    }

    /**
     * Parses a Diameter message from a DataInputStream.
     *
     * @param byteBuffer the DataInputStream containing the Diameter message
     * @param messageLength the length of the Diameter message
     * @return the parsed Command object representing the Diameter message
     * @throws DiameterException if an error occurs while parsing the message
     */
    private static Command parseMessage(final ByteBuffer byteBuffer, final int messageLength) throws DiameterException {
        try {
            // Read Diameter header (20 bytes)
            final int version = byteBuffer.get();
            if (version != DiameterConstants.DIAMETER_VERSION) {
                throw new DiameterException("Unsupported Diameter version: " + version);
            }
            // Skip already-read messageLength
            byteBuffer.position(byteBuffer.position() + 3);

            // Flags (1 byte)
            final int flags = byteBuffer.get();
            final boolean isRequest = (flags & 0x80) != 0;
            final boolean isProxiable = (flags & 0x40) != 0;
            final boolean isError = (flags & 0x20) != 0;
            final boolean isRetransmitted = (flags & 0x10) != 0; // T flag (RFC 6733)

            // Command Code (3 bytes)
            final int commandCode = (byteBuffer.get() << 16) |
                    (byteBuffer.get() << 8) |
                    byteBuffer.get();

            // Application-Id (4 bytes)
            final int applicationId = byteBuffer.getInt();

            // Hop-by-Hop Identifier (4 bytes)
            final int hopByHopId = byteBuffer.getInt();

            // End-to-End Identifier (4 bytes)
            final int endToEndId = byteBuffer.getInt();

            // Parse AVPs (remaining bytes)
            final List<AVP> avps = parseAVPs(byteBuffer, messageLength - 20);

            // Create appropriate message type
            final Class<? extends Command> commandClass = isRequest
                    ? REQUEST_TYPES.get(commandCode)
                    : ANSWER_TYPES.get(commandCode);

            final String createMethod = isRetransmitted ? "createRetransmitted" : "create";
            final Command command = create(createMethod, commandClass, hopByHopId, endToEndId);

            if (command == null) {
                throw new DiameterException(String.format("Unsupported Diameter command code %s for app-id: %s", commandCode, applicationId));
            }

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
     *
     * @param byteBuffer the ByteBuffer to read from
     * @param remainingLength the number of bytes remaining in the message after the header
     * @return a list of parsed AVPs
     * @throws IOException if an I/O error occurs while reading the AVPs
     */
    private static List<AVP> parseAVPs(final ByteBuffer byteBuffer, final int remainingLength)
            throws IOException {
        final List<AVP> avps = new ArrayList<>();
        int bytesRead = 0;

        while (bytesRead < remainingLength) {
            final AVP avp = AVP.readFrom(byteBuffer);
            avps.add(avp);
            // Account for padding to 4-byte boundary
            final int padding = (4 - (avp.getLength() % 4)) % 4;
            bytesRead += avp.getLength() + padding;
        }

        return avps;
    }

    private static Command create(final String methodName, final Class<? extends Command> requestClass, final int hopByHopId, final int endToEndId) {
        try {
            return (Command) requestClass.getMethod(methodName, int.class, int.class)
                    .invoke(null, hopByHopId, endToEndId);
        } catch (final Exception e) {
            return null;
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();

        // Command name from class simple name
        final String commandName = this.getClass().getSimpleName();

        // Format flags
        final StringBuilder flagsStr = new StringBuilder();
        if (request) flagsStr.append("R");
        if (proxiable) flagsStr.append("P");
        if (error) flagsStr.append("E");
        if (retransmitted) flagsStr.append("T");
        while (flagsStr.length() < 3) flagsStr.append("-");

        // Header line
        sb.append(String.format("%s <Version: 0x%02x, Length: %d, Flags: 0x%02x (%s), Hop-by-Hop Identifier: 0x%x, End-to-End Identifier: 0x%x>",
                commandName,
                version,
                getMessageLength(),
                getFlagsValue(),
                flagsStr,
                hopByHopIdentifier,
                endToEndIdentifier));

        // Add AVPs
        for (final AVP avp : avps) {
            sb.append("\n  ").append(avp.toString());
        }

        return sb.toString();
    }

    private int getFlagsValue() {
        int flags = 0;
        if (request) flags |= 0x80;
        if (proxiable) flags |= 0x40;
        if (error) flags |= 0x20;
        if (retransmitted) flags |= 0x10;
        return flags;
    }
}
