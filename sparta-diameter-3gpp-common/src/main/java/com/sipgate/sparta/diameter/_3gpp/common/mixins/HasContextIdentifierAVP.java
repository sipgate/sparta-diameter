package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the Context-Identifier AVP (3GPP, code 1423). */
public interface HasContextIdentifierAVP extends AVPContainer {

    default void setContextIdentifier(final long value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_CONTEXT_IDENTIFIER, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default long getContextIdentifier() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_CONTEXT_IDENTIFIER, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsUnsignedInt() : 0L;
    }
}
