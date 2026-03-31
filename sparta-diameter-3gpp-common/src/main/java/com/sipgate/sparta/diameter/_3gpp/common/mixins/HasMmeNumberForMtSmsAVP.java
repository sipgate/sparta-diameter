package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

/**
 * Mixin for messages carrying an MME-Number-for-MT-SMS AVP (3GPP TS 29.272).
 */
public interface HasMmeNumberForMtSmsAVP<T extends HasMmeNumberForMtSmsAVP<T>> extends AVPContainer<T> {

    default T setMmeNumberForMtSms(final byte[] value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_MME_NUMBER_FOR_MT_SMS, _3gppConstants.VENDOR_ID_3GPP), value));
        return self();
    }

    default byte[] getMmeNumberForMtSms() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_MME_NUMBER_FOR_MT_SMS, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsOctetString() : null;
    }
}
