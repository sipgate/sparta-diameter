package com.sipgate.sparta.diameter._3gpp.gy.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.gy.GyConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for containers carrying the 3GPP-NSAPI AVP (TS 29.061 §16.4.7.2, code 10). */
public interface Has3gppNsapiAVP extends AVPContainer {

    default void set3gppNsapi(final byte[] value) {
        setAVP(AVP.create(new AVPKey(GyConstants.AVP_3GPP_NSAPI, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default byte[] get3gppNsapi() {
        final var avp = findAVP(new AVPKey(GyConstants.AVP_3GPP_NSAPI, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsOctetString() : null;
    }
}
