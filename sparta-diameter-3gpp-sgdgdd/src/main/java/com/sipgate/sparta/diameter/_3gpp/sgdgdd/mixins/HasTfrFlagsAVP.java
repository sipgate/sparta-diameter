package com.sipgate.sparta.diameter._3gpp.sgdgdd.mixins;

import com.sipgate.sparta.diameter._3gpp.sgdgdd.SgdGddConstants;
import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

/**
 * Mixin for messages carrying a TFR-Flags AVP (3GPP TS 29.338 §6.3.3.4, code 3302).
 * <p>
 * Unsigned32 bitmask — bit 0: More-Messages-To-Send. M,V flags.
 * </p>
 */
public interface HasTfrFlagsAVP extends AVPContainer {

    default void setTfrFlags(final long value) {
        setAVP(AVP.create(new AVPKey(SgdGddConstants.AVP_TFR_FLAGS, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default long getTfrFlags() {
        final var avp = findAVP(new AVPKey(SgdGddConstants.AVP_TFR_FLAGS, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsUnsignedInt() : 0L;
    }
}
