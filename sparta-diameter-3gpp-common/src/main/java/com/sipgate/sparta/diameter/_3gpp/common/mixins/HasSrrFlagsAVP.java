package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the SRR-Flags AVP (3GPP, code 3310). */
public interface HasSrrFlagsAVP extends AVPContainer {

    default void setSrrFlags(final long value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_SRR_FLAGS, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default long getSrrFlags() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_SRR_FLAGS, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsUnsignedInt() : 0L;
    }
}
