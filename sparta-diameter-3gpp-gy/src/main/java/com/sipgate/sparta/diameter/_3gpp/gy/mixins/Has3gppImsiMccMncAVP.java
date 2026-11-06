package com.sipgate.sparta.diameter._3gpp.gy.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.gy.GyConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for containers carrying the 3GPP-IMSI-MCC-MNC AVP (TS 29.061 §16.4.7.2, code 8). */
public interface Has3gppImsiMccMncAVP extends AVPContainer {

    default void set3gppImsiMccMnc(final String value) {
        setAVP(AVP.create(new AVPKey(GyConstants.AVP_3GPP_IMSI_MCC_MNC, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default String get3gppImsiMccMnc() {
        final var avp = findAVP(new AVPKey(GyConstants.AVP_3GPP_IMSI_MCC_MNC, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsString() : null;
    }
}
