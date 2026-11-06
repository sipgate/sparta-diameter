package com.sipgate.sparta.diameter.ietf.cc.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.cc.CcConstants;

import java.util.Date;

/** Mixin for messages carrying the Tariff-Time-Change AVP (RFC 4006 §8.20, code 451). */
public interface HasTariffTimeChangeAVP extends AVPContainer {

    default void setTariffTimeChange(final Date value) {
        setAVP(AVP.create(new AVPKey(CcConstants.AVP_TARIFF_TIME_CHANGE, 0), value));
    }

    default Date getTariffTimeChange() {
        final var avp = findAVP(new AVPKey(CcConstants.AVP_TARIFF_TIME_CHANGE, 0));
        return avp != null ? avp.getDataAsTime() : null;
    }
}
