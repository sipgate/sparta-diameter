package com.sipgate.sparta.diameter._3gpp.s6a.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** Mixin for messages carrying zero or more External-Identifier AVPs (3GPP TS 29.336 §6.4.11, code 3111). */
public interface HasExternalIdentifierAVPs extends AVPContainer {

    default void addExternalIdentifier(final String value) {
        addAVP(AVP.create(new AVPKey(_3gppConstants.AVP_EXTERNAL_IDENTIFIER, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default List<String> getExternalIdentifiers() {
        final List<String> result = new ArrayList<>();
        for (final AVP avp : findAVPs(new AVPKey(_3gppConstants.AVP_EXTERNAL_IDENTIFIER, _3gppConstants.VENDOR_ID_3GPP))) {
            result.add(avp.getDataAsString());
        }
        return result;
    }

    default void addAllExternalIdentifiers(final Collection<String> values) {
        for (final String v : values) {
            addExternalIdentifier(v);
        }
    }
}
