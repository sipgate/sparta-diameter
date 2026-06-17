package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the IDR-Flags AVP (3GPP, code 1490). */
public interface HasIdrFlagsAVP extends AVPContainer {

    default void setIdrFlags(final long value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_IDR_FLAGS, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default long getIdrFlags() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_IDR_FLAGS, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsUnsignedInt() : 0L;
    }
}
