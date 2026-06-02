package com.sipgate.sparta.diameter._3gpp.s6a.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.s6a.S6aConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the DSA-Flags AVP (3GPP TS 29.272 §7.3.26, code 1422). */
public interface HasDsaFlagsAVP extends AVPContainer {

    default void setDsaFlags(final long value) {
        setAVP(AVP.create(new AVPKey(S6aConstants.AVP_DSA_FLAGS, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default long getDsaFlags() {
        final var avp = findAVP(new AVPKey(S6aConstants.AVP_DSA_FLAGS, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsUnsignedInt() : 0L;
    }
}
