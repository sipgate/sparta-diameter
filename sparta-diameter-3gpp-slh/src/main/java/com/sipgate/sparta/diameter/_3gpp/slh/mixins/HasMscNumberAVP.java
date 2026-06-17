package com.sipgate.sparta.diameter._3gpp.slh.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.slh.SlhConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the MSC-Number AVP (3GPP TS 29.173 §6.4.5, code 2403). */
public interface HasMscNumberAVP extends AVPContainer {

    default void setMscNumber(final byte[] value) {
        setAVP(AVP.create(new AVPKey(SlhConstants.AVP_MSC_NUMBER, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default byte[] getMscNumber() {
        final var avp = findAVP(new AVPKey(SlhConstants.AVP_MSC_NUMBER, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsOctetString() : null;
    }
}
