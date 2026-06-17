package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the Server-Name AVP (3GPP, code 602). */
public interface HasServerNameAVP extends AVPContainer {

    default void setServerName(final String value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_SERVER_NAME, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default String getServerName() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_SERVER_NAME, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsString() : null;
    }
}
