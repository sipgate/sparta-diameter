package com.sipgate.sparta.diameter._3gpp.s6a.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.s6a.S6aConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the Error-Diagnostic AVP (3GPP TS 29.272 §7.3.128, code 1614). */
public interface HasErrorDiagnosticAVP extends AVPContainer {

    default void setErrorDiagnostic(final int value) {
        setAVP(AVP.create(new AVPKey(S6aConstants.AVP_ERROR_DIAGNOSTIC, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default int getErrorDiagnostic() {
        final var avp = findAVP(new AVPKey(S6aConstants.AVP_ERROR_DIAGNOSTIC, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsInt() : -1;
    }
}
