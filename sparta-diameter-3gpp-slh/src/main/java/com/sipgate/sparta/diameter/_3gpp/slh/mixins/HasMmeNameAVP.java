package com.sipgate.sparta.diameter._3gpp.slh.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.slh.SlhConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the MME-Name AVP (3GPP TS 29.173 §6.4.4, code 2402). */
public interface HasMmeNameAVP extends AVPContainer {

    default void setMmeName(final String value) {
        setAVP(AVP.create(new AVPKey(SlhConstants.AVP_MME_NAME, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default String getMmeName() {
        final var avp = findAVP(new AVPKey(SlhConstants.AVP_MME_NAME, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsString() : null;
    }
}
