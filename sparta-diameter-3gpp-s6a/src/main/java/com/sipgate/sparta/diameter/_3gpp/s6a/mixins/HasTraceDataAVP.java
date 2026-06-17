package com.sipgate.sparta.diameter._3gpp.s6a.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.s6a.S6aConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;
import java.util.List;

/** Mixin for messages carrying the Trace-Data AVP (3GPP TS 29.272, code 1458). */
public interface HasTraceDataAVP extends AVPContainer {

    default void setTraceData(final List<AVP> avps) {
        setAVP(AVP.create(new AVPKey(S6aConstants.AVP_TRACE_DATA, _3gppConstants.VENDOR_ID_3GPP), avps));
    }

    default AVPContainer getTraceData() {
        final var avp = findAVP(new AVPKey(S6aConstants.AVP_TRACE_DATA, _3gppConstants.VENDOR_ID_3GPP));
        return avp instanceof final GroupedAVP grouped ? grouped : null;
    }
}
