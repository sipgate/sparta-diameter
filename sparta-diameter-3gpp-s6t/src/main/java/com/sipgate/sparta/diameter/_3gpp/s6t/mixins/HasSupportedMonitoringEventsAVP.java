package com.sipgate.sparta.diameter._3gpp.s6t.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.s6t.S6tConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

import java.math.BigInteger;

/** Mixin for messages carrying the Supported-Monitoring-Events AVP (3GPP TS 29.336 §8.4.41, code 3144). */
public interface HasSupportedMonitoringEventsAVP extends AVPContainer {

    default void setSupportedMonitoringEvents(final BigInteger value) {
        setAVP(AVP.create(new AVPKey(S6tConstants.AVP_SUPPORTED_MONITORING_EVENTS, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default BigInteger getSupportedMonitoringEvents() {
        final var avp = findAVP(new AVPKey(S6tConstants.AVP_SUPPORTED_MONITORING_EVENTS, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsUnsignedLong() : null;
    }
}
