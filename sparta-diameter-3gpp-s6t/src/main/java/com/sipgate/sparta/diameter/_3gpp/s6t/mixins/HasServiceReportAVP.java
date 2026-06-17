package com.sipgate.sparta.diameter._3gpp.s6t.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.s6t.S6tConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

import java.util.List;

/** Mixin for messages carrying the Service-Report AVP (3GPP TS 29.336 §8.4.47, code 3152). */
public interface HasServiceReportAVP extends AVPContainer {

    default void setServiceReport(final List<AVP> avps) {
        setAVP(AVP.create(new AVPKey(S6tConstants.AVP_SERVICE_REPORT, _3gppConstants.VENDOR_ID_3GPP), avps));
    }

    default AVPContainer getServiceReport() {
        final var avp = findAVP(new AVPKey(S6tConstants.AVP_SERVICE_REPORT, _3gppConstants.VENDOR_ID_3GPP));
        return avp instanceof final GroupedAVP grouped ? grouped : null;
    }
}
