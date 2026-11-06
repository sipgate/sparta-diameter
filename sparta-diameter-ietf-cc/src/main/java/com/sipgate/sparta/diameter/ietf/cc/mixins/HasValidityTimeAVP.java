package com.sipgate.sparta.diameter.ietf.cc.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.cc.CcConstants;

/** Mixin for messages carrying the Validity-Time AVP (RFC 4006 §8.33, code 448). */
public interface HasValidityTimeAVP extends AVPContainer {

    default void setValidityTime(final long value) {
        setAVP(AVP.create(new AVPKey(CcConstants.AVP_VALIDITY_TIME, 0), value));
    }

    default long getValidityTime() {
        final var avp = findAVP(new AVPKey(CcConstants.AVP_VALIDITY_TIME, 0));
        return avp != null ? avp.getDataAsUnsignedInt() : -1L;
    }
}
