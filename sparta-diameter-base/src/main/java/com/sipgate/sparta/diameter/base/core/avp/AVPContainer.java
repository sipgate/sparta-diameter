package com.sipgate.sparta.diameter.base.core.avp;

import com.sipgate.sparta.diameter.base.core.Selfable;

import java.util.List;

/**
 * Base interface for Diameter messages that need AVP operations.
 * <p>
 * This interface provides fundamental methods for managing Attribute-Value Pairs (AVPs)
 * as defined in <a href="https://datatracker.ietf.org/doc/html/rfc6733#section-4.4">RFC 6733, Section 4.4</a>.
 * </p>
 */
public interface AVPContainer<T extends AVPContainer<T>> extends Selfable<T> {

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
     * Finds an AVP by its key (code + vendor ID).
     *
     * @param key The AVP key to search for.
     * @return The matching AVP, or null if not found.
     */
    AVP findAVP(AVPKey key);

    /**
     * Finds all AVPs with the given key (code + vendor ID).
     *
     * @param key The AVP key to search for.
     * @return All matching AVPs in insertion order, never null, possibly empty.
     */
    List<AVP> findAVPs(AVPKey key);
}
