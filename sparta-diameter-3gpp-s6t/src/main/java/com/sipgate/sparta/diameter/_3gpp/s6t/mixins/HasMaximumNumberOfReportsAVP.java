package com.sipgate.sparta.diameter._3gpp.s6t.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.s6t.S6tConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the Maximum-Number-of-Reports AVP (3GPP TS 29.336 §8.4.8, code 3128). */
public interface HasMaximumNumberOfReportsAVP extends AVPContainer {

    default void setMaximumNumberOfReports(final long value) {
        setAVP(AVP.create(new AVPKey(S6tConstants.AVP_MAXIMUM_NUMBER_OF_REPORTS, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default long getMaximumNumberOfReports() {
        final var avp = findAVP(new AVPKey(S6tConstants.AVP_MAXIMUM_NUMBER_OF_REPORTS, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsUnsignedInt() : 0L;
    }
}
