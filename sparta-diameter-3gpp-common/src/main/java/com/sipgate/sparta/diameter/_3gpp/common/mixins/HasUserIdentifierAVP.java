package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

import java.util.List;

/**
 * Mixin for messages carrying a User-Identifier AVP (3GPP TS 29.336 §6.4.2).
 */
public interface HasUserIdentifierAVP extends AVPContainer {

    default void setUserIdentifier(final List<AVP> avps) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_USER_IDENTIFIER, _3gppConstants.VENDOR_ID_3GPP), avps));
    }

    default AVPContainer getUserIdentifier() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_USER_IDENTIFIER, _3gppConstants.VENDOR_ID_3GPP));
        return avp instanceof final GroupedAVP grouped ? grouped : null;
    }
}
