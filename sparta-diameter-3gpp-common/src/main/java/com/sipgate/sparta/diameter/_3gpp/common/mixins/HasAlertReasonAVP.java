package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the Alert-Reason AVP (3GPP, code 1434). */
public interface HasAlertReasonAVP extends AVPContainer {

    default void setAlertReason(final int value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_ALERT_REASON, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default int getAlertReason() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_ALERT_REASON, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsEnumerated() : -1;
    }
}
