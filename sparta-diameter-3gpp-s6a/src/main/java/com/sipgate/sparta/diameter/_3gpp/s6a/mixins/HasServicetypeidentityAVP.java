package com.sipgate.sparta.diameter._3gpp.s6a.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.s6a.S6aConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the ServiceTypeIdentity AVP (3GPP TS 29.272, code 1484). */
public interface HasServicetypeidentityAVP extends AVPContainer {

    default void setServicetypeidentity(final long value) {
        setAVP(AVP.create(new AVPKey(S6aConstants.AVP_SERVICETYPEIDENTITY, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default long getServicetypeidentity() {
        final var avp = findAVP(new AVPKey(S6aConstants.AVP_SERVICETYPEIDENTITY, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsUnsignedInt() : 0L;
    }
}
