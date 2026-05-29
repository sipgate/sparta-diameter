package com.sipgate.sparta.diameter._3gpp.cxdx.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.cxdx.CxDxConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying a Server-Name AVP (3GPP TS 29.229 §6.3.3, code 602). */
public interface HasServerNameAVP extends AVPContainer {

    default void setServerName(final String value) {
        setAVP(AVP.create(new AVPKey(CxDxConstants.AVP_SERVER_NAME, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default String getServerName() {
        final var avp = findAVP(new AVPKey(CxDxConstants.AVP_SERVER_NAME, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsString() : null;
    }
}
