package com.sipgate.sparta.diameter._3gpp.gx.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.gx.GxConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the Guaranteed-Bitrate-DL AVP (3GPP TS 29.212, code 1025). */
public interface HasGuaranteedBitrateDlAVP extends AVPContainer {

    default void setGuaranteedBitrateDl(final long value) {
        setAVP(AVP.create(new AVPKey(GxConstants.AVP_GUARANTEED_BITRATE_DL, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default long getGuaranteedBitrateDl() {
        final var avp = findAVP(new AVPKey(GxConstants.AVP_GUARANTEED_BITRATE_DL, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsUnsignedInt() : 0L;
    }
}
