package com.sipgate.sparta.diameter._3gpp.gx.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.gx.GxConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

import java.util.Date;

/** Mixin for messages carrying the Revalidation-Time AVP (3GPP TS 29.212, code 1042). */
public interface HasRevalidationTimeAVP extends AVPContainer {

    default void setRevalidationTime(final Date value) {
        setAVP(AVP.create(new AVPKey(GxConstants.AVP_REVALIDATION_TIME, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default Date getRevalidationTime() {
        final var avp = findAVP(new AVPKey(GxConstants.AVP_REVALIDATION_TIME, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsTime() : null;
    }
}
