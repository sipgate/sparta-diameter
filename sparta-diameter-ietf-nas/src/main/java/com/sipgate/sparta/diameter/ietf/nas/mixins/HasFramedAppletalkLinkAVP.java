package com.sipgate.sparta.diameter.ietf.nas.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.nas.DiameterNasConstants;

/** Mixin for containers carrying the Framed-Appletalk-Link AVP (RFC 4005 §6.13.1, code 37). */
public interface HasFramedAppletalkLinkAVP extends AVPContainer {

    default void setFramedAppletalkLink(final long value) {
        setAVP(AVP.create(new AVPKey(DiameterNasConstants.AVP_FRAMED_APPLETALK_LINK, 0), value));
    }

    default long getFramedAppletalkLink() {
        final var avp = findAVP(new AVPKey(DiameterNasConstants.AVP_FRAMED_APPLETALK_LINK, 0));
        return avp != null ? avp.getDataAsUnsignedInt() : 0L;
    }
}
