package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the GMLC-Restriction AVP (3GPP, code 1481). */
public interface HasGmlcRestrictionAVP extends AVPContainer {

    default void setGmlcRestriction(final int value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_GMLC_RESTRICTION, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default int getGmlcRestriction() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_GMLC_RESTRICTION, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsEnumerated() : -1;
    }
}
