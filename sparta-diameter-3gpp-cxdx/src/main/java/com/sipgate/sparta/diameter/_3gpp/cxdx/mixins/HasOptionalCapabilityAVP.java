package com.sipgate.sparta.diameter._3gpp.cxdx.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.cxdx.CxDxConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying an Optional-Capability AVP (3GPP TS 29.229 §6.3.6, code 605). */
public interface HasOptionalCapabilityAVP extends AVPContainer {

    default void setOptionalCapability(final long value) {
        setAVP(AVP.create(new AVPKey(CxDxConstants.AVP_OPTIONAL_CAPABILITY, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default long getOptionalCapability() {
        final var avp = findAVP(new AVPKey(CxDxConstants.AVP_OPTIONAL_CAPABILITY, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsUnsignedInt() : 0L;
    }
}
