package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the ULA-Flags AVP (3GPP, code 1406). */
public interface HasUlaFlagsAVP extends AVPContainer {

    default void setUlaFlags(final long value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_ULA_FLAGS, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default long getUlaFlags() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_ULA_FLAGS, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsUnsignedInt() : 0L;
    }
}
