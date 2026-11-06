package com.sipgate.sparta.diameter.ietf.cc.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.cc.CcConstants;

/** Mixin for messages carrying the Multiple-Services-Indicator AVP (RFC 4006 §8.40, code 455). */
public interface HasMultipleServicesIndicatorAVP extends AVPContainer {

    default void setMultipleServicesIndicator(final int value) {
        setAVP(AVP.create(new AVPKey(CcConstants.AVP_MULTIPLE_SERVICES_INDICATOR, 0), value));
    }

    default int getMultipleServicesIndicator() {
        final var avp = findAVP(new AVPKey(CcConstants.AVP_MULTIPLE_SERVICES_INDICATOR, 0));
        return avp != null ? avp.getDataAsEnumerated() : -1;
    }
}
