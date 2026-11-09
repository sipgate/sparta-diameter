package com.sipgate.sparta.diameter._3gpp.gy.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.gy.GyConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for containers carrying the 3GPP-Selection-Mode AVP (TS 29.061 §16.4.7.2, code 12). */
public interface Has3gppSelectionModeAVP extends AVPContainer {

    default void set3gppSelectionMode(final String value) {
        setAVP(AVP.create(new AVPKey(GyConstants.AVP_3GPP_SELECTION_MODE, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default String get3gppSelectionMode() {
        final var avp = findAVP(new AVPKey(GyConstants.AVP_3GPP_SELECTION_MODE, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsString() : null;
    }
}
