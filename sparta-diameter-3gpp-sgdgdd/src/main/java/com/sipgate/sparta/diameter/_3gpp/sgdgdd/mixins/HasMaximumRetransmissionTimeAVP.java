package com.sipgate.sparta.diameter._3gpp.sgdgdd.mixins;

import com.sipgate.sparta.diameter._3gpp.sgdgdd.SgdGddConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

import java.util.Date;

/**
 * Mixin for messages carrying a Maximum-Retransmission-Time AVP (3GPP TS 29.338 §6.3.3.17, code 3330).
 * <p>
 * Time — UTC timestamp until which the SMS-GMSC is capable to retransmit. V flag only.
 * </p>
 */
public interface HasMaximumRetransmissionTimeAVP<T extends HasMaximumRetransmissionTimeAVP<T>> extends AVPContainer<T> {

    default T setMaximumRetransmissionTime(final Date value) {
        setAVP(AVP.create(SgdGddConstants.AVP_MAXIMUM_RETRANSMISSION_TIME, value));
        return self();
    }

    default Date getMaximumRetransmissionTime() {
        final var avp = findAVP(SgdGddConstants.AVP_MAXIMUM_RETRANSMISSION_TIME);
        return avp != null ? avp.getDataAsTime() : null;
    }
}
