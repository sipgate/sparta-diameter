package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the Kc AVP (3GPP, code 1453). */
public interface HasKcAVP extends AVPContainer {

    default void setKc(final byte[] value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_KC, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default byte[] getKc() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_KC, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsOctetString() : null;
    }
}
