package com.sipgate.sparta.diameter._3gpp.s6a.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.s6a.S6aConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the Homogeneous-Support-of-IMS-Voice-Over-PS-Sessions AVP (3GPP TS 29.272 §7.3.107, code 1493). */
public interface HasHomogeneousSupportOfImsVoiceOverPsSessionsAVP extends AVPContainer {

    default void setHomogeneousSupportOfImsVoiceOverPsSessions(final int value) {
        setAVP(AVP.create(new AVPKey(S6aConstants.AVP_HOMOGENEOUS_SUPPORT_OF_IMS_VOICE_OVER_PS_SESSIONS, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default int getHomogeneousSupportOfImsVoiceOverPsSessions() {
        final var avp = findAVP(new AVPKey(S6aConstants.AVP_HOMOGENEOUS_SUPPORT_OF_IMS_VOICE_OVER_PS_SESSIONS, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsInt() : -1;
    }
}
