package com.sipgate.sparta.diameter._3gpp.gx.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.gx.GxConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

import java.util.List;

/** Mixin for messages carrying the Default-EPS-Bearer-QoS AVP (3GPP TS 29.212, code 1049). */
public interface HasDefaultEpsBearerQosAVP extends AVPContainer {

    default void setDefaultEpsBearerQos(final List<AVP> avps) {
        setAVP(AVP.create(new AVPKey(GxConstants.AVP_DEFAULT_EPS_BEARER_QOS, _3gppConstants.VENDOR_ID_3GPP), avps));
    }

    default AVPContainer getDefaultEpsBearerQos() {
        final var avp = findAVP(new AVPKey(GxConstants.AVP_DEFAULT_EPS_BEARER_QOS, _3gppConstants.VENDOR_ID_3GPP));
        return avp instanceof final GroupedAVP grouped ? grouped : null;
    }
}
