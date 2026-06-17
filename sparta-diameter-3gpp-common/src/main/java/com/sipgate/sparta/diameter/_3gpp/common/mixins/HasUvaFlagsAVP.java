package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the UVA-Flags AVP (3GPP, code 1640). */
public interface HasUvaFlagsAVP extends AVPContainer {

    default void setUvaFlags(final long value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_UVA_FLAGS, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default long getUvaFlags() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_UVA_FLAGS, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsUnsignedInt() : 0L;
    }
}
