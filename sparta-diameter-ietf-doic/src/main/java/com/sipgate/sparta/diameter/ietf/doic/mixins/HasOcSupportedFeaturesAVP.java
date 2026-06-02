package com.sipgate.sparta.diameter.ietf.doic.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;
import com.sipgate.sparta.diameter.ietf.doic.DoicConstants;

import java.util.List;

/** Mixin for messages carrying the OC-Supported-Features grouped AVP (RFC 7683 §7.1, code 621). */
public interface HasOcSupportedFeaturesAVP extends AVPContainer {

    default void setOcSupportedFeatures(final List<AVP> avps) {
        setAVP(AVP.create(new AVPKey(DoicConstants.AVP_OC_SUPPORTED_FEATURES, 0), avps));
    }

    default AVPContainer getOcSupportedFeatures() {
        final var avp = findAVP(new AVPKey(DoicConstants.AVP_OC_SUPPORTED_FEATURES, 0));
        return avp instanceof final GroupedAVP grouped ? grouped : null;
    }
}
