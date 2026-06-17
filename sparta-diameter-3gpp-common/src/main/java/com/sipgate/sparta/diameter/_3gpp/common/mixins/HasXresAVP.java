package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the XRES AVP (3GPP, code 1448). */
public interface HasXresAVP extends AVPContainer {

    default void setXres(final byte[] value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_XRES, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default byte[] getXres() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_XRES, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsOctetString() : null;
    }
}
