package com.sipgate.sparta.diameter._3gpp.sgdgdd.mixins;

import com.sipgate.sparta.diameter._3gpp.sgdgdd.SgdGddConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

/**
 * Mixin for messages carrying an SMSMI-Correlation-ID AVP (3GPP TS 29.338 §6.3.3.13, code 3324).
 * <p>
 * Grouped — used in the context of MSISDN-less SMS delivery in IMS. V flag only.
 * </p>
 */
public interface HasSmsMiCorrelationIdAVP<T extends HasSmsMiCorrelationIdAVP<T>> extends AVPContainer<T> {

    default T setSmsMiCorrelationId(final GroupedAVP value) {
        setAVP(AVP.create(SgdGddConstants.AVP_SMSMI_CORRELATION_ID, value.getAVPs()));
        return self();
    }

    default GroupedAVP getSmsMiCorrelationId() {
        final var avp = findAVP(SgdGddConstants.AVP_SMSMI_CORRELATION_ID);
        return avp instanceof final GroupedAVP grouped ? grouped : null;
    }
}
