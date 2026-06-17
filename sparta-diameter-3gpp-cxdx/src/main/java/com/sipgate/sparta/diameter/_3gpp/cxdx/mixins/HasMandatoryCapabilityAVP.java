package com.sipgate.sparta.diameter._3gpp.cxdx.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.cxdx.CxDxConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying a Mandatory-Capability AVP (3GPP TS 29.229 §6.3.5, code 604). */
public interface HasMandatoryCapabilityAVP extends AVPContainer {

    default void setMandatoryCapability(final long value) {
        setAVP(AVP.create(new AVPKey(CxDxConstants.AVP_MANDATORY_CAPABILITY, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default long getMandatoryCapability() {
        final var avp = findAVP(new AVPKey(CxDxConstants.AVP_MANDATORY_CAPABILITY, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsUnsignedInt() : 0L;
    }
}
