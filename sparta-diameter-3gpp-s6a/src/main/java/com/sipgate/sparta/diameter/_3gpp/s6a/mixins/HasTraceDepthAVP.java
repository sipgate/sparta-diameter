package com.sipgate.sparta.diameter._3gpp.s6a.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.s6a.S6aConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the Trace-Depth AVP (3GPP TS 29.272, code 1462). */
public interface HasTraceDepthAVP extends AVPContainer {

    default void setTraceDepth(final int value) {
        setAVP(AVP.create(new AVPKey(S6aConstants.AVP_TRACE_DEPTH, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default int getTraceDepth() {
        final var avp = findAVP(new AVPKey(S6aConstants.AVP_TRACE_DEPTH, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsEnumerated() : -1;
    }
}
