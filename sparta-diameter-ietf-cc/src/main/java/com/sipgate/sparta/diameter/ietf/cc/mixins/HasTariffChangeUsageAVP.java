package com.sipgate.sparta.diameter.ietf.cc.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.cc.CcConstants;

/** Mixin for messages carrying the Tariff-Change-Usage AVP (RFC 4006 §8.27, code 452). */
public interface HasTariffChangeUsageAVP extends AVPContainer {

    default void setTariffChangeUsage(final int value) {
        setAVP(AVP.create(new AVPKey(CcConstants.AVP_TARIFF_CHANGE_USAGE, 0), value));
    }

    default int getTariffChangeUsage() {
        final var avp = findAVP(new AVPKey(CcConstants.AVP_TARIFF_CHANGE_USAGE, 0));
        return avp != null ? avp.getDataAsEnumerated() : -1;
    }
}
