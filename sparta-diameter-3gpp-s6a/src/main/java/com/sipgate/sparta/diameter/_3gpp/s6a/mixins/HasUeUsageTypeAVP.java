package com.sipgate.sparta.diameter._3gpp.s6a.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.s6a.S6aConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the UE-Usage-Type AVP (3GPP TS 29.272 §7.3.202, code 1680). */
public interface HasUeUsageTypeAVP extends AVPContainer {

    default void setUeUsageType(final long value) {
        setAVP(AVP.create(new AVPKey(S6aConstants.AVP_UE_USAGE_TYPE, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default long getUeUsageType() {
        final var avp = findAVP(new AVPKey(S6aConstants.AVP_UE_USAGE_TYPE, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsUnsignedInt() : 0L;
    }
}
