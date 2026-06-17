package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the VPLMN-Dynamic-Address-Allowed AVP (3GPP, code 1432). */
public interface HasVplmnDynamicAddressAllowedAVP extends AVPContainer {

    default void setVplmnDynamicAddressAllowed(final int value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_VPLMN_DYNAMIC_ADDRESS_ALLOWED, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default int getVplmnDynamicAddressAllowed() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_VPLMN_DYNAMIC_ADDRESS_ALLOWED, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsEnumerated() : -1;
    }
}
