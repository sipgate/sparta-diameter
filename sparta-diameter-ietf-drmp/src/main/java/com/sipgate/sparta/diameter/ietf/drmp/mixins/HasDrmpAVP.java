package com.sipgate.sparta.diameter.ietf.drmp.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.drmp.DrmpConstants;

/**
 * Mixin for Diameter messages carrying the DRMP AVP (RFC 7944 §9.1).
 * <p>
 * Priority values range from 0 (highest) to 15 (lowest). Use the
 * {@code DrmpConstants.PRIORITY_*} constants. Absent DRMP implies
 * {@code PRIORITY_10} per RFC 7944 §8.
 * </p>
 */
public interface HasDrmpAVP extends AVPContainer {

    default void setDrmp(final int priority) {
        setAVP(AVP.create(new AVPKey(DrmpConstants.AVP_DRMP, 0), priority));
    }

    default int getDrmp() {
        final var avp = findAVP(new AVPKey(DrmpConstants.AVP_DRMP, 0));
        return avp != null ? avp.getDataAsInt() : -1;
    }
}
