package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the 3GPP2-MEID AVP (3GPP, code 1471). */
public interface Has3gpp2MeidAVP extends AVPContainer {

    default void set3gpp2Meid(final byte[] value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_3GPP2_MEID, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default byte[] get3gpp2Meid() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_3GPP2_MEID, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsOctetString() : null;
    }
}
