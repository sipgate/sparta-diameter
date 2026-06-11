package com.sipgate.sparta.diameter._3gpp.s6a.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.s6a.S6aConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the IDA-Flags AVP (3GPP TS 29.272 §7.3.47, code 1441). */
public interface HasImeiAVP extends AVPContainer {

    default void setImei(final String value) {
        setAVP(AVP.create(new AVPKey(S6aConstants.AVP_IMEI, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default String getImei() {
        final var avp = findAVP(new AVPKey(S6aConstants.AVP_IMEI, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsString() : null;
    }
}
