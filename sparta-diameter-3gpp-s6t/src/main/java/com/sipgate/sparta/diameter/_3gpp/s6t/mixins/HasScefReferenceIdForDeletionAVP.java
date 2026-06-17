package com.sipgate.sparta.diameter._3gpp.s6t.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.s6t.S6tConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying the SCEF-Reference-ID-for-Deletion AVP (3GPP TS 29.336 §8.4.6, code 3126). */
public interface HasScefReferenceIdForDeletionAVP extends AVPContainer {

    default void setScefReferenceIdForDeletion(final long value) {
        setAVP(AVP.create(new AVPKey(S6tConstants.AVP_SCEF_REFERENCE_ID_FOR_DELETION, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default long getScefReferenceIdForDeletion() {
        final var avp = findAVP(new AVPKey(S6tConstants.AVP_SCEF_REFERENCE_ID_FOR_DELETION, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsUnsignedInt() : 0L;
    }
}
