package com.sipgate.sparta.diameter.ietf.nas.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.nas.DiameterNasConstants;

/** Mixin for containers carrying the Login-LAT-Group AVP (RFC 4005 §6.17.3, code 36). */
public interface HasLoginLatGroupAVP extends AVPContainer {

    default void setLoginLatGroup(final byte[] value) {
        setAVP(AVP.create(new AVPKey(DiameterNasConstants.AVP_LOGIN_LAT_GROUP, 0), value));
    }

    default byte[] getLoginLatGroup() {
        final var avp = findAVP(new AVPKey(DiameterNasConstants.AVP_LOGIN_LAT_GROUP, 0));
        return avp != null ? avp.getDataAsOctetString() : null;
    }
}
