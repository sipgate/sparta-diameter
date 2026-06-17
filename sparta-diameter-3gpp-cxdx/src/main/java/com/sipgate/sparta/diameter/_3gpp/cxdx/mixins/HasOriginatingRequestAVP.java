package com.sipgate.sparta.diameter._3gpp.cxdx.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.cxdx.CxDxConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying an Originating-Request AVP (3GPP TS 29.229 §6.3.34, code 633). */
public interface HasOriginatingRequestAVP extends AVPContainer {

    default void setOriginatingRequest(final int value) {
        setAVP(AVP.create(new AVPKey(CxDxConstants.AVP_ORIGINATING_REQUEST, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default int getOriginatingRequest() {
        final var avp = findAVP(new AVPKey(CxDxConstants.AVP_ORIGINATING_REQUEST, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsEnumerated() : -1;
    }
}
