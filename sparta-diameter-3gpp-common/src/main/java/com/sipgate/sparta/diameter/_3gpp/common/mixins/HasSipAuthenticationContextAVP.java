package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the SIP-Authentication-Context AVP (3GPP, code 611). */
public interface HasSipAuthenticationContextAVP extends AVPContainer {

    default void setSipAuthenticationContext(final byte[] value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_SIP_AUTHENTICATION_CONTEXT, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default byte[] getSipAuthenticationContext() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_SIP_AUTHENTICATION_CONTEXT, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsOctetString() : null;
    }
}
