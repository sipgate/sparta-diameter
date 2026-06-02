package com.sipgate.sparta.diameter._3gpp.s6a.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.s6a.S6aConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** Mixin for messages carrying zero or more User-Id AVPs (3GPP TS 29.272 §7.3.50, code 1444). */
public interface HasUserIdAVPs extends AVPContainer {

    default void addUserId(final String value) {
        addAVP(AVP.create(new AVPKey(S6aConstants.AVP_USER_ID, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default List<String> getUserIds() {
        final List<String> result = new ArrayList<>();
        for (final AVP avp : findAVPs(new AVPKey(S6aConstants.AVP_USER_ID, _3gppConstants.VENDOR_ID_3GPP))) {
            result.add(avp.getDataAsString());
        }
        return result;
    }

    default void addAllUserIds(final Collection<String> values) {
        for (final String v : values) {
            addUserId(v);
        }
    }
}
