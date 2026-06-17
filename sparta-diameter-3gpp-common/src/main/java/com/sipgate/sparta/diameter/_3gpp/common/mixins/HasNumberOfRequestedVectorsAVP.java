package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the Number-Of-Requested-Vectors AVP (3GPP, code 1410). */
public interface HasNumberOfRequestedVectorsAVP extends AVPContainer {

    default void setNumberOfRequestedVectors(final long value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_NUMBER_OF_REQUESTED_VECTORS, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default long getNumberOfRequestedVectors() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_NUMBER_OF_REQUESTED_VECTORS, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsUnsignedInt() : 0L;
    }
}
