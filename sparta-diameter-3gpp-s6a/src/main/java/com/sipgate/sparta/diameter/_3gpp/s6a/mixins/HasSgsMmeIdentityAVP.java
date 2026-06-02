package com.sipgate.sparta.diameter._3gpp.s6a.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.s6a.S6aConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the SGs-MME-Identity AVP (3GPP TS 29.272 §7.3.160, code 1664). */
public interface HasSgsMmeIdentityAVP extends AVPContainer {

    default void setSgsMmeIdentity(final String value) {
        setAVP(AVP.create(new AVPKey(S6aConstants.AVP_SGS_MME_IDENTITY, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default String getSgsMmeIdentity() {
        final var avp = findAVP(new AVPKey(S6aConstants.AVP_SGS_MME_IDENTITY, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsString() : null;
    }
}
