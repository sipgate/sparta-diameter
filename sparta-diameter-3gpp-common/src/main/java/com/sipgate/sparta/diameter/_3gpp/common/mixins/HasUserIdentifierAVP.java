package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

/**
 * Mixin for messages carrying a User-Identifier AVP (3GPP TS 29.336).
 */
public interface HasUserIdentifierAVP<T extends HasUserIdentifierAVP<T>> extends AVPContainer<T> {

    default T setUserIdentifier(final GroupedAVP value) {
        setAVP(AVP.create(_3gppConstants.AVP_USER_IDENTIFIER, value.getAVPs()));
        return self();
    }

    default GroupedAVP getUserIdentifier() {
        final var avp = findAVP(_3gppConstants.AVP_USER_IDENTIFIER);
        return avp instanceof GroupedAVP grouped ? grouped : null;
    }
}
