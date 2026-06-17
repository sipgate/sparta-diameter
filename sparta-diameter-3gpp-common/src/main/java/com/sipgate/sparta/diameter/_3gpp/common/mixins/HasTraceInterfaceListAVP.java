package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the Trace-Interface-List AVP (3GPP, code 1464). */
public interface HasTraceInterfaceListAVP extends AVPContainer {

    default void setTraceInterfaceList(final byte[] value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_TRACE_INTERFACE_LIST, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default byte[] getTraceInterfaceList() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_TRACE_INTERFACE_LIST, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsOctetString() : null;
    }
}
