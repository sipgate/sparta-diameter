package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the PDP-Type AVP (3GPP, code 1470). */
public interface HasPdpTypeAVP extends AVPContainer {

    default void setPdpType(final byte[] value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_PDP_TYPE, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default byte[] getPdpType() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_PDP_TYPE, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsOctetString() : null;
    }
}
