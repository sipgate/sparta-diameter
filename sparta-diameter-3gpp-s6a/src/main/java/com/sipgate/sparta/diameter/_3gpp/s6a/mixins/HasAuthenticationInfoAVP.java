package com.sipgate.sparta.diameter._3gpp.s6a.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.s6a.S6aConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

import java.util.List;

/**
 * Mixin for messages carrying a AuthenticationInfo AVP (3GPP TS 29.272 §7.3.17, code 1413). Grouped, modelled flat (no child
 * accessors): the caller supplies/receives the nested AVPs as a list. M,V flags.
 */
public interface HasAuthenticationInfoAVP extends AVPContainer {

    default void setAuthenticationInfo(final List<AVP> avps) {
        setAVP(AVP.create(new AVPKey(S6aConstants.AVP_AUTHENTICATION_INFO, _3gppConstants.VENDOR_ID_3GPP), avps));
    }

    default AVPContainer getAuthenticationInfo() {
        final var avp = findAVP(new AVPKey(S6aConstants.AVP_AUTHENTICATION_INFO, _3gppConstants.VENDOR_ID_3GPP));
        return avp instanceof final GroupedAVP grouped ? grouped : null;
    }
}
