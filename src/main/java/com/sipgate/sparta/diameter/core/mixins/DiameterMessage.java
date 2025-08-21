package com.sipgate.sparta.diameter.core.mixins;

import com.sipgate.sparta.diameter.core.AVP;

/**
 * Base interface for Diameter messages that need AVP operations.
 * Provides the fundamental AVP manipulation methods that other mixins can extend.
 */
public interface DiameterMessage {

    /**
     * Add an AVP to this message.
     */
    void addAVP(AVP avp);

    /**
     * Add or update an AVP to this message, ensuring uniqueness by AVP code.
     * If an AVP with the same code already exists, it will be replaced.
     */
    void setAVP(AVP avp);

    /**
     * Find an AVP by its code.
     */
    AVP findAVP(int code);
}
