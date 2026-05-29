package com.sipgate.sparta.diameter._3gpp.sgdgdd.mixins;

import com.sipgate.sparta.diameter._3gpp.sgdgdd.SgdGddConstants;
import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

import java.util.List;

/**
 * Mixin for messages carrying an SMSMI-Correlation-ID AVP (3GPP TS 29.338 §6.3.3.13, code 3324).
 * <p>
 * Grouped — used in the context of MSISDN-less SMS delivery in IMS. V flag only.
 * </p>
 */
public interface HasSmsmiCorrelationIdAVP extends AVPContainer {

    default void setSmsmiCorrelationId(final List<AVP> avps) {
        setAVP(AVP.create(new AVPKey(SgdGddConstants.AVP_SMSMI_CORRELATION_ID, _3gppConstants.VENDOR_ID_3GPP), avps));
    }

    default AVPContainer getSmsmiCorrelationId() {
        final var avp = findAVP(new AVPKey(SgdGddConstants.AVP_SMSMI_CORRELATION_ID, _3gppConstants.VENDOR_ID_3GPP));
        return avp instanceof final GroupedAVP grouped ? grouped : null;
    }
}
