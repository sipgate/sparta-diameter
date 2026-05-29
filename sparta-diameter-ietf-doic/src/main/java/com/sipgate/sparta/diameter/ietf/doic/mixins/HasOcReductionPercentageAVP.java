package com.sipgate.sparta.diameter.ietf.doic.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.doic.DoicConstants;

/** Mixin for containers carrying the OC-Reduction-Percentage AVP (RFC 7683 §7.7, code 627). */
public interface HasOcReductionPercentageAVP extends AVPContainer {

    default void setOcReductionPercentage(final long percent) {
        setAVP(AVP.create(new AVPKey(DoicConstants.AVP_OC_REDUCTION_PERCENTAGE, 0), percent));
    }

    default long getOcReductionPercentage() {
        final var avp = findAVP(new AVPKey(DoicConstants.AVP_OC_REDUCTION_PERCENTAGE, 0));
        return avp != null ? avp.getDataAsUnsignedInt() : -1L;
    }
}
