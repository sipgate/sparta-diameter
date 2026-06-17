package com.sipgate.sparta.diameter._3gpp.slh.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.slh.SlhConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the LMSI AVP (3GPP TS 29.173 §6.4.2, code 2400). */
public interface HasLmsiAVP extends AVPContainer {

    default void setLmsi(final byte[] value) {
        setAVP(AVP.create(new AVPKey(SlhConstants.AVP_LMSI, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default byte[] getLmsi() {
        final var avp = findAVP(new AVPKey(SlhConstants.AVP_LMSI, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsOctetString() : null;
    }
}
