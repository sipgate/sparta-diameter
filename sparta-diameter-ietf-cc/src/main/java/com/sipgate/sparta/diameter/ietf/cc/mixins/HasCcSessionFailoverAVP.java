package com.sipgate.sparta.diameter.ietf.cc.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.cc.CcConstants;

/** Mixin for messages carrying the CC-Session-Failover AVP (RFC 4006 §8.4, code 418). */
public interface HasCcSessionFailoverAVP extends AVPContainer {

    default void setCcSessionFailover(final int value) {
        setAVP(AVP.create(new AVPKey(CcConstants.AVP_CC_SESSION_FAILOVER, 0), value));
    }

    default int getCcSessionFailover() {
        final var avp = findAVP(new AVPKey(CcConstants.AVP_CC_SESSION_FAILOVER, 0));
        return avp != null ? avp.getDataAsEnumerated() : -1;
    }
}
