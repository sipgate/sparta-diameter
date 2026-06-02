package com.sipgate.sparta.diameter._3gpp.s6t.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.s6t.S6tConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** Mixin for messages carrying zero or more Monitoring-Event-Report grouped AVPs (3GPP TS 29.336 §8.4.3, code 3123). */
public interface HasMonitoringEventReportAVPs extends AVPContainer {

    default void addMonitoringEventReport(final List<AVP> avps) {
        addAVP(AVP.create(new AVPKey(S6tConstants.AVP_MONITORING_EVENT_REPORT, _3gppConstants.VENDOR_ID_3GPP), avps));
    }

    default List<AVPContainer> getMonitoringEventReports() {
        final List<AVPContainer> result = new ArrayList<>();
        for (final AVP avp : findAVPs(new AVPKey(S6tConstants.AVP_MONITORING_EVENT_REPORT, _3gppConstants.VENDOR_ID_3GPP))) {
            if (avp instanceof final GroupedAVP grouped) {
                result.add(grouped);
            }
        }
        return result;
    }

    default void addAllMonitoringEventReports(final Collection<List<AVP>> values) {
        for (final List<AVP> avps : values) {
            addMonitoringEventReport(avps);
        }
    }
}
