package com.sipgate.sparta.diameter._3gpp.gx.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.gx.GxConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** Mixin for messages carrying zero or more Event-Trigger AVPs (3GPP TS 29.212, code 1006). */
public interface HasEventTriggerAVPs extends AVPContainer {

    default void addEventTrigger(final int value) {
        addAVP(AVP.create(new AVPKey(GxConstants.AVP_EVENT_TRIGGER, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default List<Integer> getEventTriggers() {
        final List<Integer> result = new ArrayList<>();
        for (final AVP avp : findAVPs(new AVPKey(GxConstants.AVP_EVENT_TRIGGER, _3gppConstants.VENDOR_ID_3GPP))) {
            result.add(avp.getDataAsEnumerated());
        }
        return result;
    }

    default void addAllEventTriggers(final Collection<Integer> values) {
        for (final int value : values) {
            addEventTrigger(value);
        }
    }
}
