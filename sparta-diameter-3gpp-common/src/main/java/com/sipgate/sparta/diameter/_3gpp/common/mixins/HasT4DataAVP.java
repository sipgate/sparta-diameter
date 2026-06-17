package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

import java.util.List;

/** Mixin for messages carrying the T4-Data AVP (3GPP, code 3108). */
public interface HasT4DataAVP extends AVPContainer {

    default void setT4Data(final List<AVP> avps) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_T4_DATA, _3gppConstants.VENDOR_ID_3GPP), avps));
    }

    default AVPContainer getT4Data() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_T4_DATA, _3gppConstants.VENDOR_ID_3GPP));
        return avp instanceof final GroupedAVP grouped ? grouped : null;
    }
}
