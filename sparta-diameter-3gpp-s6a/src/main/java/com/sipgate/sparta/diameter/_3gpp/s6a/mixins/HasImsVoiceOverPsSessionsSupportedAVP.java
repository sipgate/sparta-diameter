package com.sipgate.sparta.diameter._3gpp.s6a.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.s6a.S6aConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the IMS-Voice-Over-PS-Sessions-Supported AVP (3GPP TS 29.272 §7.3.106, code 1492). */
public interface HasImsVoiceOverPsSessionsSupportedAVP extends AVPContainer {

    default void setImsVoiceOverPsSessionsSupported(final int value) {
        setAVP(AVP.create(new AVPKey(S6aConstants.AVP_IMS_VOICE_OVER_PS_SESSIONS_SUPPORTED, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default int getImsVoiceOverPsSessionsSupported() {
        final var avp = findAVP(new AVPKey(S6aConstants.AVP_IMS_VOICE_OVER_PS_SESSIONS_SUPPORTED, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsInt() : -1;
    }
}
