package com.sipgate.sparta.diameter._3gpp.s6a.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.s6a.S6aConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/**
 * Mixin for messages carrying a IdaFlags AVP (3GPP TS 29.272 §7.3.47, code 1441).
 * <p>Unsigned32 bitmask — Insert-Subscriber-Data-Answer flags. M,V flags.</p>
 */
public interface HasIdaFlagsAVP extends AVPContainer {

    default void setIdaFlags(final long value) {
        setAVP(AVP.create(new AVPKey(S6aConstants.AVP_IDA_FLAGS, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default long getIdaFlags() {
        final var avp = findAVP(new AVPKey(S6aConstants.AVP_IDA_FLAGS, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsUnsignedInt() : 0L;
    }
}
