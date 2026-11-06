package com.sipgate.sparta.diameter._3gpp.gy.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.gy.GyConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

import java.util.List;

/** Mixin for containers carrying the Service-Information AVP (TS 32.299, code 873). */
public interface HasServiceInformationAVP extends AVPContainer {

    default void setServiceInformation(final List<AVP> avps) {
        setAVP(AVP.create(new AVPKey(GyConstants.AVP_SERVICE_INFORMATION, _3gppConstants.VENDOR_ID_3GPP), avps));
    }

    default AVPContainer getServiceInformation() {
        final var avp = findAVP(new AVPKey(GyConstants.AVP_SERVICE_INFORMATION, _3gppConstants.VENDOR_ID_3GPP));
        return avp instanceof final GroupedAVP grouped ? grouped : null;
    }
}
