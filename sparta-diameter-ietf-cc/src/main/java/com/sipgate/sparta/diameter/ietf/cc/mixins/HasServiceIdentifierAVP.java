package com.sipgate.sparta.diameter.ietf.cc.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.cc.CcConstants;

/** Mixin for messages carrying the Service-Identifier AVP (RFC 4006 §8.28, code 439). */
public interface HasServiceIdentifierAVP extends AVPContainer {

    default void setServiceIdentifier(final long value) {
        setAVP(AVP.create(new AVPKey(CcConstants.AVP_SERVICE_IDENTIFIER, 0), value));
    }

    default long getServiceIdentifier() {
        final var avp = findAVP(new AVPKey(CcConstants.AVP_SERVICE_IDENTIFIER, 0));
        return avp != null ? avp.getDataAsUnsignedInt() : -1L;
    }
}
