package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the ServiceTypeIdentity AVP (3GPP, code 1484). */
public interface HasServicetypeidentityAVP extends AVPContainer {

    default void setServicetypeidentity(final long value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_SERVICETYPEIDENTITY, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default long getServicetypeidentity() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_SERVICETYPEIDENTITY, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsUnsignedInt() : 0L;
    }
}
