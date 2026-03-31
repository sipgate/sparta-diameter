package com.sipgate.sparta.diameter.base.core.avp;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Grouped AVP that contains other AVPs.
 * <p>
 * This class represents a Grouped AVP as defined in
 * <a href="https://datatracker.ietf.org/doc/html/rfc6733#section-4.4">RFC 6733, Section 4.4</a>.
 * Grouped AVPs are used to encapsulate multiple AVPs within a single AVP.
 * </p>
 */
public class GroupedAVP extends AVP {

    private final List<AVP> avps;

    /**
     * Constructs a Grouped AVP with the specified key, mandatory flag, and list of AVPs.
     *
     * @param key       The AVP key (code + vendor ID).
     * @param mandatory Indicates whether the AVP is mandatory.
     * @param avps      The list of AVPs to include in this Grouped AVP.
     */
    public GroupedAVP(final AVPKey key, final boolean mandatory, final List<AVP> avps) {
        super(key.code(), mandatory, serializeAVPs(avps));
        this.avps = new ArrayList<>(avps);
    }

    /**
     * Constructs a Grouped AVP with the specified parameters and list of AVPs.
     *
     * @param code           The AVP code.
     * @param vendorSpecific Indicates whether the AVP is vendor-specific.
     * @param mandatory      Indicates whether the AVP is mandatory.
     * @param protectedAVP   Indicates whether the AVP is protected.
     * @param vendorId       The vendor ID.
     * @param avps           The list of AVPs to include in this Grouped AVP.
     */
    public GroupedAVP(final int code, final boolean vendorSpecific, final boolean mandatory, final boolean protectedAVP,
                      final int vendorId, final List<AVP> avps) {
        super(code, vendorSpecific, mandatory, protectedAVP, vendorId, serializeAVPs(avps));
        this.avps = new ArrayList<>(avps);
    }

    /**
     * Retrieves the list of AVPs contained in this Grouped AVP.
     *
     * @return A copy of the list of AVPs.
     */
    public List<AVP> getAVPs() {
        return new ArrayList<>(avps);
    }

    /**
     * Adds an AVP to this Grouped AVP.
     *
     * @param avp The AVP to add.
     */
    public void addAVP(final AVP avp) {
        avps.add(avp);
    }

    /**
     * Finds the first AVP with the specified key (code + vendor ID).
     *
     * @param key The AVP key to search for.
     * @return The first matching AVP, or null if no match is found.
     */
    public AVP findAVP(final AVPKey key) {
        for (final AVP avp : avps) {
            if (avp.getCode() == key.code() && avp.getVendorId() == key.vendorId()) {
                return avp;
            }
        }
        return null;
    }

    /**
     * Finds all AVPs with the specified key (code + vendor ID).
     *
     * @param key The AVP key to search for.
     * @return A list of matching AVPs, or an empty list if no matches are found.
     */
    public List<AVP> findAVPs(final AVPKey key) {
        final List<AVP> result = new ArrayList<>();
        for (final AVP avp : avps) {
            if (avp.getCode() == key.code() && avp.getVendorId() == key.vendorId()) {
                result.add(avp);
            }
        }
        return result;
    }

    /**
     * Serializes the list of AVPs into a byte array.
     *
     * @param avps The list of AVPs to serialize.
     * @return The serialized byte array.
     */
    private static byte[] serializeAVPs(final List<AVP> avps) {
        if (avps == null || avps.isEmpty()) {
            return new byte[0];
        }

        try {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final DataOutputStream dos = new DataOutputStream(baos);

            for (final AVP avp : avps) {
                avp.writeTo(dos);
            }

            return baos.toByteArray();
        } catch (final IOException e) {
            throw new UncheckedIOException("Failed to serialize AVPs", e);
        }
    }
}
