package com.sipgate.sparta.diameter._3gpp.sgdgdd.mixins;

import com.sipgate.sparta.diameter._3gpp.sgdgdd.SgdGddConstants;
import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

/**
 * Mixin for messages carrying an OFR-Flags AVP (3GPP TS 29.338 §6.3.3.12, code 3328).
 * <p>
 * Unsigned32 bitmask — bit 0: S6a/S6d-Indicator (set=Gdd/SGSN, clear=SGd/MME). V flag only.
 * </p>
 */
public interface HasOfrFlagsAVP<T extends HasOfrFlagsAVP<T>> extends AVPContainer<T> {

    default T setOfrFlags(final long value) {
        setAVP(AVP.create(new AVPKey(SgdGddConstants.AVP_OFR_FLAGS, _3gppConstants.VENDOR_ID_3GPP), value));
        return self();
    }

    default long getOfrFlags() {
        final var avp = findAVP(new AVPKey(SgdGddConstants.AVP_OFR_FLAGS, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsUnsignedInt() : 0L;
    }
}
