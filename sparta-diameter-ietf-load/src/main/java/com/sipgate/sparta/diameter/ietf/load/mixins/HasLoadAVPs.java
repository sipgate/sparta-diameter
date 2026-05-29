package com.sipgate.sparta.diameter.ietf.load.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;
import com.sipgate.sparta.diameter.ietf.load.LoadConstants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** Mixin for messages carrying zero or more Load grouped AVPs (RFC 8583 §7.1, code 650). */
public interface HasLoadAVPs extends AVPContainer {

    default void addLoad(final List<AVP> avps) {
        addAVP(AVP.create(new AVPKey(LoadConstants.AVP_LOAD, 0), avps));
    }

    default List<AVPContainer> getLoads() {
        final List<AVPContainer> result = new ArrayList<>();
        for (final AVP avp : findAVPs(new AVPKey(LoadConstants.AVP_LOAD, 0))) {
            if (avp instanceof final GroupedAVP grouped) {
                result.add(grouped);
            }
        }
        return result;
    }

    default void addAllLoads(final Collection<List<AVP>> values) {
        for (final List<AVP> avps : values) {
            addLoad(avps);
        }
    }
}
