package com.sipgate.sparta.diameter._3gpp.s6t.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.s6t.S6tConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the SCEF-ID AVP (3GPP TS 29.336 §8.4.5, code 3125). */
public interface HasScefIdAVP extends AVPContainer {

    default void setScefId(final String value) {
        setAVP(AVP.create(new AVPKey(S6tConstants.AVP_SCEF_ID, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default String getScefId() {
        final var avp = findAVP(new AVPKey(S6tConstants.AVP_SCEF_ID, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsDiameterIdentity() : null;
    }
}
