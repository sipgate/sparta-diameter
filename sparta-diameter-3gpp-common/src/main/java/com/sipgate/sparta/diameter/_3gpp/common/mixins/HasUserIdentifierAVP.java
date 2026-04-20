package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

/**
 * Mixin for messages carrying a User-Identifier AVP (3GPP TS 29.336 §6.4.2).
 */
public interface HasUserIdentifierAVP extends AVPContainer {

    default void setUserIdentifier(final GroupedAVP value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_USER_IDENTIFIER, _3gppConstants.VENDOR_ID_3GPP), value.getAVPs()));
    }

    default GroupedAVP getUserIdentifier() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_USER_IDENTIFIER, _3gppConstants.VENDOR_ID_3GPP));
        return avp instanceof final GroupedAVP grouped ? grouped : null;
    }
}
