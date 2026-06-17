package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the SIP-Item-Number AVP (3GPP, code 613). */
public interface HasSipItemNumberAVP extends AVPContainer {

    default void setSipItemNumber(final long value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_SIP_ITEM_NUMBER, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default long getSipItemNumber() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_SIP_ITEM_NUMBER, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsUnsignedInt() : 0L;
    }
}
