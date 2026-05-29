package com.sipgate.sparta.diameter._3gpp.cxdx.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.cxdx.CxDxConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying a Wildcarded-Public-Identity AVP (3GPP TS 29.229 §6.3.35, code 634). */
public interface HasWildcardedPublicIdentityAVP extends AVPContainer {

    default void setWildcardedPublicIdentity(final String value) {
        setAVP(AVP.create(new AVPKey(CxDxConstants.AVP_WILDCARDED_PUBLIC_IDENTITY, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default String getWildcardedPublicIdentity() {
        final var avp = findAVP(new AVPKey(CxDxConstants.AVP_WILDCARDED_PUBLIC_IDENTITY, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsString() : null;
    }
}
