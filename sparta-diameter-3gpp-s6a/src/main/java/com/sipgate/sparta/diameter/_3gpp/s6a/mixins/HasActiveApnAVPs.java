package com.sipgate.sparta.diameter._3gpp.s6a.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.s6a.S6aConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** Mixin for messages carrying zero or more Active-APN grouped AVPs (3GPP TS 29.272 §7.3.127, code 1612). */
public interface HasActiveApnAVPs extends AVPContainer {

    default void addActiveApn(final List<AVP> avps) {
        addAVP(AVP.create(new AVPKey(S6aConstants.AVP_ACTIVE_APN, _3gppConstants.VENDOR_ID_3GPP), avps));
    }

    default List<AVPContainer> getActiveApns() {
        final List<AVPContainer> result = new ArrayList<>();
        for (final AVP avp : findAVPs(new AVPKey(S6aConstants.AVP_ACTIVE_APN, _3gppConstants.VENDOR_ID_3GPP))) {
            if (avp instanceof final GroupedAVP grouped) {
                result.add(grouped);
            }
        }
        return result;
    }

    default void addAllActiveApns(final Collection<List<AVP>> values) {
        for (final List<AVP> avps : values) {
            addActiveApn(avps);
        }
    }
}
