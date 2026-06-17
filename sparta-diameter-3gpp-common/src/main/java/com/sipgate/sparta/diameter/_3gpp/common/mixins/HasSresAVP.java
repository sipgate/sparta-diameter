package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the SRES AVP (3GPP, code 1454). */
public interface HasSresAVP extends AVPContainer {

    default void setSres(final byte[] value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_SRES, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default byte[] getSres() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_SRES, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsOctetString() : null;
    }
}
