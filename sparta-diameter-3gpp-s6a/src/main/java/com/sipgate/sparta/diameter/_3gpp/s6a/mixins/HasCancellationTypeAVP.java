package com.sipgate.sparta.diameter._3gpp.s6a.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.s6a.S6aConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/**
 * Mixin for messages carrying a CancellationType AVP (3GPP TS 29.272 §7.3.24, code 1420).
 * <p>Enumerated — the type of cancellation (see CANCELLATION_TYPE_* constants). M,V flags.</p>
 */
public interface HasCancellationTypeAVP extends AVPContainer {

    default void setCancellationType(final int value) {
        setAVP(AVP.create(new AVPKey(S6aConstants.AVP_CANCELLATION_TYPE, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default int getCancellationType() {
        final var avp = findAVP(new AVPKey(S6aConstants.AVP_CANCELLATION_TYPE, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsEnumerated() : -1;
    }
}
