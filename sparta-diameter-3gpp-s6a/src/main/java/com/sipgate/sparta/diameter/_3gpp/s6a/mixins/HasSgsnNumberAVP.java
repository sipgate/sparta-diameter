package com.sipgate.sparta.diameter._3gpp.s6a.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.s6a.S6aConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the SGSN-Number AVP (3GPP TS 29.272, code 1489). */
public interface HasSgsnNumberAVP extends AVPContainer {

    default void setSgsnNumber(final byte[] value) {
        setAVP(AVP.create(new AVPKey(S6aConstants.AVP_SGSN_NUMBER, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default byte[] getSgsnNumber() {
        final var avp = findAVP(new AVPKey(S6aConstants.AVP_SGSN_NUMBER, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsOctetString() : null;
    }
}
