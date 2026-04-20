package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

/**
 * Mixin for messages carrying an MME-Number-for-MT-SMS AVP (3GPP TS 29.272 §7.3.159).
 */
public interface HasMmeNumberForMtSmsAVP extends AVPContainer {

    default void setMmeNumberForMtSms(final byte[] value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_MME_NUMBER_FOR_MT_SMS, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default byte[] getMmeNumberForMtSms() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_MME_NUMBER_FOR_MT_SMS, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsOctetString() : null;
    }
}
