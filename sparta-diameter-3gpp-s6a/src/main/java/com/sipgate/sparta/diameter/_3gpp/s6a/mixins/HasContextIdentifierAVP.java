package com.sipgate.sparta.diameter._3gpp.s6a.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.s6a.S6aConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the Context-Identifier AVP (3GPP TS 29.272 §7.3.27, code 1423). */
public interface HasContextIdentifierAVP extends AVPContainer {

    default void setContextIdentifier(final long value) {
        setAVP(AVP.create(new AVPKey(S6aConstants.AVP_CONTEXT_IDENTIFIER, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default long getContextIdentifier() {
        final var avp = findAVP(new AVPKey(S6aConstants.AVP_CONTEXT_IDENTIFIER, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsUnsignedInt() : 0L;
    }
}
