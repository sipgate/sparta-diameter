package com.sipgate.sparta.diameter._3gpp.cxdx.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.cxdx.CxDxConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying a User-Data-Already-Available AVP (3GPP TS 29.229 §6.3.26, code 624). */
public interface HasUserDataAlreadyAvailableAVP extends AVPContainer {

    default void setUserDataAlreadyAvailable(final int value) {
        setAVP(AVP.create(new AVPKey(CxDxConstants.AVP_USER_DATA_ALREADY_AVAILABLE, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default int getUserDataAlreadyAvailable() {
        final var avp = findAVP(new AVPKey(CxDxConstants.AVP_USER_DATA_ALREADY_AVAILABLE, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsInt() : -1;
    }
}
