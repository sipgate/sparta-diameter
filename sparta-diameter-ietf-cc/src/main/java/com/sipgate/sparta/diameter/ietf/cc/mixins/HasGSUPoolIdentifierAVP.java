package com.sipgate.sparta.diameter.ietf.cc.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.cc.CcConstants;

/** Mixin for messages carrying the G-S-U-Pool-Identifier AVP (RFC 4006 §8.31, code 453). */
public interface HasGSUPoolIdentifierAVP extends AVPContainer {

    default void setGSUPoolIdentifier(final long value) {
        setAVP(AVP.create(new AVPKey(CcConstants.AVP_G_S_U_POOL_IDENTIFIER, 0), value));
    }

    default long getGSUPoolIdentifier() {
        final var avp = findAVP(new AVPKey(CcConstants.AVP_G_S_U_POOL_IDENTIFIER, 0));
        return avp != null ? avp.getDataAsUnsignedInt() : -1L;
    }
}
