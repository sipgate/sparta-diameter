package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the HSS-Cause AVP (3GPP, code 3109). */
public interface HasHssCauseAVP extends AVPContainer {

    default void setHssCause(final long value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_HSS_CAUSE, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default long getHssCause() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_HSS_CAUSE, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsUnsignedInt() : 0L;
    }
}
