package com.sipgate.sparta.diameter._3gpp.s6t.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.s6t.S6tConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the Reachability-Information AVP (3GPP TS 29.336 §8.4.20, code 3140). */
public interface HasReachabilityInformationAVP extends AVPContainer {

    default void setReachabilityInformation(final long value) {
        setAVP(AVP.create(new AVPKey(S6tConstants.AVP_REACHABILITY_INFORMATION, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default long getReachabilityInformation() {
        final var avp = findAVP(new AVPKey(S6tConstants.AVP_REACHABILITY_INFORMATION, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsUnsignedInt() : 0L;
    }
}
