package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

/**
 * Mixin for messages carrying an SGSN-Number AVP (3GPP TS 29.272).
 */
public interface HasSgsnNumberAVP<T extends HasSgsnNumberAVP<T>> extends AVPContainer<T> {

    default T setSgsnNumber(final byte[] value) {
        setAVP(AVP.create(_3gppConstants.AVP_SGSN_NUMBER, value));
        return self();
    }

    default byte[] getSgsnNumber() {
        final var avp = findAVP(_3gppConstants.AVP_SGSN_NUMBER);
        return avp != null ? avp.getDataAsOctetString() : null;
    }
}
