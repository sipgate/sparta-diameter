package com.sipgate.sparta.diameter._3gpp.sgdgdd.mixins;

import com.sipgate.sparta.diameter._3gpp.sgdgdd.SgdGddConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

/**
 * Mixin for messages carrying an SMS-GMSC-Address AVP (3GPP TS 29.338 §6.3.3.19, code 3332).
 * <p>
 * OctetString — E.164 number of the SMS-GMSC or SMS Router encoded as TBCD-string. V flag only.
 * </p>
 */
public interface HasSmsGmscAddressAVP<T extends HasSmsGmscAddressAVP<T>> extends AVPContainer<T> {

    default T setSmsGmscAddress(final byte[] value) {
        setAVP(AVP.create(SgdGddConstants.AVP_SMS_GMSC_ADDRESS, value));
        return self();
    }

    default byte[] getSmsGmscAddress() {
        final var avp = findAVP(SgdGddConstants.AVP_SMS_GMSC_ADDRESS);
        return avp != null ? avp.getDataAsOctetString() : null;
    }
}
