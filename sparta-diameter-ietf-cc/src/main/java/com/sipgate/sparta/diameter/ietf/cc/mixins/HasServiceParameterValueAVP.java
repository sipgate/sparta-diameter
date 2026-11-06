package com.sipgate.sparta.diameter.ietf.cc.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.cc.CcConstants;

/** Mixin for messages carrying the Service-Parameter-Value AVP (RFC 4006 §8.45, code 442). */
public interface HasServiceParameterValueAVP extends AVPContainer {

    default void setServiceParameterValue(final byte[] value) {
        setAVP(AVP.create(new AVPKey(CcConstants.AVP_SERVICE_PARAMETER_VALUE, 0), value));
    }

    default byte[] getServiceParameterValue() {
        final var avp = findAVP(new AVPKey(CcConstants.AVP_SERVICE_PARAMETER_VALUE, 0));
        return avp != null ? avp.getDataAsOctetString() : null;
    }
}
