package com.sipgate.sparta.diameter._3gpp.swx.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

import java.util.List;

/** Mixin for messages carrying the SIP-Auth-Data-Item AVP (3GPP, code 612). */
public interface HasSipAuthDataItemAVP extends AVPContainer {

    default void setSipAuthDataItem(final List<AVP> avps) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_SIP_AUTH_DATA_ITEM, _3gppConstants.VENDOR_ID_3GPP), avps));
    }

    default AVPContainer getSipAuthDataItem() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_SIP_AUTH_DATA_ITEM, _3gppConstants.VENDOR_ID_3GPP));
        return avp instanceof final GroupedAVP grouped ? grouped : null;
    }
}
