package com.sipgate.sparta.diameter._3gpp.gx.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.gx.GxConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the TFT-Filter AVP (3GPP TS 29.212, code 1012). */
public interface HasTftFilterAVP extends AVPContainer {

    default void setTftFilter(final String value) {
        setAVP(AVP.create(new AVPKey(GxConstants.AVP_TFT_FILTER, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default String getTftFilter() {
        final var avp = findAVP(new AVPKey(GxConstants.AVP_TFT_FILTER, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsString() : null;
    }
}
