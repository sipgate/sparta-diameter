package com.sipgate.sparta.diameter._3gpp.s6t.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.s6t.S6tConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

import java.util.List;

/** Mixin for messages carrying the NIDD-Authorization-Request AVP (3GPP TS 29.336 §8.4.44, code 3150). */
public interface HasNiddAuthorizationRequestAVP extends AVPContainer {

    default void setNiddAuthorizationRequest(final List<AVP> avps) {
        setAVP(AVP.create(new AVPKey(S6tConstants.AVP_NIDD_AUTHORIZATION_REQUEST, _3gppConstants.VENDOR_ID_3GPP), avps));
    }

    default AVPContainer getNiddAuthorizationRequest() {
        final var avp = findAVP(new AVPKey(S6tConstants.AVP_NIDD_AUTHORIZATION_REQUEST, _3gppConstants.VENDOR_ID_3GPP));
        return avp instanceof final GroupedAVP grouped ? grouped : null;
    }
}
