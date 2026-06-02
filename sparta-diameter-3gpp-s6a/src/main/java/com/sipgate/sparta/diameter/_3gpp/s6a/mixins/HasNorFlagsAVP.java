package com.sipgate.sparta.diameter._3gpp.s6a.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.s6a.S6aConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the NOR-Flags AVP (3GPP TS 29.272 §7.3.49, code 1443). */
public interface HasNorFlagsAVP extends AVPContainer {

    default void setNorFlags(final long value) {
        setAVP(AVP.create(new AVPKey(S6aConstants.AVP_NOR_FLAGS, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default long getNorFlags() {
        final var avp = findAVP(new AVPKey(S6aConstants.AVP_NOR_FLAGS, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsUnsignedInt() : 0L;
    }
}
