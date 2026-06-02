package com.sipgate.sparta.diameter._3gpp.s6a.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.s6a.S6aConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the AIR-Flags AVP (3GPP TS 29.272 §7.3.201, code 1679). */
public interface HasAirFlagsAVP extends AVPContainer {

    default void setAirFlags(final long value) {
        setAVP(AVP.create(new AVPKey(S6aConstants.AVP_AIR_FLAGS, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default long getAirFlags() {
        final var avp = findAVP(new AVPKey(S6aConstants.AVP_AIR_FLAGS, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsUnsignedInt() : 0L;
    }
}
