package com.sipgate.sparta.diameter._3gpp.gx.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.gx.GxConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the Rule-Failure-Code AVP (3GPP TS 29.212, code 1031). */
public interface HasRuleFailureCodeAVP extends AVPContainer {

    default void setRuleFailureCode(final int value) {
        setAVP(AVP.create(new AVPKey(GxConstants.AVP_RULE_FAILURE_CODE, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default int getRuleFailureCode() {
        final var avp = findAVP(new AVPKey(GxConstants.AVP_RULE_FAILURE_CODE, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsEnumerated() : -1;
    }
}
