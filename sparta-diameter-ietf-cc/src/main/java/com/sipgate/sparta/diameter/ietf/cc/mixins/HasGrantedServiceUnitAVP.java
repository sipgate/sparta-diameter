package com.sipgate.sparta.diameter.ietf.cc.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;
import com.sipgate.sparta.diameter.ietf.cc.CcConstants;

import java.util.List;

/** Mixin for messages carrying the Granted-Service-Unit AVP (RFC 4006 §8.17, code 431). */
public interface HasGrantedServiceUnitAVP extends AVPContainer {

    default void setGrantedServiceUnit(final List<AVP> avps) {
        setAVP(AVP.create(new AVPKey(CcConstants.AVP_GRANTED_SERVICE_UNIT, 0), avps));
    }

    default AVPContainer getGrantedServiceUnit() {
        final var avp = findAVP(new AVPKey(CcConstants.AVP_GRANTED_SERVICE_UNIT, 0));
        return avp instanceof final GroupedAVP grouped ? grouped : null;
    }
}
