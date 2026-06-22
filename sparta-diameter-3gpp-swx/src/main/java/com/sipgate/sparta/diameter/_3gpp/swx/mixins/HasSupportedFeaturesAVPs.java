package com.sipgate.sparta.diameter._3gpp.swx.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Mixin for messages carrying zero or more Supported-Features AVPs (3GPP TS 29.229 §6.3.29).
 */
public interface HasSupportedFeaturesAVPs extends AVPContainer {

    default void addSupportedFeatures(final List<AVP> avps) {
        addAVP(AVP.create(new AVPKey(_3gppConstants.AVP_SUPPORTED_FEATURES, _3gppConstants.VENDOR_ID_3GPP), avps));
    }

    default List<AVPContainer> getSupportedFeatures() {
        final List<AVPContainer> result = new ArrayList<>();
        for (final AVP avp : findAVPs(new AVPKey(_3gppConstants.AVP_SUPPORTED_FEATURES, _3gppConstants.VENDOR_ID_3GPP))) {
            if (avp instanceof final GroupedAVP grouped) {
                result.add(grouped);
            }
        }
        return result;
    }

    default void addAllSupportedFeatures(final Collection<List<AVP>> values) {
        for (final List<AVP> avps : values) {
            addSupportedFeatures(avps);
        }
    }
}
