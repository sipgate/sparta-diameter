package com.sipgate.sparta.diameter._3gpp.cxdx.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.cxdx.CxDxConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying a User-Authorization-Type AVP (3GPP TS 29.229 §6.3.24, code 623). */
public interface HasUserAuthorizationTypeAVP extends AVPContainer {

    default void setUserAuthorizationType(final int value) {
        setAVP(AVP.create(new AVPKey(CxDxConstants.AVP_USER_AUTHORIZATION_TYPE, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default int getUserAuthorizationType() {
        final var avp = findAVP(new AVPKey(CxDxConstants.AVP_USER_AUTHORIZATION_TYPE, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsEnumerated() : -1;
    }
}
