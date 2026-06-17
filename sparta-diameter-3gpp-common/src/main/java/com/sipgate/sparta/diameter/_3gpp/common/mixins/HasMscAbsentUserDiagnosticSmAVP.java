package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the MSC-Absent-User-Diagnostic-SM AVP (3GPP, code 3314). */
public interface HasMscAbsentUserDiagnosticSmAVP extends AVPContainer {

    default void setMscAbsentUserDiagnosticSm(final long value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_MSC_ABSENT_USER_DIAGNOSTIC_SM, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default long getMscAbsentUserDiagnosticSm() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_MSC_ABSENT_USER_DIAGNOSTIC_SM, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsUnsignedInt() : 0L;
    }
}
