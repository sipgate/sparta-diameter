package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the User-Data-Already-Available AVP (3GPP, code 624). */
public interface HasUserDataAlreadyAvailableAVP extends AVPContainer {

    default void setUserDataAlreadyAvailable(final int value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_USER_DATA_ALREADY_AVAILABLE, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default int getUserDataAlreadyAvailable() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_USER_DATA_ALREADY_AVAILABLE, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsEnumerated() : -1;
    }
}
