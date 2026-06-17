package com.sipgate.sparta.diameter._3gpp.s6t.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.s6t.S6tConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

import java.util.List;

/** Mixin for messages carrying the AESE-Communication-Pattern AVP (3GPP TS 29.336 §8.4.25, code 3113). */
public interface HasAeseCommunicationPatternAVP extends AVPContainer {

    default void setAeseCommunicationPattern(final List<AVP> avps) {
        setAVP(AVP.create(new AVPKey(S6tConstants.AVP_AESE_COMMUNICATION_PATTERN, _3gppConstants.VENDOR_ID_3GPP), avps));
    }

    default AVPContainer getAeseCommunicationPattern() {
        final var avp = findAVP(new AVPKey(S6tConstants.AVP_AESE_COMMUNICATION_PATTERN, _3gppConstants.VENDOR_ID_3GPP));
        return avp instanceof final GroupedAVP grouped ? grouped : null;
    }
}
