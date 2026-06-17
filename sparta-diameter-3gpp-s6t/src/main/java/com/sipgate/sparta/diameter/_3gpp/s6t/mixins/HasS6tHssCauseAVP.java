package com.sipgate.sparta.diameter._3gpp.s6t.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.s6t.S6tConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the S6t-HSS-Cause AVP (3GPP TS 29.336 §8.4.50, code 3154). */
public interface HasS6tHssCauseAVP extends AVPContainer {

    default void setS6tHssCause(final long value) {
        setAVP(AVP.create(new AVPKey(S6tConstants.AVP_S6T_HSS_CAUSE, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default long getS6tHssCause() {
        final var avp = findAVP(new AVPKey(S6tConstants.AVP_S6T_HSS_CAUSE, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsUnsignedInt() : 0L;
    }
}
