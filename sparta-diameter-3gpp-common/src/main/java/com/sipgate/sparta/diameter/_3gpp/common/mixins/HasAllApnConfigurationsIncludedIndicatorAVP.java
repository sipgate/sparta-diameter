package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the All-APN-Configurations-Included-Indicator AVP (3GPP, code 1428). */
public interface HasAllApnConfigurationsIncludedIndicatorAVP extends AVPContainer {

    default void setAllApnConfigurationsIncludedIndicator(final int value) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_ALL_APN_CONFIGURATIONS_INCLUDED_INDICATOR, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default int getAllApnConfigurationsIncludedIndicator() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_ALL_APN_CONFIGURATIONS_INCLUDED_INDICATOR, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsEnumerated() : -1;
    }
}
