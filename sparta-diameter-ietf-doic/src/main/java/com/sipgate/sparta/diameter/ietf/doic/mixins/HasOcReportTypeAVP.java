package com.sipgate.sparta.diameter.ietf.doic.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.doic.DoicConstants;

/** Mixin for containers carrying the OC-Report-Type AVP (RFC 7683 §7.6, code 626). */
public interface HasOcReportTypeAVP extends AVPContainer {

    default void setOcReportType(final int value) {
        setAVP(AVP.create(new AVPKey(DoicConstants.AVP_OC_REPORT_TYPE, 0), value));
    }

    default int getOcReportType() {
        final var avp = findAVP(new AVPKey(DoicConstants.AVP_OC_REPORT_TYPE, 0));
        return avp != null ? avp.getDataAsInt() : -1;
    }
}
