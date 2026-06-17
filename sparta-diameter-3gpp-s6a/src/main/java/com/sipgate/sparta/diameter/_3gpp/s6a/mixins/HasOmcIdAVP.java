package com.sipgate.sparta.diameter._3gpp.s6a.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.s6a.S6aConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the OMC-Id AVP (3GPP TS 29.272, code 1466). */
public interface HasOmcIdAVP extends AVPContainer {

    default void setOmcId(final byte[] value) {
        setAVP(AVP.create(new AVPKey(S6aConstants.AVP_OMC_ID, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default byte[] getOmcId() {
        final var avp = findAVP(new AVPKey(S6aConstants.AVP_OMC_ID, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsOctetString() : null;
    }
}
