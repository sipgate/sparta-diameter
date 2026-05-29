package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/**
 * Mixin for messages carrying a RAT-Type AVP (3GPP TS 29.212 §5.3.31, code 1032).
 * <p>
 * Enumerated — identifies the radio access technology serving the UE. Used message-direct in the
 * S6a/S6d Update-Location-Request (TS 29.272 Table 7.3.1/2 marks the M-bit "Must set"). M,V flags.
 * </p>
 */
public interface HasRatTypeAVP extends AVPContainer {

    default void setRatType(final int value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_RAT_TYPE, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default int getRatType() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_RAT_TYPE, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsEnumerated() : -1;
    }
}
