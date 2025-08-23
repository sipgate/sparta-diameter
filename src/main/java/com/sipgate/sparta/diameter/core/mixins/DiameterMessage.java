package com.sipgate.sparta.diameter.core.mixins;

import com.sipgate.sparta.diameter.core.avp.AVP;

/**
 * Base interface for Diameter messages that need AVP operations.
 * <p>
 * This interface provides fundamental methods for managing Attribute-Value Pairs (AVPs)
 * as defined in <a href="https://datatracker.ietf.org/doc/html/rfc6733#section-4.4">RFC 6733, Section 4.4</a>.
 * </p>
 */
public interface DiameterMessage {

    /**
     * Adds an AVP to this message.
     *
     * @param avp The AVP to add.
     */
    void addAVP(AVP avp);

    /**
     * Adds or updates an AVP in this message, ensuring uniqueness by AVP code.
     * If an AVP with the same code already exists, it will be replaced.
     *
     * @param avp The AVP to add or update.
     */
    void setAVP(AVP avp);

    /**
     * Finds an AVP by its code.
     *
     * @param code The AVP code to search for.
     * @return The matching AVP, or null if not found.
     */
    AVP findAVP(int code);
}
