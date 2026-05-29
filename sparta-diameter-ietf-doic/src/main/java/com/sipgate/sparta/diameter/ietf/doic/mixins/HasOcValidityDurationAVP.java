package com.sipgate.sparta.diameter.ietf.doic.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.doic.DoicConstants;

/** Mixin for containers carrying the OC-Validity-Duration AVP (RFC 7683 §7.5, code 625). */
public interface HasOcValidityDurationAVP extends AVPContainer {

    default void setOcValidityDuration(final long seconds) {
        setAVP(AVP.create(new AVPKey(DoicConstants.AVP_OC_VALIDITY_DURATION, 0), seconds));
    }

    default long getOcValidityDuration() {
        final var avp = findAVP(new AVPKey(DoicConstants.AVP_OC_VALIDITY_DURATION, 0));
        return avp != null ? avp.getDataAsUnsignedInt() : -1L;
    }
}
