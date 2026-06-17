package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the IMEI AVP (3GPP, code 1402). */
public interface HasImeiAVP extends AVPContainer {

    default void setImei(final String value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_IMEI, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default String getImei() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_IMEI, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsString() : null;
    }
}
