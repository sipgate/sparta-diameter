package com.sipgate.sparta.diameter._3gpp.s6a.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.s6a.S6aConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

import java.util.List;

/** Mixin for messages carrying a Requested-UTRAN-GERAN-Authentication-Info grouped AVP (3GPP TS 29.272 §7.3.12, code 1409). */
public interface HasRequestedUtranGeranAuthenticationInfoAVP extends AVPContainer {

    default void setRequestedUtranGeranAuthenticationInfo(final List<AVP> avps) {
        setAVP(AVP.create(new AVPKey(S6aConstants.AVP_REQUESTED_UTRAN_GERAN_AUTHENTICATION_INFO, _3gppConstants.VENDOR_ID_3GPP), avps));
    }

    default AVPContainer getRequestedUtranGeranAuthenticationInfo() {
        final var avp = findAVP(new AVPKey(S6aConstants.AVP_REQUESTED_UTRAN_GERAN_AUTHENTICATION_INFO, _3gppConstants.VENDOR_ID_3GPP));
        return avp instanceof final GroupedAVP grouped ? grouped : null;
    }
}
