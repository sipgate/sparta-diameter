package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the SIP-Authentication-Scheme AVP (3GPP, code 608). */
public interface HasSipAuthenticationSchemeAVP extends AVPContainer {

    default void setSipAuthenticationScheme(final String value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_SIP_AUTHENTICATION_SCHEME, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default String getSipAuthenticationScheme() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_SIP_AUTHENTICATION_SCHEME, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsString() : null;
    }
}
