package com.sipgate.sparta.diameter._3gpp.swx.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

import java.util.List;

/** Mixin for messages carrying the Terminal-Information AVP (3GPP, code 1401). */
public interface HasTerminalInformationAVP extends AVPContainer {

    default void setTerminalInformation(final List<AVP> avps) {
        setAVP(AVP.create(new AVPKey(_3gppConstants.AVP_TERMINAL_INFORMATION, _3gppConstants.VENDOR_ID_3GPP), avps));
    }

    default AVPContainer getTerminalInformation() {
        final var avp = findAVP(new AVPKey(_3gppConstants.AVP_TERMINAL_INFORMATION, _3gppConstants.VENDOR_ID_3GPP));
        return avp instanceof final GroupedAVP grouped ? grouped : null;
    }
}
