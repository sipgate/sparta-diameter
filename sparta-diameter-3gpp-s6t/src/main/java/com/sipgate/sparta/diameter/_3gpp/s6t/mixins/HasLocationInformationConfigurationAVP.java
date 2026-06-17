package com.sipgate.sparta.diameter._3gpp.s6t.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.s6t.S6tConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

import java.util.List;

/** Mixin for messages carrying the Location-Information-Configuration AVP (3GPP TS 29.336 §8.4.15, code 3135). */
public interface HasLocationInformationConfigurationAVP extends AVPContainer {

    default void setLocationInformationConfiguration(final List<AVP> avps) {
        setAVP(AVP.create(new AVPKey(S6tConstants.AVP_LOCATION_INFORMATION_CONFIGURATION, _3gppConstants.VENDOR_ID_3GPP), avps));
    }

    default AVPContainer getLocationInformationConfiguration() {
        final var avp = findAVP(new AVPKey(S6tConstants.AVP_LOCATION_INFORMATION_CONFIGURATION, _3gppConstants.VENDOR_ID_3GPP));
        return avp instanceof final GroupedAVP grouped ? grouped : null;
    }
}
