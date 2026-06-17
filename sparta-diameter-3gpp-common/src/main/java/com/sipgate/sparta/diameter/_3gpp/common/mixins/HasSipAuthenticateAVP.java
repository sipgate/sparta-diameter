package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the SIP-Authenticate AVP (3GPP, code 609). */
public interface HasSipAuthenticateAVP extends AVPContainer {

    default void setSipAuthenticate(final byte[] value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_SIP_AUTHENTICATE, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default byte[] getSipAuthenticate() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_SIP_AUTHENTICATE, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsOctetString() : null;
    }
}
