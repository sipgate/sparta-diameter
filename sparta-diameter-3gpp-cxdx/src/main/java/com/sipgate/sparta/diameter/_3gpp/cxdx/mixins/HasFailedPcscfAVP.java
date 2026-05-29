package com.sipgate.sparta.diameter._3gpp.cxdx.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.cxdx.CxDxConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

import java.util.List;

/** Mixin for messages carrying a Failed-PCSCF grouped AVP (TS 29.229 §6.3.64, code 664). */
public interface HasFailedPcscfAVP extends AVPContainer {

    default void setFailedPcscf(final List<AVP> avps) {
        setAVP(AVP.create(new AVPKey(CxDxConstants.AVP_FAILED_PCSCF, _3gppConstants.VENDOR_ID_3GPP), avps));
    }

    default AVPContainer getFailedPcscf() {
        final var avp = findAVP(new AVPKey(CxDxConstants.AVP_FAILED_PCSCF, _3gppConstants.VENDOR_ID_3GPP));
        return avp instanceof final GroupedAVP grouped ? grouped : null;
    }
}
