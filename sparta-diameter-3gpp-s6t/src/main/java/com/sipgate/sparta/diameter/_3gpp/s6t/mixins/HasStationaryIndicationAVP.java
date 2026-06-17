package com.sipgate.sparta.diameter._3gpp.s6t.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.s6t.S6tConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the Stationary-Indication AVP (3GPP TS 29.336 §8.4.31, code 3119). */
public interface HasStationaryIndicationAVP extends AVPContainer {

    default void setStationaryIndication(final long value) {
        setAVP(AVP.create(new AVPKey(S6tConstants.AVP_STATIONARY_INDICATION, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default long getStationaryIndication() {
        final var avp = findAVP(new AVPKey(S6tConstants.AVP_STATIONARY_INDICATION, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsUnsignedInt() : 0L;
    }
}
