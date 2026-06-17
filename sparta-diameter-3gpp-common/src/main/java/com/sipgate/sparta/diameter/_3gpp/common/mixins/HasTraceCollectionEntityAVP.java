package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import java.net.InetAddress;

/** Mixin for messages carrying the Trace-Collection-Entity AVP (3GPP, code 1452). */
public interface HasTraceCollectionEntityAVP extends AVPContainer {

    default void setTraceCollectionEntity(final InetAddress value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_TRACE_COLLECTION_ENTITY, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default InetAddress getTraceCollectionEntity() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_TRACE_COLLECTION_ENTITY, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsIPAddress() : null;
    }
}
