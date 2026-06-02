package com.sipgate.sparta.diameter.ietf.load.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.load.LoadConstants;

/** Mixin for containers carrying the Load-Type AVP (RFC 8583 §7.2, code 651). */
public interface HasLoadTypeAVP extends AVPContainer {

    default void setLoadType(final int value) {
        setAVP(AVP.create(new AVPKey(LoadConstants.AVP_LOAD_TYPE, 0), value));
    }

    default int getLoadType() {
        final var avp = findAVP(new AVPKey(LoadConstants.AVP_LOAD_TYPE, 0));
        return avp != null ? avp.getDataAsInt() : -1;
    }
}
