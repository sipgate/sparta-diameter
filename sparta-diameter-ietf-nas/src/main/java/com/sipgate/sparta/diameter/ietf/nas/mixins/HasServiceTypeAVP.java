package com.sipgate.sparta.diameter.ietf.nas.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.nas.DiameterNasConstants;

/** Mixin for containers carrying the Service-Type AVP (RFC 4005 §6.1, code 6). */
public interface HasServiceTypeAVP extends AVPContainer {

    default void setServiceType(final int value) {
        setAVP(AVP.create(new AVPKey(DiameterNasConstants.AVP_SERVICE_TYPE, 0), value));
    }

    default int getServiceType() {
        final var avp = findAVP(new AVPKey(DiameterNasConstants.AVP_SERVICE_TYPE, 0));
        return avp != null ? avp.getDataAsEnumerated() : -1;
    }
}
