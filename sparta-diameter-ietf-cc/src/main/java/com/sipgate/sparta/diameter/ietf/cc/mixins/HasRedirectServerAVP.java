package com.sipgate.sparta.diameter.ietf.cc.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;
import com.sipgate.sparta.diameter.ietf.cc.CcConstants;

import java.util.List;

/** Mixin for messages carrying the Redirect-Server AVP (RFC 4006 §8.37, code 434). */
public interface HasRedirectServerAVP extends AVPContainer {

    default void setRedirectServer(final List<AVP> avps) {
        setAVP(AVP.create(new AVPKey(CcConstants.AVP_REDIRECT_SERVER, 0), avps));
    }

    default AVPContainer getRedirectServer() {
        final var avp = findAVP(new AVPKey(CcConstants.AVP_REDIRECT_SERVER, 0));
        return avp instanceof final GroupedAVP grouped ? grouped : null;
    }
}
