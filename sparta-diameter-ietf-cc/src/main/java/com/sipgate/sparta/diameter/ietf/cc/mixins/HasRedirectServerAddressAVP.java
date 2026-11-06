package com.sipgate.sparta.diameter.ietf.cc.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.cc.CcConstants;

/** Mixin for messages carrying the Redirect-Server-Address AVP (RFC 4006 §8.39, code 435). */
public interface HasRedirectServerAddressAVP extends AVPContainer {

    default void setRedirectServerAddress(final String value) {
        setAVP(AVP.create(new AVPKey(CcConstants.AVP_REDIRECT_SERVER_ADDRESS, 0), value));
    }

    default String getRedirectServerAddress() {
        final var avp = findAVP(new AVPKey(CcConstants.AVP_REDIRECT_SERVER_ADDRESS, 0));
        return avp != null ? avp.getDataAsString() : null;
    }
}
