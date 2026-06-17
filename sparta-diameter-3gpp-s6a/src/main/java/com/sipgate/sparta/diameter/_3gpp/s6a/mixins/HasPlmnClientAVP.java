package com.sipgate.sparta.diameter._3gpp.s6a.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.s6a.S6aConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the PLMN-Client AVP (3GPP TS 29.272, code 1482). */
public interface HasPlmnClientAVP extends AVPContainer {

    default void setPlmnClient(final int value) {
        setAVP(AVP.create(new AVPKey(S6aConstants.AVP_PLMN_CLIENT, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default int getPlmnClient() {
        final var avp = findAVP(new AVPKey(S6aConstants.AVP_PLMN_CLIENT, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsEnumerated() : -1;
    }
}
