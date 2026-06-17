package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the SCS-Identity AVP (3GPP, code 3104). */
public interface HasScsIdentityAVP extends AVPContainer {

    default void setScsIdentity(final byte[] value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_SCS_IDENTITY, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default byte[] getScsIdentity() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_SCS_IDENTITY, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsOctetString() : null;
    }
}
