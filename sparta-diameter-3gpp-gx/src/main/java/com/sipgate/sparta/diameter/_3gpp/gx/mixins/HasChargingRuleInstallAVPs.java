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

/** Mixin for messages carrying zero or more Charging-Rule-Install AVPs (3GPP TS 29.212, code 1001). */
public interface HasChargingRuleInstallAVPs extends AVPContainer {

    default void addChargingRuleInstall(final List<AVP> avps) {
        addAVP(AVP.create(new AVPKey(GxConstants.AVP_CHARGING_RULE_INSTALL, _3gppConstants.VENDOR_ID_3GPP), avps));
    }

    default List<AVPContainer> getChargingRuleInstalls() {
        final List<AVPContainer> result = new ArrayList<>();
        for (final AVP avp : findAVPs(new AVPKey(GxConstants.AVP_CHARGING_RULE_INSTALL, _3gppConstants.VENDOR_ID_3GPP))) {
            if (avp instanceof final GroupedAVP grouped) {
                result.add(grouped);
            }
        }
        return result;
    }

    default void addAllChargingRuleInstalls(final Collection<List<AVP>> values) {
        for (final List<AVP> avps : values) {
            addChargingRuleInstall(avps);
        }
    }
}
