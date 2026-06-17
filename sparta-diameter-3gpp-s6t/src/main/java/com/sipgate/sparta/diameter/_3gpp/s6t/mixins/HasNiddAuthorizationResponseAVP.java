package com.sipgate.sparta.diameter._3gpp.s6t.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.s6t.S6tConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

import java.util.List;

/** Mixin for messages carrying the NIDD-Authorization-Response AVP (3GPP TS 29.336 §8.4.45, code 3151). */
public interface HasNiddAuthorizationResponseAVP extends AVPContainer {

    default void setNiddAuthorizationResponse(final List<AVP> avps) {
        setAVP(AVP.create(new AVPKey(S6tConstants.AVP_NIDD_AUTHORIZATION_RESPONSE, _3gppConstants.VENDOR_ID_3GPP), avps));
    }

    default AVPContainer getNiddAuthorizationResponse() {
        final var avp = findAVP(new AVPKey(S6tConstants.AVP_NIDD_AUTHORIZATION_RESPONSE, _3gppConstants.VENDOR_ID_3GPP));
        return avp instanceof final GroupedAVP grouped ? grouped : null;
    }
}
