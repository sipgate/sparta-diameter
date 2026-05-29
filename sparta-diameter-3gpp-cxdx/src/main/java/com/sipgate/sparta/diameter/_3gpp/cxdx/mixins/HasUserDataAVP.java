package com.sipgate.sparta.diameter._3gpp.cxdx.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.cxdx.CxDxConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying a User-Data AVP (3GPP TS 29.229 §6.3.7, code 606). */
public interface HasUserDataAVP extends AVPContainer {

    default void setUserData(final byte[] value) {
        setAVP(AVP.create(new AVPKey(CxDxConstants.AVP_USER_DATA, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default byte[] getUserData() {
        final var avp = findAVP(new AVPKey(CxDxConstants.AVP_USER_DATA, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsOctetString() : null;
    }
}
