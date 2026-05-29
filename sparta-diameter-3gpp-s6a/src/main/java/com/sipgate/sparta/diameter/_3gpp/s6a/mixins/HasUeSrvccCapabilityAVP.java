package com.sipgate.sparta.diameter._3gpp.s6a.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.s6a.S6aConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/**
 * Mixin for messages carrying a UeSrvccCapability AVP (3GPP TS 29.272 §7.3.130, code 1615).
 * <p>Enumerated — the UE SRVCC capability. V flags.</p>
 */
public interface HasUeSrvccCapabilityAVP extends AVPContainer {

    default void setUeSrvccCapability(final int value) {
        setAVP(AVP.create(new AVPKey(S6aConstants.AVP_UE_SRVCC_CAPABILITY, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default int getUeSrvccCapability() {
        final var avp = findAVP(new AVPKey(S6aConstants.AVP_UE_SRVCC_CAPABILITY, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsEnumerated() : -1;
    }
}
