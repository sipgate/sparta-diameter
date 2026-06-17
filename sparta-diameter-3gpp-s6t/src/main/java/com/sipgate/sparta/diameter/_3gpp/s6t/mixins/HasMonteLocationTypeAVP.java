package com.sipgate.sparta.diameter._3gpp.s6t.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.s6t.S6tConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the MONTE-Location-Type AVP (3GPP TS 29.336 §8.4.16, code 3136). */
public interface HasMonteLocationTypeAVP extends AVPContainer {

    default void setMonteLocationType(final long value) {
        setAVP(AVP.create(new AVPKey(S6tConstants.AVP_MONTE_LOCATION_TYPE, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default long getMonteLocationType() {
        final var avp = findAVP(new AVPKey(S6tConstants.AVP_MONTE_LOCATION_TYPE, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsUnsignedInt() : 0L;
    }
}
