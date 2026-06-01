package com.sipgate.sparta.diameter._3gpp.cxdx.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.cxdx.CxDxConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** Mixin for messages carrying zero or more Identity-with-Emergency-Registration grouped AVPs (TS 29.229 §6.3.51, code 651). */
public interface HasIdentityWithEmergencyRegistrationAVPs extends AVPContainer {

    default void addIdentityWithEmergencyRegistration(final List<AVP> avps) {
        addAVP(AVP.create(new AVPKey(CxDxConstants.AVP_IDENTITY_WITH_EMERGENCY_REGISTRATION, _3gppConstants.VENDOR_ID_3GPP), avps));
    }

    default List<AVPContainer> getIdentityWithEmergencyRegistrations() {
        final List<AVPContainer> result = new ArrayList<>();
        for (final AVP avp : findAVPs(new AVPKey(CxDxConstants.AVP_IDENTITY_WITH_EMERGENCY_REGISTRATION, _3gppConstants.VENDOR_ID_3GPP))) {
            if (avp instanceof final GroupedAVP grouped) {
                result.add(grouped);
            }
        }
        return result;
    }

    default void addAllIdentityWithEmergencyRegistrations(final Collection<List<AVP>> values) {
        for (final List<AVP> avps : values) {
            addIdentityWithEmergencyRegistration(avps);
        }
    }
}
