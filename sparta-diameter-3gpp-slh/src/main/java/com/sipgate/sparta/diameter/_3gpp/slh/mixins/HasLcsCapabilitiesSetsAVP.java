package com.sipgate.sparta.diameter._3gpp.slh.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.slh.SlhConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the LCS-Capabilities-Sets AVP (3GPP TS 29.173 §6.4.6, code 2404). */
public interface HasLcsCapabilitiesSetsAVP extends AVPContainer {

    default void setLcsCapabilitiesSets(final long value) {
        setAVP(AVP.create(new AVPKey(SlhConstants.AVP_LCS_CAPABILITIES_SETS, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default long getLcsCapabilitiesSets() {
        final var avp = findAVP(new AVPKey(SlhConstants.AVP_LCS_CAPABILITIES_SETS, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsUnsignedInt() : 0L;
    }
}
