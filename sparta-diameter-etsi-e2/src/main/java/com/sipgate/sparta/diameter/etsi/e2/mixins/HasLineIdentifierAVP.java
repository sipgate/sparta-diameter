package com.sipgate.sparta.diameter.etsi.e2.mixins;

import com.sipgate.sparta.diameter.etsi.e2.E2Constants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying a Line-Identifier AVP (ETSI ES 283 035 §7.3.5, code 500). */
public interface HasLineIdentifierAVP extends AVPContainer {

    default void setLineIdentifier(final byte[] value) {
        setAVP(AVP.create(new AVPKey(E2Constants.AVP_LINE_IDENTIFIER, E2Constants.VENDOR_ID_ETSI), value));
    }

    default byte[] getLineIdentifier() {
        final var avp = findAVP(new AVPKey(E2Constants.AVP_LINE_IDENTIFIER, E2Constants.VENDOR_ID_ETSI));
        return avp != null ? avp.getDataAsOctetString() : null;
    }
}
