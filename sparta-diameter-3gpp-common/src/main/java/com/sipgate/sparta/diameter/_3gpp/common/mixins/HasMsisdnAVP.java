package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

/**
 * Mixin for messages or grouped AVPs carrying the MSISDN AVP (3GPP TS 29.329 §6.3.2).
 *
 * <p>The value is a TBCD-encoded octet string as defined in ITU-T Rec E.164.
 */
public interface HasMsisdnAVP<T extends HasMsisdnAVP<T>> extends AVPContainer<T> {

    default T setMsisdn(final byte[] value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_MSISDN, _3gppConstants.VENDOR_ID_3GPP), value));
        return self();
    }

    default byte[] getMsisdn() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_MSISDN, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsOctetString() : null;
    }
}
