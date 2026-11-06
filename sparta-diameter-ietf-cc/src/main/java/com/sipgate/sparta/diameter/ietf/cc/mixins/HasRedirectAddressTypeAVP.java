package com.sipgate.sparta.diameter.ietf.cc.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.cc.CcConstants;

/** Mixin for messages carrying the Redirect-Address-Type AVP (RFC 4006 §8.38, code 433). */
public interface HasRedirectAddressTypeAVP extends AVPContainer {

    default void setRedirectAddressType(final int value) {
        setAVP(AVP.create(new AVPKey(CcConstants.AVP_REDIRECT_ADDRESS_TYPE, 0), value));
    }

    default int getRedirectAddressType() {
        final var avp = findAVP(new AVPKey(CcConstants.AVP_REDIRECT_ADDRESS_TYPE, 0));
        return avp != null ? avp.getDataAsEnumerated() : -1;
    }
}
