package com.sipgate.sparta.diameter._3gpp.sgdgdd.mixins;

import com.sipgate.sparta.diameter._3gpp.sgdgdd.SgdGddConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

import java.util.Date;

/**
 * Mixin for messages carrying a Requested-Retransmission-Time AVP (3GPP TS 29.338 §6.3.3.18, code 3331).
 * <p>
 * Time — UTC timestamp at which the SMS-GMSC is requested to retransmit. V flag only.
 * </p>
 */
public interface HasRequestedRetransmissionTimeAVP<T extends HasRequestedRetransmissionTimeAVP<T>> extends AVPContainer<T> {

    default T setRequestedRetransmissionTime(final Date value) {
        setAVP(AVP.create(SgdGddConstants.AVP_REQUESTED_RETRANSMISSION_TIME, value));
        return self();
    }

    default Date getRequestedRetransmissionTime() {
        final var avp = findAVP(SgdGddConstants.AVP_REQUESTED_RETRANSMISSION_TIME);
        return avp != null ? avp.getDataAsTime() : null;
    }
}
