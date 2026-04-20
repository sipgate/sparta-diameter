package com.sipgate.sparta.diameter._3gpp.sgdgdd.mixins;

import com.sipgate.sparta.diameter._3gpp.sgdgdd.SgdGddConstants;
import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

/**
 * Mixin for messages carrying an SM-Diagnostic-Info AVP (3GPP TS 29.338 §6.3.3.7, code 3305).
 * <p>
 * OctetString — complementary information associated with SM Delivery Failure. M,V flags.
 * </p>
 */
public interface HasSmDiagnosticInfoAVP extends AVPContainer {

    default void setSmDiagnosticInfo(final byte[] value) {
        setAVP(AVP.create(new AVPKey(SgdGddConstants.AVP_SM_DIAGNOSTIC_INFO, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default byte[] getSmDiagnosticInfo() {
        final var avp = findAVP(new AVPKey(SgdGddConstants.AVP_SM_DIAGNOSTIC_INFO, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsOctetString() : null;
    }
}
