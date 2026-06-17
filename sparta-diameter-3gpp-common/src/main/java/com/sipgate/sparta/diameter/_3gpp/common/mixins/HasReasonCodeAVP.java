package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the Reason-Code AVP (3GPP, code 616). */
public interface HasReasonCodeAVP extends AVPContainer {

    default void setReasonCode(final int value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_REASON_CODE, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default int getReasonCode() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_REASON_CODE, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsEnumerated() : -1;
    }
}
