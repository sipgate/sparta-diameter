package com.sipgate.sparta.diameter.ietf.cc.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.cc.CcConstants;

/** Mixin for messages carrying the User-Equipment-Info-Value AVP (RFC 4006 §8.51, code 460). */
public interface HasUserEquipmentInfoValueAVP extends AVPContainer {

    default void setUserEquipmentInfoValue(final byte[] value) {
        setAVP(AVP.create(new AVPKey(CcConstants.AVP_USER_EQUIPMENT_INFO_VALUE, 0), value));
    }

    default byte[] getUserEquipmentInfoValue() {
        final var avp = findAVP(new AVPKey(CcConstants.AVP_USER_EQUIPMENT_INFO_VALUE, 0));
        return avp != null ? avp.getDataAsOctetString() : null;
    }
}
