package com.sipgate.sparta.diameter._3gpp.s6a.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.s6a.S6aConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** Mixin for messages carrying zero or more Context-Identifier AVPs (3GPP TS 29.272 §7.3.27, code 1423). */
public interface HasContextIdentifierAVPs extends AVPContainer {

    default void addContextIdentifier(final long value) {
        addAVP(AVP.create(new AVPKey(S6aConstants.AVP_CONTEXT_IDENTIFIER, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default List<Long> getContextIdentifiers() {
        final List<Long> result = new ArrayList<>();
        for (final AVP avp : findAVPs(new AVPKey(S6aConstants.AVP_CONTEXT_IDENTIFIER, _3gppConstants.VENDOR_ID_3GPP))) {
            result.add(avp.getDataAsUnsignedInt());
        }
        return result;
    }

    default void addAllContextIdentifiers(final Collection<Long> values) {
        for (final Long v : values) {
            addContextIdentifier(v);
        }
    }
}
