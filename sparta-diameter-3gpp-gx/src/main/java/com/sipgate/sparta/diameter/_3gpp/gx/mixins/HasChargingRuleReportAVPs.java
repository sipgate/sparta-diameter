package com.sipgate.sparta.diameter._3gpp.gx.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.gx.GxConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** Mixin for messages carrying zero or more Charging-Rule-Report AVPs (3GPP TS 29.212, code 1018). */
public interface HasChargingRuleReportAVPs extends AVPContainer {

    default void addChargingRuleReport(final List<AVP> avps) {
        addAVP(AVP.create(new AVPKey(GxConstants.AVP_CHARGING_RULE_REPORT, _3gppConstants.VENDOR_ID_3GPP), avps));
    }

    default List<AVPContainer> getChargingRuleReports() {
        final List<AVPContainer> result = new ArrayList<>();
        for (final AVP avp : findAVPs(new AVPKey(GxConstants.AVP_CHARGING_RULE_REPORT, _3gppConstants.VENDOR_ID_3GPP))) {
            if (avp instanceof final GroupedAVP grouped) {
                result.add(grouped);
            }
        }
        return result;
    }

    default void addAllChargingRuleReports(final Collection<List<AVP>> values) {
        for (final List<AVP> avps : values) {
            addChargingRuleReport(avps);
        }
    }
}
