package com.sipgate.sparta.diameter._3gpp.cxdx.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.cxdx.CxDxConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying a Public-Identity AVP (3GPP TS 29.229 §6.3.2, code 601). */
public interface HasPublicIdentityAVP extends AVPContainer {

    default void setPublicIdentity(final String value) {
        setAVP(AVP.create(new AVPKey(CxDxConstants.AVP_PUBLIC_IDENTITY, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default String getPublicIdentity() {
        final var avp = findAVP(new AVPKey(CxDxConstants.AVP_PUBLIC_IDENTITY, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsString() : null;
    }
}
