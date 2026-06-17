package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

import java.util.List;

/** Mixin for messages carrying the GERAN-Vector AVP (3GPP, code 1416). */
public interface HasGeranVectorAVP extends AVPContainer {

    default void setGeranVector(final List<AVP> avps) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_GERAN_VECTOR, _3gppConstants.VENDOR_ID_3GPP), avps));
    }

    default AVPContainer getGeranVector() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_GERAN_VECTOR, _3gppConstants.VENDOR_ID_3GPP));
        return avp instanceof final GroupedAVP grouped ? grouped : null;
    }
}
