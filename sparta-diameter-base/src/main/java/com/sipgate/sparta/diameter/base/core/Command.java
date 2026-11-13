package com.sipgate.sparta.diameter.base.core;

import com.sipgate.sparta.diameter.base.DiameterException;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.AVPParseException;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasOriginHostAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasOriginRealmAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasSessionIdAVP;


import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class for all Diameter commands (messages).
 * <p>
 * This class represents a Diameter command as defined in
 * <a href="https://datatracker.ietf.org/doc/html/rfc6733#section-3">RFC 6733, Section 3</a>.
 * It provides common functionality for handling the Diameter header and AVPs.
 * </p>
 */
public abstract class Command implements
    HasSessionIdAVP,
    HasOriginHostAVP,
    HasOriginRealmAVP {

    // Diameter header fields
    private final int version;
    private final int commandCode;
    private final boolean request;
    private final boolean proxiable;
    private final boolean error;
    private boolean retransmitted;
    private final int applicationId;

    // AVPs contained in this command
    private final List<AVP> avps;

    /**
     * Header flags and Application-Id of the message currently being parsed on this thread.
     * Typed incoming classes pass class constants (e.g. proxiable=true) to the constructor; while
     * {@link #parseMessage} builds the command, the first {@code Command} constructed on the thread
     * takes these wire values instead, so a parsed message reflects what was actually received.
     */
    private static final ThreadLocal<WireHeader> PARSED_WIRE_HEADER = new ThreadLocal<>();

    private record WireHeader(boolean request, boolean proxiable, boolean error,
                              boolean retransmitted, int applicationId) {
    }

    /**
     * Constructs a Diameter command with the specified parameters.
     * <p>
     * When constructed while {@link #parseMessage} builds a received message, the header flags
     * and Application-Id are taken from the wire instead of the given arguments.
     * </p>
     *
     * @param commandCode   The command code of the message.
     * @param request       Indicates whether the message is a request.
     * @param proxiable     Indicates whether the message is proxiable.
     * @param error         Indicates whether the message is an error.
     * @param retransmitted Indicates whether the message is retransmitted.
     * @param applicationId The application ID of the message.
     */
    protected Command(final int commandCode, final boolean request, final boolean proxiable,
                      final boolean error, final boolean retransmitted, final int applicationId) {
        this.version = 1; // Diameter version is always 1
        this.commandCode = commandCode;
        final WireHeader wireHeader = PARSED_WIRE_HEADER.get();
        if (wireHeader != null) {
            // consumed once: only the parsed command itself takes the wire values
            PARSED_WIRE_HEADER.remove();
            this.request = wireHeader.request();
            this.proxiable = wireHeader.proxiable();
            this.error = wireHeader.error();
            this.retransmitted = wireHeader.retransmitted();
            this.applicationId = wireHeader.applicationId();
        } else {
            this.request = request;
            this.proxiable = proxiable;
            this.error = error;
            this.retransmitted = retransmitted;
            this.applicationId = applicationId;
        }
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
     * Retrieves the list of AVPs contained in this command.
     *
     * @return A copy of the list of AVPs.
     */
    public List<AVP> getAVPs() { return new ArrayList<>(avps); }

    /**
     * Adds an AVP to this command at first position (RFX 6733 requires that Session-Id AVP is the first)
     *
     * @param avp The AVP to add
     */
    public void unshiftAvp(final AVP avp) {
        avps.add(0, avp);
    }

    /**
     * Adds an AVP to this command.
     *
     * @param avp The AVP to add.
     */
    @Override
    public void addAVP(final AVP avp) {
        if (this instanceof IncomingCommand) {
            throw new UnsupportedOperationException("Cannot mutate a wire-parsed incoming command");
        }
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
        if (this instanceof IncomingCommand) {
            throw new UnsupportedOperationException("Cannot mutate a wire-parsed incoming command");
        }
        avps.removeIf(avp::isSameKey);
        avps.add(avp);
    }

    /**
     * Find an AVP by its key (code + vendor ID).
     *
     * @param key The AVP key to search for.
     * @return The AVP with the given key, or null if not found.
     */
    @Override
    public AVP findAVP(final AVPKey key) {
        for (final AVP avp : avps) {
            if (avp.isSameKey(key)) {
                return avp;
            }
        }
        return null;
    }

    /**
     * Find all AVPs with the given key (code + vendor ID).
     *
     * @param key The AVP key to search for.
     * @return A list of AVPs with the given key.
     */
    @Override
    public List<AVP> findAVPs(final AVPKey key) {
        final List<AVP> result = new ArrayList<>();
        for (final AVP avp : avps) {
            if (avp.isSameKey(key)) {
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
            length += avp.calculateLength();
            final int padding = (4 - (avp.calculateLength() % 4)) % 4;
            length += padding;
        }
        return length;
    }

    /**
     * Returns a human-readable name for this command, suitable for log output.
     * <p>
     * The default implementation returns the fully-qualified class name, which is always
     * correct, always unique, and grep-able in source. Concrete types may override this
     * to return a prettier protocol-level name (e.g. {@code "Device-Watchdog"}).
     * </p>
     *
     * @return a human-readable command name; never {@code null}.
     */
    public String getCommandName() {
        return getClass().getName();
    }

    protected void setRetransmissionFlag() {
        this.retransmitted = true;
    }

    /**
     * Writes the 20-byte Diameter header with the supplied hop-by-hop and end-to-end identifiers.
     * Subclasses call this from their {@code writeTo} implementations to serialize outgoing messages.
     */
    protected void writeTo(final DataOutputStream outputStream,
                           final HopByHopId hopByHop, final EndToEndId endToEnd) throws IOException {
        outputStream.writeByte(version);

        final int messageLength = getMessageLength();
        outputStream.writeByte((messageLength >> 16) & 0xFF);
        outputStream.writeByte((messageLength >> 8) & 0xFF);
        outputStream.writeByte(messageLength & 0xFF);

        int flags = 0;
        if (request) flags |= 0x80;
        if (proxiable) flags |= 0x40;
        if (error) flags |= 0x20;
        if (retransmitted) flags |= 0x10; // T flag (RFC 6733)
        outputStream.writeByte(flags);

        outputStream.writeByte((commandCode >> 16) & 0xFF);
        outputStream.writeByte((commandCode >> 8) & 0xFF);
        outputStream.writeByte(commandCode & 0xFF);

        outputStream.writeInt(applicationId);
        outputStream.writeInt(hopByHop.value());
        outputStream.writeInt(endToEnd.value());

        for (final AVP avp : avps) {
            avp.writeTo(outputStream);
        }
    }

    /**
     * Extracts the message length from a ByteBuffer without changing the buffer position.
     */
    public static int getMessageLength(final ByteBuffer buffer) throws DiameterException {
        if (buffer.remaining() < 4) {
            throw new DiameterException("Need at least 4 bytes to read message length");
        }

        final int position = buffer.position();
        buffer.get(); // skip version
        final int length = ((buffer.get() & 0xFF) << 16) |
                ((buffer.get() & 0xFF) << 8) |
                (buffer.get() & 0xFF);
        buffer.position(position);

        return length;
    }

    /**
     * Parses a Diameter message from a ByteBuffer and returns it as an {@link IncomingCommand}.
     * Strict endpoint parsing: an unrecognized M-bit AVP in a known command fails with 5001.
     */
    public static IncomingCommand parseMessage(final ByteBuffer buffer) throws DiameterException {
        return parseMessage(buffer, false);
    }

    /**
     * Parses a Diameter message from a ByteBuffer and returns it as an {@link IncomingCommand}.
     *
     * @param relay {@code true} on a relay agent: in a proxiable (P-bit) message an unrecognized
     *              M-bit AVP is kept opaque instead of failing with 5001 (RFC 6733 §2.8.1/§6.1:
     *              relays forward AVPs they do not understand). Non-proxiable messages
     *              (CER/DWR/DPR, consumed locally) stay strict.
     */
    public static IncomingCommand parseMessage(final ByteBuffer buffer, final boolean relay) throws DiameterException {
        final int messageLength = getMessageLength(buffer);
        if (buffer.remaining() < messageLength) {
            throw new DiameterException("Invalid Diameter message: too short");
        }

        return parseMessage(buffer, messageLength, relay);
    }

    private static IncomingCommand parseMessage(final ByteBuffer byteBuffer,
                                                final int messageLength,
                                                final boolean relay) throws DiameterException {
        try {
            // Read the complete 20-byte header before any error checks, so that
            // DiameterResultCodeException and AVPParseException can carry all header
            // fields needed to build the error answer.
            final int version = byteBuffer.get();                      // byte 0

            byteBuffer.position(byteBuffer.position() + 3);           // skip bytes 1-3 (length)

            final int rawFlags = byteBuffer.get() & 0xFF;             // byte 4
            final boolean isRequest = (rawFlags & 0x80) != 0;
            final boolean proxiable = (rawFlags & 0x40) != 0;
            final boolean isError = (rawFlags & 0x20) != 0;
            final boolean isRetransmitted = (rawFlags & 0x10) != 0;

            final int commandCode = ((byteBuffer.get() & 0xFF) << 16) | // bytes 5-7
                    ((byteBuffer.get() & 0xFF) << 8) |
                    (byteBuffer.get() & 0xFF);

            final int applicationId = byteBuffer.getInt();            // bytes 8-11
            final HopByHopId hopByHop = new HopByHopId(byteBuffer.getInt());   // bytes 12-15
            final EndToEndId endToEnd = new EndToEndId(byteBuffer.getInt());    // bytes 16-19

            if (version != DiameterConstants.DIAMETER_VERSION) {
                throw new DiameterResultCodeException(
                        DiameterConstants.RES_DIAMETER_UNSUPPORTED_VERSION, isRequest,
                        commandCode, proxiable, applicationId, hopByHop, endToEnd, null, null);
            }

            // Typed In classes hard-code their header flags and Application-Id; the received
            // message must reflect what was actually on the wire (e.g. a P=0 request must not be
            // presented as proxiable and thus relayed), so the constructor takes them from here.
            final IncomingCommand command;
            PARSED_WIRE_HEADER.set(new WireHeader(isRequest, proxiable, isError, isRetransmitted, applicationId));
            try {
                command = DiameterMessageFactory.createForParsing(
                        commandCode, applicationId, isRequest, proxiable, isError, hopByHop, endToEnd, isRetransmitted);
            } finally {
                PARSED_WIRE_HEADER.remove();
            }
            // A command no factory knows is never interpreted locally: it is relayed opaquely
            // (fallback handler) or answered 3001. Its AVPs are not ours to judge, so an
            // unrecognized M-bit AVP must not turn it into a 5001 (RFC 6733 §2.8.1: relays do
            // not need to understand non-routing AVPs) - e.g. SWx ANID/Non-3GPP-User-Data on a
            // DRA without the SWx dictionary. The same holds for a known proxiable command on a
            // relay node: it is forwarded, not interpreted.
            final boolean opaque = command instanceof GenericRequest || command instanceof GenericAnswer.In
                    || (relay && proxiable);

            final List<AVP> avps = new ArrayList<>();
            try {
                parseAVPs(byteBuffer, messageLength - 20, avps, opaque);
            } catch (final AVPParseException e) {
                final var sessionIdKey = new AVPKey(DiameterConstants.AVP_SESSION_ID, 0);
                final var sessionId = avps.stream()
                    .filter(avp -> avp.isSameKey(sessionIdKey))
                    .findFirst()
                    .map(AVP::getDataAsString)
                    .orElse(null);

                final var proxyInfoKey = new AVPKey(DiameterConstants.AVP_PROXY_INFO, 0);
                final List<AVP> proxyInfos = new ArrayList<>();
                for (final AVP avp : avps) {
                    if (avp.isSameKey(proxyInfoKey)) {
                        proxyInfos.add(avp);
                    }
                }

                // mirrored into the answer (RFC 6733 §8.11), like the typed answers do
                final var authSessionStateKey = new AVPKey(DiameterConstants.AVP_AUTH_SESSION_STATE, 0);
                final AVP authSessionState = findFirstAVP(avps, authSessionStateKey);

                // the exception from parseAVPs doesn't know about the context so we must enrich it here.
                throw new AVPParseException(e.getResultCode(), isRequest,
                    commandCode, proxiable, applicationId, hopByHop, endToEnd,
                    e.getOffendingAvp(), sessionId, proxyInfos, authSessionState, e);
            }

            for (final AVP avp : avps) {
                ((Command) command).avps.add(avp);
            }

            return command;

        } catch (final IOException e) {
            throw new DiameterException("Error parsing Diameter message", e);
        }
    }

    private static AVP findFirstAVP(final List<AVP> avps, final AVPKey key) {
        for (final AVP avp : avps) {
            if (avp.isSameKey(key)) {
                return avp;
            }
        }
        return null;
    }

    /**
     * Parses AVPs from the input stream.
     *
     * <p>The first {@link AVPParseException} thrown by {@link AVP#readFrom} propagates
     * immediately, implementing the single-error rule from RFC 6733 §7 without any
     * additional logic.
     *
     * @param byteBuffer      the ByteBuffer to read from
     * @param remainingLength the number of bytes remaining in the message after the header
     * @param avps will be modified within the function, filled with each parsed AVP
     * @param acceptUnknownMandatory keep unrecognized M-bit AVPs instead of failing with 5001
     * @throws IOException       if an I/O error occurs while reading the AVPs
     * @throws AVPParseException if the first AVP in the buffer violates RFC 6733
     */
    private static void parseAVPs(final ByteBuffer byteBuffer, final int remainingLength, final List<AVP> avps,
                                  final boolean acceptUnknownMandatory)
            throws IOException, AVPParseException {
        int bytesRead = 0;

        while (bytesRead < remainingLength) {
            final AVP avp = AVP.readFrom(byteBuffer, acceptUnknownMandatory);
            avps.add(avp);
            final int padding = (4 - (avp.calculateLength() % 4)) % 4;
            bytesRead += avp.calculateLength() + padding;
        }
    }

}
