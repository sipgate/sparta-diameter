package com.sipgate.sparta.diameter._3gpp.gx.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.gx.GxConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the Bearer-Operation AVP (3GPP TS 29.212, code 1021). */
public interface HasBearerOperationAVP extends AVPContainer {

    default void setBearerOperation(final int value) {
        setAVP(AVP.create(new AVPKey(GxConstants.AVP_BEARER_OPERATION, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default int getBearerOperation() {
        final var avp = findAVP(new AVPKey(GxConstants.AVP_BEARER_OPERATION, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsEnumerated() : -1;
    }
}
