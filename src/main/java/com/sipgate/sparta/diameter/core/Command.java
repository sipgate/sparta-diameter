package com.sipgate.sparta.diameter.core;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class for all Diameter commands (messages).
 * Contains the common Diameter header and AVP handling functionality.
 */
public abstract class Command {
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

    // Getters
    public int getVersion() { return version; }
    public int getCommandCode() { return commandCode; }
    public boolean isRequest() { return request; }
    public boolean isProxiable() { return proxiable; }
    public boolean isError() { return error; }
    public boolean isRetransmitted() { return retransmitted; }
    public int getApplicationId() { return applicationId; }
    public int getHopByHopIdentifier() { return hopByHopIdentifier; }
    public int getEndToEndIdentifier() { return endToEndIdentifier; }
    public List<AVP> getAVPs() { return new ArrayList<>(avps); }

    /**
     * Add an AVP to this command.
     */
    public void addAVP(final AVP avp) {
        avps.add(avp);
    }

    /**
     * Add or update an AVP to this command, ensuring uniqueness by AVP code.
     * If an AVP with the same code already exists, it will be replaced.
     * Otherwise, the AVP will be added.
     */
    public void setAVP(final AVP avp) {
        // Find and remove existing AVP with the same code
        avps.removeIf(existingAvp -> existingAvp.getCode() == avp.getCode());
        // Add the new AVP
        avps.add(avp);
    }

    /**
     * Find an AVP by its code.
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
     */
    public void setOriginHost(final String originHost) {
        setAVP(AVP.createStringAVP(DiameterConstants.ORIGIN_HOST, true, originHost));
    }

    /**
     * Sets the Origin-Realm AVP.
     * This is a mandatory AVP for most Diameter messages.
     */
    public void setOriginRealm(final String originRealm) {
        setAVP(AVP.createStringAVP(DiameterConstants.ORIGIN_REALM, true, originRealm));
    }

    /**
     * Gets the Origin-Host from this message.
     */
    public String getOriginHost() {
        final AVP originHostAVP = findAVP(DiameterConstants.ORIGIN_HOST);
        if (originHostAVP != null) {
            return originHostAVP.getDataAsString();
        }
        return null;
    }

    /**
     * Gets the Origin-Realm from this message.
     */
    public String getOriginRealm() {
        final AVP originRealmAVP = findAVP(DiameterConstants.ORIGIN_REALM);
        if (originRealmAVP != null) {
            return originRealmAVP.getDataAsString();
        }
        return null;
    }
}
