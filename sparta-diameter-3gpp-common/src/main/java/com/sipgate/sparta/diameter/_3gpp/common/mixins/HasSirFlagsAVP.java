package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the SIR-Flags AVP (3GPP, code 3110). */
public interface HasSirFlagsAVP extends AVPContainer {

    default void setSirFlags(final long value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_SIR_FLAGS, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default long getSirFlags() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_SIR_FLAGS, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsUnsignedInt() : 0L;
    }
}
