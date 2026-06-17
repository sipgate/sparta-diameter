package com.sipgate.sparta.diameter._3gpp.s6t.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.s6t.S6tConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the Periodic-time AVP (3GPP TS 29.336 §8.4.29, code 3117). */
public interface HasPeriodicTimeAVP extends AVPContainer {

    default void setPeriodicTime(final long value) {
        setAVP(AVP.create(new AVPKey(S6tConstants.AVP_PERIODIC_TIME, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default long getPeriodicTime() {
        final var avp = findAVP(new AVPKey(S6tConstants.AVP_PERIODIC_TIME, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsUnsignedInt() : 0L;
    }
}
