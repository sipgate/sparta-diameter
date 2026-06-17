package com.sipgate.sparta.diameter._3gpp.s6t.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.s6t.S6tConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

import java.util.List;

/** Mixin for messages carrying the Supported-Services AVP (3GPP TS 29.336 §8.4.40, code 3143). */
public interface HasSupportedServicesAVP extends AVPContainer {

    default void setSupportedServices(final List<AVP> avps) {
        setAVP(AVP.create(new AVPKey(S6tConstants.AVP_SUPPORTED_SERVICES, _3gppConstants.VENDOR_ID_3GPP), avps));
    }

    default AVPContainer getSupportedServices() {
        final var avp = findAVP(new AVPKey(S6tConstants.AVP_SUPPORTED_SERVICES, _3gppConstants.VENDOR_ID_3GPP));
        return avp instanceof final GroupedAVP grouped ? grouped : null;
    }
}
