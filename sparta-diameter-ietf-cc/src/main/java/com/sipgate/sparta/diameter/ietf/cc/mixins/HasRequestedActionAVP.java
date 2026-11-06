package com.sipgate.sparta.diameter.ietf.cc.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.cc.CcConstants;

/** Mixin for messages carrying the Requested-Action AVP (RFC 4006 §8.41, code 436). */
public interface HasRequestedActionAVP extends AVPContainer {

    default void setRequestedAction(final int value) {
        setAVP(AVP.create(new AVPKey(CcConstants.AVP_REQUESTED_ACTION, 0), value));
    }

    default int getRequestedAction() {
        final var avp = findAVP(new AVPKey(CcConstants.AVP_REQUESTED_ACTION, 0));
        return avp != null ? avp.getDataAsEnumerated() : -1;
    }
}
