package com.sipgate.sparta.diameter.ietf.cc.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.cc.CcConstants;

/** Mixin for messages carrying the Service-Parameter-Type AVP (RFC 4006 §8.44, code 441). */
public interface HasServiceParameterTypeAVP extends AVPContainer {

    default void setServiceParameterType(final long value) {
        setAVP(AVP.create(new AVPKey(CcConstants.AVP_SERVICE_PARAMETER_TYPE, 0), value));
    }

    default long getServiceParameterType() {
        final var avp = findAVP(new AVPKey(CcConstants.AVP_SERVICE_PARAMETER_TYPE, 0));
        return avp != null ? avp.getDataAsUnsignedInt() : -1L;
    }
}
