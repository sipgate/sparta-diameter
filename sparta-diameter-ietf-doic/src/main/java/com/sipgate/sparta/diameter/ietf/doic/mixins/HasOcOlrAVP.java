package com.sipgate.sparta.diameter.ietf.doic.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;
import com.sipgate.sparta.diameter.ietf.doic.DoicConstants;

import java.util.List;

/** Mixin for messages carrying the OC-OLR grouped AVP (RFC 7683 §7.3, code 623). */
public interface HasOcOlrAVP extends AVPContainer {

    default void setOcOlr(final List<AVP> avps) {
        setAVP(AVP.create(new AVPKey(DoicConstants.AVP_OC_OLR, 0), avps));
    }

    default AVPContainer getOcOlr() {
        final var avp = findAVP(new AVPKey(DoicConstants.AVP_OC_OLR, 0));
        return avp instanceof final GroupedAVP grouped ? grouped : null;
    }
}
