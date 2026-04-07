package com.sipgate.sparta.diameter._3gpp.common.mixins;

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
public interface HasSupportedFeaturesAVPs<T extends HasSupportedFeaturesAVPs<T>> extends AVPContainer<T> {

    default T addSupportedFeatures(final GroupedAVP value) {
        addAVP(AVP.create(new AVPKey(_3gppConstants.AVP_SUPPORTED_FEATURES, _3gppConstants.VENDOR_ID_3GPP), value.getAVPs()));
        return self();
    }

    default List<GroupedAVP> getSupportedFeatures() {
        final List<GroupedAVP> result = new ArrayList<>();
        for (final AVP avp : findAVPs(new AVPKey(_3gppConstants.AVP_SUPPORTED_FEATURES, _3gppConstants.VENDOR_ID_3GPP))) {
            if (avp instanceof final GroupedAVP grouped) {
                result.add(grouped);
            }
        }
        return result;
    }

    default T addAllSupportedFeatures(final Collection<GroupedAVP> values) {
        for (final GroupedAVP value : values) {
            addSupportedFeatures(value);
        }
        return self();
    }
}
