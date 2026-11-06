package com.sipgate.sparta.diameter.ietf.cc.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;
import com.sipgate.sparta.diameter.ietf.cc.CcConstants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** Mixin for messages carrying zero or more Used-Service-Unit AVPs (RFC 4006 §8.19, code 446). */
public interface HasUsedServiceUnitAVPs extends AVPContainer {

    default void addUsedServiceUnit(final List<AVP> avps) {
        addAVP(AVP.create(new AVPKey(CcConstants.AVP_USED_SERVICE_UNIT, 0), avps));
    }

    default List<AVPContainer> getUsedServiceUnits() {
        final List<AVPContainer> result = new ArrayList<>();
        for (final AVP avp : findAVPs(new AVPKey(CcConstants.AVP_USED_SERVICE_UNIT, 0))) {
            if (avp instanceof final GroupedAVP grouped) {
                result.add(grouped);
            }
        }
        return result;
    }

    default void addAllUsedServiceUnits(final Collection<List<AVP>> values) {
        for (final List<AVP> avps : values) {
            addUsedServiceUnit(avps);
        }
    }
}
