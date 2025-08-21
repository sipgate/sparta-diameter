package com.sipgate.sparta.diameter.base;

/**
 * Interface for Diameter messages that include Origin-State-Id AVP.
 * Can be used by any message type that needs to handle Origin-State-Id.
 * This interface provides default implementations that can be used as mixins.
 */
public interface OriginStateAware extends DiameterMessage {

    /**
     * Sets the Origin-State-Id AVP.
     * This AVP is used to detect peer restarts.
     */
    default void setOriginStateId(final int originStateId) {
        setAVP(AVP.createIntegerAVP(DiameterConstants.ORIGIN_STATE_ID, true, originStateId));
    }

    /**
     * Gets the Origin-State-Id from this message.
     * @return the origin state id, or -1 if not found
     */
    default int getOriginStateId() {
        final AVP originStateIdAVP = findAVP(DiameterConstants.ORIGIN_STATE_ID);
        if (originStateIdAVP != null && originStateIdAVP.getData().length >= 4) {
            return originStateIdAVP.getDataAsInt();
        }
        return -1;
    }
}
