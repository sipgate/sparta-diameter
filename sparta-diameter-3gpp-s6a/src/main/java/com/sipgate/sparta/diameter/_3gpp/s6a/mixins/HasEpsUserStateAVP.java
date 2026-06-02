package com.sipgate.sparta.diameter._3gpp.s6a.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.s6a.S6aConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

import java.util.List;

/** Mixin for messages carrying a EPS-User-State grouped AVP (3GPP TS 29.272 §7.3.110, code 1495). */
public interface HasEpsUserStateAVP extends AVPContainer {

    default void setEpsUserState(final List<AVP> avps) {
        setAVP(AVP.create(new AVPKey(S6aConstants.AVP_EPS_USER_STATE, _3gppConstants.VENDOR_ID_3GPP), avps));
    }

    default AVPContainer getEpsUserState() {
        final var avp = findAVP(new AVPKey(S6aConstants.AVP_EPS_USER_STATE, _3gppConstants.VENDOR_ID_3GPP));
        return avp instanceof final GroupedAVP grouped ? grouped : null;
    }
}
