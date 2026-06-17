package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the DSA-Flags AVP (3GPP, code 1422). */
public interface HasDsaFlagsAVP extends AVPContainer {

    default void setDsaFlags(final long value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_DSA_FLAGS, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default long getDsaFlags() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_DSA_FLAGS, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsUnsignedInt() : 0L;
    }
}
