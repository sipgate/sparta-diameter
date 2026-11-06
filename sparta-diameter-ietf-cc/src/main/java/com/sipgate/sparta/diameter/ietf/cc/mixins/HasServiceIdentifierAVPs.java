package com.sipgate.sparta.diameter.ietf.cc.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.cc.CcConstants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** Mixin for messages carrying zero or more Service-Identifier AVPs (RFC 4006 §8.28, code 439). */
public interface HasServiceIdentifierAVPs extends AVPContainer {

    default void addServiceIdentifier(final long value) {
        addAVP(AVP.create(new AVPKey(CcConstants.AVP_SERVICE_IDENTIFIER, 0), value));
    }

    default List<Long> getServiceIdentifiers() {
        final List<Long> result = new ArrayList<>();
        for (final AVP avp : findAVPs(new AVPKey(CcConstants.AVP_SERVICE_IDENTIFIER, 0))) {
            result.add(avp.getDataAsUnsignedInt());
        }
        return result;
    }

    default void addAllServiceIdentifiers(final Collection<Long> values) {
        for (final long value : values) {
            addServiceIdentifier(value);
        }
    }
}
