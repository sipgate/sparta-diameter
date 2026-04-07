package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

/**
 * Mixin for messages carrying the External-Identifier AVP (3GPP TS 29.336 §6.4.11).
 */
public interface HasExternalIdentifierAVP<T extends HasExternalIdentifierAVP<T>> extends AVPContainer<T> {

    default T setExternalIdentifier(final String value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_EXTERNAL_IDENTIFIER, _3gppConstants.VENDOR_ID_3GPP), value));
        return self();
    }

    default String getExternalIdentifier() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_EXTERNAL_IDENTIFIER, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsString() : null;
    }
}
