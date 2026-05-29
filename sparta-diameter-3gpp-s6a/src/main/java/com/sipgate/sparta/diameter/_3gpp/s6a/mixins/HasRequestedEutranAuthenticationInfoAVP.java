package com.sipgate.sparta.diameter._3gpp.s6a.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.s6a.S6aConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

import java.util.List;

/**
 * Mixin for messages carrying a RequestedEutranAuthenticationInfo AVP (3GPP TS 29.272 §7.3.11, code 1408). Grouped, modelled flat (no child
 * accessors): the caller supplies/receives the nested AVPs as a list. M,V flags.
 */
public interface HasRequestedEutranAuthenticationInfoAVP extends AVPContainer {

    default void setRequestedEutranAuthenticationInfo(final List<AVP> avps) {
        setAVP(AVP.create(new AVPKey(S6aConstants.AVP_REQUESTED_EUTRAN_AUTHENTICATION_INFO, _3gppConstants.VENDOR_ID_3GPP), avps));
    }

    default AVPContainer getRequestedEutranAuthenticationInfo() {
        final var avp = findAVP(new AVPKey(S6aConstants.AVP_REQUESTED_EUTRAN_AUTHENTICATION_INFO, _3gppConstants.VENDOR_ID_3GPP));
        return avp instanceof final GroupedAVP grouped ? grouped : null;
    }
}
