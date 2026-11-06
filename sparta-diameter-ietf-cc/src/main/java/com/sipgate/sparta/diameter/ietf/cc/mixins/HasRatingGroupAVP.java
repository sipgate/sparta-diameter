package com.sipgate.sparta.diameter.ietf.cc.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.cc.CcConstants;

/** Mixin for messages carrying the Rating-Group AVP (RFC 4006 §8.29, code 432). */
public interface HasRatingGroupAVP extends AVPContainer {

    default void setRatingGroup(final long value) {
        setAVP(AVP.create(new AVPKey(CcConstants.AVP_RATING_GROUP, 0), value));
    }

    default long getRatingGroup() {
        final var avp = findAVP(new AVPKey(CcConstants.AVP_RATING_GROUP, 0));
        return avp != null ? avp.getDataAsUnsignedInt() : -1L;
    }
}
