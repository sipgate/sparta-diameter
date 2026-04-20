package com.sipgate.sparta.diameter._3gpp.sgdgdd.mixins;

import com.sipgate.sparta.diameter._3gpp.sgdgdd.SgdGddConstants;
import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

import java.util.Date;

/**
 * Mixin for messages carrying a Requested-Retransmission-Time AVP (3GPP TS 29.338 §6.3.3.18, code 3331).
 * <p>
 * Time — UTC timestamp at which the SMS-GMSC is requested to retransmit. V flag only.
 * </p>
 */
public interface HasRequestedRetransmissionTimeAVP extends AVPContainer {

    default void setRequestedRetransmissionTime(final Date value) {
        setAVP(AVP.create(new AVPKey(SgdGddConstants.AVP_REQUESTED_RETRANSMISSION_TIME, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default Date getRequestedRetransmissionTime() {
        final var avp = findAVP(new AVPKey(SgdGddConstants.AVP_REQUESTED_RETRANSMISSION_TIME, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsTime() : null;
    }
}
