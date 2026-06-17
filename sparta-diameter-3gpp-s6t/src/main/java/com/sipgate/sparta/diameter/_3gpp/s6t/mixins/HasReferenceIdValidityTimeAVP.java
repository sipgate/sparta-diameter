package com.sipgate.sparta.diameter._3gpp.s6t.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.s6t.S6tConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

import java.util.Date;

/** Mixin for messages carrying the Reference-ID-Validity-Time AVP (3GPP TS 29.336 §8.4.42, code 3148). */
public interface HasReferenceIdValidityTimeAVP extends AVPContainer {

    default void setReferenceIdValidityTime(final Date value) {
        setAVP(AVP.create(new AVPKey(S6tConstants.AVP_REFERENCE_ID_VALIDITY_TIME, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default Date getReferenceIdValidityTime() {
        final var avp = findAVP(new AVPKey(S6tConstants.AVP_REFERENCE_ID_VALIDITY_TIME, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsTime() : null;
    }
}
