package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the MME-Absent-User-Diagnostic-SM AVP (3GPP, code 3313). */
public interface HasMmeAbsentUserDiagnosticSmAVP extends AVPContainer {

    default void setMmeAbsentUserDiagnosticSm(final long value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_MME_ABSENT_USER_DIAGNOSTIC_SM, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default long getMmeAbsentUserDiagnosticSm() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_MME_ABSENT_USER_DIAGNOSTIC_SM, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsUnsignedInt() : 0L;
    }
}
