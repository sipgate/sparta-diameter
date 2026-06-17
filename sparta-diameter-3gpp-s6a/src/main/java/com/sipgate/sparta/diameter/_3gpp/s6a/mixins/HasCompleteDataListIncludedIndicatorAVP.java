package com.sipgate.sparta.diameter._3gpp.s6a.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.s6a.S6aConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the Complete-Data-List-Included-Indicator AVP (3GPP TS 29.272, code 1468). */
public interface HasCompleteDataListIncludedIndicatorAVP extends AVPContainer {

    default void setCompleteDataListIncludedIndicator(final int value) {
        setAVP(AVP.create(new AVPKey(S6aConstants.AVP_COMPLETE_DATA_LIST_INCLUDED_INDICATOR, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default int getCompleteDataListIncludedIndicator() {
        final var avp = findAVP(new AVPKey(S6aConstants.AVP_COMPLETE_DATA_LIST_INCLUDED_INDICATOR, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsEnumerated() : -1;
    }
}
