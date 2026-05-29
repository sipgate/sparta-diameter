package com.sipgate.sparta.diameter._3gpp.s6a.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.s6a.S6aConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/**
 * Mixin for messages carrying a UlaFlags AVP (3GPP TS 29.272 §7.3.8, code 1406).
 * <p>Unsigned32 bitmask — Update-Location-Answer flags. M,V flags.</p>
 */
public interface HasUlaFlagsAVP extends AVPContainer {

    default void setUlaFlags(final long value) {
        setAVP(AVP.create(new AVPKey(S6aConstants.AVP_ULA_FLAGS, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default long getUlaFlags() {
        final var avp = findAVP(new AVPKey(S6aConstants.AVP_ULA_FLAGS, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsUnsignedInt() : 0L;
    }
}
