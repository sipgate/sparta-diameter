package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

import java.util.List;

/**
 * Mixin for messages carrying an EPS-Location-Information AVP (3GPP TS 29.272 §7.3.111).
 */
public interface HasEpsLocationInformationAVP extends AVPContainer {

    default void setEpsLocationInformation(final List<AVP> avps) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_EPS_LOCATION_INFORMATION, _3gppConstants.VENDOR_ID_3GPP), avps));
    }

    default AVPContainer getEpsLocationInformation() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_EPS_LOCATION_INFORMATION, _3gppConstants.VENDOR_ID_3GPP));
        return avp instanceof final GroupedAVP grouped ? grouped : null;
    }
}
