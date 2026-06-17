package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the Integrity-Key AVP (3GPP, code 626). */
public interface HasIntegrityKeyAVP extends AVPContainer {

    default void setIntegrityKey(final byte[] value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_INTEGRITY_KEY, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default byte[] getIntegrityKey() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_INTEGRITY_KEY, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsOctetString() : null;
    }
}
