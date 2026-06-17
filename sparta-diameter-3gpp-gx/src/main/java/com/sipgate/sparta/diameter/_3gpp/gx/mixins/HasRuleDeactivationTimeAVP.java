package com.sipgate.sparta.diameter._3gpp.gx.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.gx.GxConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

import java.util.Date;

/** Mixin for messages carrying the Rule-Deactivation-Time AVP (3GPP TS 29.212, code 1044). */
public interface HasRuleDeactivationTimeAVP extends AVPContainer {

    default void setRuleDeactivationTime(final Date value) {
        setAVP(AVP.create(new AVPKey(GxConstants.AVP_RULE_DEACTIVATION_TIME, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default Date getRuleDeactivationTime() {
        final var avp = findAVP(new AVPKey(GxConstants.AVP_RULE_DEACTIVATION_TIME, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsTime() : null;
    }
}
