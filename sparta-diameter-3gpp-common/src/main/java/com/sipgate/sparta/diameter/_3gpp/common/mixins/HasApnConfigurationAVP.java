package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

import java.util.List;

/** Mixin for messages carrying the APN-Configuration AVP (3GPP, code 1430). */
public interface HasApnConfigurationAVP extends AVPContainer {

    default void setApnConfiguration(final List<AVP> avps) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_APN_CONFIGURATION, _3gppConstants.VENDOR_ID_3GPP), avps));
    }

    default AVPContainer getApnConfiguration() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_APN_CONFIGURATION, _3gppConstants.VENDOR_ID_3GPP));
        return avp instanceof final GroupedAVP grouped ? grouped : null;
    }
}
