package com.sipgate.sparta.diameter.ietf.cc.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;
import com.sipgate.sparta.diameter.ietf.cc.CcAvps;
import com.sipgate.sparta.diameter.ietf.cc.CcConstants;

import java.util.List;

/** Mixin for messages carrying the Unit-Value AVP (RFC 4006 §8.8, code 445). */
public interface HasUnitValueAVP extends AVPContainer {

    default void setUnitValue(final List<AVP> avps) {
        setAVP(AVP.create(new AVPKey(CcConstants.AVP_UNIT_VALUE, 0), avps));
    }

    /**
     * Sets a Unit-Value of {@code valueDigits * 10^exponent}. Prefer this over {@link #setUnitValue(List)}
     * because Value-Digits needs a special 8-byte encoding, see {@link CcAvps#valueDigits(long)}.
     */
    default void setUnitValue(final long valueDigits, final int exponent) {
        setUnitValue(List.of(
            CcAvps.valueDigits(valueDigits),
            AVP.create(new AVPKey(CcConstants.AVP_EXPONENT, 0), exponent)));
    }

    default AVPContainer getUnitValue() {
        final var avp = findAVP(new AVPKey(CcConstants.AVP_UNIT_VALUE, 0));
        return avp instanceof final GroupedAVP grouped ? grouped : null;
    }
}
