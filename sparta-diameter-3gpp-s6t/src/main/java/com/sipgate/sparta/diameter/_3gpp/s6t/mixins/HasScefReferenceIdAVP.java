package com.sipgate.sparta.diameter._3gpp.s6t.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.s6t.S6tConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the SCEF-Reference-ID AVP (3GPP TS 29.336 §8.4.4, code 3124). */
public interface HasScefReferenceIdAVP extends AVPContainer {

    default void setScefReferenceId(final long value) {
        setAVP(AVP.create(new AVPKey(S6tConstants.AVP_SCEF_REFERENCE_ID, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default long getScefReferenceId() {
        final var avp = findAVP(new AVPKey(S6tConstants.AVP_SCEF_REFERENCE_ID, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsUnsignedInt() : 0L;
    }
}
