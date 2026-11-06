package com.sipgate.sparta.diameter.ietf.cc.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;
import com.sipgate.sparta.diameter.ietf.cc.CcConstants;

import java.util.List;

/** Mixin for messages carrying the Requested-Service-Unit AVP (RFC 4006 §8.18, code 437). */
public interface HasRequestedServiceUnitAVP extends AVPContainer {

    default void setRequestedServiceUnit(final List<AVP> avps) {
        setAVP(AVP.create(new AVPKey(CcConstants.AVP_REQUESTED_SERVICE_UNIT, 0), avps));
    }

    default AVPContainer getRequestedServiceUnit() {
        final var avp = findAVP(new AVPKey(CcConstants.AVP_REQUESTED_SERVICE_UNIT, 0));
        return avp instanceof final GroupedAVP grouped ? grouped : null;
    }
}
