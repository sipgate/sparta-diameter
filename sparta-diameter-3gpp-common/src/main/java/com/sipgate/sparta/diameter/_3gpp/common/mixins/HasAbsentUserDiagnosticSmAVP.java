package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

/**
 * Mixin for messages carrying an AbsentUser-Diagnostic-SM AVP (3GPP TS 29.338 §5.3.3.20).
 * <p>
 * Unsigned32 — M,V flags.
 * </p>
 */
public interface HasAbsentUserDiagnosticSmAVP extends AVPContainer {

    default void setAbsentUserDiagnosticSm(final long value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_ABSENT_USER_DIAGNOSTIC_SM, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default long getAbsentUserDiagnosticSm() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_ABSENT_USER_DIAGNOSTIC_SM, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsUnsignedInt() : 0L;
    }
}
