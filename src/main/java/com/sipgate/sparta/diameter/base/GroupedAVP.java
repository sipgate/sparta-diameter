package com.sipgate.sparta.diameter.base;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Grouped AVP that contains other AVPs.
 */
public class GroupedAVP extends AVP {
    private final List<AVP> avps;

    public GroupedAVP(final int code, final boolean mandatory, final List<AVP> avps) {
        super(code, mandatory, serializeAVPs(avps));
        this.avps = new ArrayList<>(avps);
    }

    public GroupedAVP(final int code, final boolean vendorSpecific, final boolean mandatory, final boolean protectedAVP,
                      final int vendorId, final List<AVP> avps) {
        super(code, vendorSpecific, mandatory, protectedAVP, vendorId, serializeAVPs(avps));
        this.avps = new ArrayList<>(avps);
    }

    public List<AVP> getAVPs() {
        return new ArrayList<>(avps);
    }

    public void addAVP(final AVP avp) {
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
            throw new RuntimeException("Failed to serialize AVPs", e);
        }
    }
}
