package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

import java.util.List;

/** Mixin for messages carrying the Teleservice-List AVP (3GPP, code 1486). */
public interface HasTeleserviceListAVP extends AVPContainer {

    default void setTeleserviceList(final List<AVP> avps) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_TELESERVICE_LIST, _3gppConstants.VENDOR_ID_3GPP), avps));
    }

    default AVPContainer getTeleserviceList() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_TELESERVICE_LIST, _3gppConstants.VENDOR_ID_3GPP));
        return avp instanceof final GroupedAVP grouped ? grouped : null;
    }
}
