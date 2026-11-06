package com.sipgate.sparta.diameter.ietf.cc.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;
import com.sipgate.sparta.diameter.ietf.cc.CcConstants;

import java.util.List;

/** Mixin for messages carrying the User-Equipment-Info AVP (RFC 4006 §8.49, code 458). */
public interface HasUserEquipmentInfoAVP extends AVPContainer {

    default void setUserEquipmentInfo(final List<AVP> avps) {
        setAVP(AVP.create(new AVPKey(CcConstants.AVP_USER_EQUIPMENT_INFO, 0), avps));
    }

    default AVPContainer getUserEquipmentInfo() {
        final var avp = findAVP(new AVPKey(CcConstants.AVP_USER_EQUIPMENT_INFO, 0));
        return avp instanceof final GroupedAVP grouped ? grouped : null;
    }
}
