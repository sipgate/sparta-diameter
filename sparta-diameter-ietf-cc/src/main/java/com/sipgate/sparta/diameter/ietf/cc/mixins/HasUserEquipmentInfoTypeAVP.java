package com.sipgate.sparta.diameter.ietf.cc.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.cc.CcConstants;

/** Mixin for messages carrying the User-Equipment-Info-Type AVP (RFC 4006 §8.50, code 459). */
public interface HasUserEquipmentInfoTypeAVP extends AVPContainer {

    default void setUserEquipmentInfoType(final int value) {
        setAVP(AVP.create(new AVPKey(CcConstants.AVP_USER_EQUIPMENT_INFO_TYPE, 0), value));
    }

    default int getUserEquipmentInfoType() {
        final var avp = findAVP(new AVPKey(CcConstants.AVP_USER_EQUIPMENT_INFO_TYPE, 0));
        return avp != null ? avp.getDataAsEnumerated() : -1;
    }
}
