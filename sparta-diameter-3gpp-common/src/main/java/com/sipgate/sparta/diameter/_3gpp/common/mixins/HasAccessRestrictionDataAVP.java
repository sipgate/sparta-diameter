package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the Access-Restriction-Data AVP (3GPP, code 1426). */
public interface HasAccessRestrictionDataAVP extends AVPContainer {

    default void setAccessRestrictionData(final long value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_ACCESS_RESTRICTION_DATA, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default long getAccessRestrictionData() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_ACCESS_RESTRICTION_DATA, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsUnsignedInt() : 0L;
    }
}
