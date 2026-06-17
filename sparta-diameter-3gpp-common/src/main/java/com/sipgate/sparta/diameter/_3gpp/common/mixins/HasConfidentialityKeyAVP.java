package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the Confidentiality-Key AVP (3GPP, code 625). */
public interface HasConfidentialityKeyAVP extends AVPContainer {

    default void setConfidentialityKey(final byte[] value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_CONFIDENTIALITY_KEY, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default byte[] getConfidentialityKey() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_CONFIDENTIALITY_KEY, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsOctetString() : null;
    }
}
