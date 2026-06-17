package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the Reason-Info AVP (3GPP, code 617). */
public interface HasReasonInfoAVP extends AVPContainer {

    default void setReasonInfo(final String value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_REASON_INFO, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default String getReasonInfo() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_REASON_INFO, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsString() : null;
    }
}
