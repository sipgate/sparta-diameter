package com.sipgate.sparta.diameter._3gpp.s6a.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.s6a.S6aConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

import java.util.List;

/**
 * Mixin for messages carrying a TerminalInformation AVP (3GPP TS 29.272 §7.3.3, code 1401). Grouped, modelled flat (no child
 * accessors): the caller supplies/receives the nested AVPs as a list. M,V flags.
 */
public interface HasTerminalInformationAVP extends AVPContainer {

    default void setTerminalInformation(final List<AVP> avps) {
        setAVP(AVP.create(new AVPKey(S6aConstants.AVP_TERMINAL_INFORMATION, _3gppConstants.VENDOR_ID_3GPP), avps));
    }

    default AVPContainer getTerminalInformation() {
        final var avp = findAVP(new AVPKey(S6aConstants.AVP_TERMINAL_INFORMATION, _3gppConstants.VENDOR_ID_3GPP));
        return avp instanceof final GroupedAVP grouped ? grouped : null;
    }
}
