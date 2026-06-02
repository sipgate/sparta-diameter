package com.sipgate.sparta.diameter._3gpp.s6a.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.s6a.S6aConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the SMS-Register-Request AVP (3GPP TS 29.272 §7.3.144, code 1648). */
public interface HasSmsRegisterRequestAVP extends AVPContainer {

    default void setSmsRegisterRequest(final int value) {
        setAVP(AVP.create(new AVPKey(S6aConstants.AVP_SMS_REGISTER_REQUEST, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default int getSmsRegisterRequest() {
        final var avp = findAVP(new AVPKey(S6aConstants.AVP_SMS_REGISTER_REQUEST, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsInt() : -1;
    }
}
