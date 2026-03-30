package com.sipgate.sparta.diameter._3gpp.common.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

/**
 * Mixin for messages carrying an SMS-GMSC-AlertEvent AVP (3GPP TS 29.338 S6c).
 * <p>
 * Unsigned32 bitmask — V flag only (M-bit must not be set).
 * See 3GPP TS 29.338 §5.3.3.23 for bit definitions.
 * </p>
 */
public interface HasSmsGmscAlertEventAVP<T extends HasSmsGmscAlertEventAVP<T>> extends AVPContainer<T> {

    default T setSmsGmscAlertEvent(final long value) {
        setAVP(AVP.create(_3gppConstants.AVP_SMS_GMSC_ALERT_EVENT, value));
        return self();
    }

    default long getSmsGmscAlertEvent() {
        final var avp = findAVP(_3gppConstants.AVP_SMS_GMSC_ALERT_EVENT);
        return avp != null ? avp.getDataAsUnsignedInt() : 0L;
    }
}
