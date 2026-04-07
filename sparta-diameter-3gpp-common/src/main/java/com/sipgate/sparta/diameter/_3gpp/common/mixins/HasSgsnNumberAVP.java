package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

/**
 * Mixin for messages carrying an SGSN-Number AVP (3GPP TS 29.272 §7.3.102).
 */
public interface HasSgsnNumberAVP<T extends HasSgsnNumberAVP<T>> extends AVPContainer<T> {

    default T setSgsnNumber(final byte[] value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_SGSN_NUMBER, _3gppConstants.VENDOR_ID_3GPP), value));
        return self();
    }

    default byte[] getSgsnNumber() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_SGSN_NUMBER, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsOctetString() : null;
    }
}
