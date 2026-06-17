package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the NOR-Flags AVP (3GPP, code 1443). */
public interface HasNorFlagsAVP extends AVPContainer {

    default void setNorFlags(final long value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_NOR_FLAGS, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default long getNorFlags() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_NOR_FLAGS, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsUnsignedInt() : 0L;
    }
}
