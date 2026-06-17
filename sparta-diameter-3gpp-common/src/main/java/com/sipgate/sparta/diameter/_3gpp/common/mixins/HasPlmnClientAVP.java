package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the PLMN-Client AVP (3GPP, code 1482). */
public interface HasPlmnClientAVP extends AVPContainer {

    default void setPlmnClient(final int value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_PLMN_CLIENT, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default int getPlmnClient() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_PLMN_CLIENT, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsEnumerated() : -1;
    }
}
