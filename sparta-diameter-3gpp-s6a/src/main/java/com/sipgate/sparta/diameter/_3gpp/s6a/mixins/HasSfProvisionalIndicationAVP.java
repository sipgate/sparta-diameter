package com.sipgate.sparta.diameter._3gpp.s6a.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.s6a.S6aConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the SF-Provisional-Indication AVP (3GPP TS 29.272 §7.3.231, code 1730). */
public interface HasSfProvisionalIndicationAVP extends AVPContainer {

    default void setSfProvisionalIndication(final int value) {
        setAVP(AVP.create(new AVPKey(S6aConstants.AVP_SF_PROVISIONAL_INDICATION, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default int getSfProvisionalIndication() {
        final var avp = findAVP(new AVPKey(S6aConstants.AVP_SF_PROVISIONAL_INDICATION, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsInt() : -1;
    }
}
