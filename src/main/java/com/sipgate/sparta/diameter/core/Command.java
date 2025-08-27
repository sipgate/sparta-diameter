package com.sipgate.sparta.diameter.core;

import com.sipgate.sparta.diameter.core.avp.AVP;
import com.sipgate.sparta.diameter.core.avp.AVPContainer;

import java.io.DataOutputStream;
import java.io.IOException;
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
public abstract class Command implements AVPContainer {
    // Diameter header fields
    private final int version;
    private final int commandCode;
    private final boolean request;
    private final boolean proxiable;
    private final boolean error;
    private final boolean retransmitted;
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
     * Sets the Origin-Host AVP.
     * This is a mandatory AVP for most Diameter messages.
     *
     * @param originHost The origin host value.
     */
    public void setOriginHost(final String originHost) {
        setAVP(AVP.create(DiameterConstants.AVP_ORIGIN_HOST, originHost));
    }

    /**
     * Sets the Origin-Realm AVP.
     * This is a mandatory AVP for most Diameter messages.
     *
     * @param originRealm The origin realm value.
     */
    public void setOriginRealm(final String originRealm) {
        setAVP(AVP.create(DiameterConstants.AVP_ORIGIN_REALM, originRealm));
    }

    /**
     * Gets the Origin-Host from this message.
     *
     * @return The Origin-Host value, or null if not present.
     */
    public String getOriginHost() {
        final AVP originHostAVP = findAVP(DiameterConstants.AVP_ORIGIN_HOST);
        if (originHostAVP != null) {
            return originHostAVP.getDataAsString();
        }
        return null;
    }

    /**
     * Gets the Origin-Realm from this message.
     *
     * @return The Origin-Realm value, or null if not present.
     */
    public String getOriginRealm() {
        final AVP originRealmAVP = findAVP(DiameterConstants.AVP_ORIGIN_REALM);
        if (originRealmAVP != null) {
            return originRealmAVP.getDataAsString();
        }
        return null;
    }
}
