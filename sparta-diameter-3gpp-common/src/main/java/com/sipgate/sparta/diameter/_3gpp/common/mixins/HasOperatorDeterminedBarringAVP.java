package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the Operator-Determined-Barring AVP (3GPP, code 1425). */
public interface HasOperatorDeterminedBarringAVP extends AVPContainer {

    default void setOperatorDeterminedBarring(final long value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_OPERATOR_DETERMINED_BARRING, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default long getOperatorDeterminedBarring() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_OPERATOR_DETERMINED_BARRING, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsUnsignedInt() : 0L;
    }
}
