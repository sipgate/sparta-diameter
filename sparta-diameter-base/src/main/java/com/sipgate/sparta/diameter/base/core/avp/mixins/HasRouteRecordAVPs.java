package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Mixin for Diameter messages carrying zero or more Route-Record AVPs.
 * <p>
 * RFC 6733 defines {@code * [ Route-Record ]} in proxiable request messages.
 * </p>
 */
public interface HasRouteRecordAVPs<T extends HasRouteRecordAVPs<T>> extends AVPContainer<T> {

    default T addRouteRecord(final String routeRecord) {
        addAVP(AVP.create(new AVPKey(DiameterConstants.AVP_ROUTE_RECORD, 0), routeRecord));
        return self();
    }

    default List<String> getRouteRecords() {
        final List<String> result = new ArrayList<>();
        for (final AVP avp : findAVPs(new AVPKey(DiameterConstants.AVP_ROUTE_RECORD, 0))) {
            result.add(avp.getDataAsString());
        }
        return result;
    }

    default String getFirstRouteRecord() {
        final List<String> all = getRouteRecords();
        return all.isEmpty() ? null : all.get(0);
    }

    default T addAllRouteRecords(final Collection<String> routeRecords) {
        for (final String record : routeRecords) {
            addRouteRecord(record);
        }
        return self();
    }
}
