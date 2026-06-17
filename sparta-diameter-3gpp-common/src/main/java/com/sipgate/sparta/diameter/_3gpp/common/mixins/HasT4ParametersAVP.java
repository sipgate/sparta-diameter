package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

import java.util.List;

/** Mixin for messages carrying the T4-Parameters AVP (3GPP, code 3106). */
public interface HasT4ParametersAVP extends AVPContainer {

    default void setT4Parameters(final List<AVP> avps) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_T4_PARAMETERS, _3gppConstants.VENDOR_ID_3GPP), avps));
    }

    default AVPContainer getT4Parameters() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_T4_PARAMETERS, _3gppConstants.VENDOR_ID_3GPP));
        return avp instanceof final GroupedAVP grouped ? grouped : null;
    }
}
