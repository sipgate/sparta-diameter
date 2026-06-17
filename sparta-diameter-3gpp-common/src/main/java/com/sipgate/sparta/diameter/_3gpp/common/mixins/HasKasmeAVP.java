package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the KASME AVP (3GPP, code 1450). */
public interface HasKasmeAVP extends AVPContainer {

    default void setKasme(final byte[] value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_KASME, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default byte[] getKasme() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_KASME, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsOctetString() : null;
    }
}
