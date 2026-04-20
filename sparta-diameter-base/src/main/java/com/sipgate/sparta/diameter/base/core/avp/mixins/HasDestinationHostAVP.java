package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

/**
 * Interface for Diameter messages that include Destination-Host AVP.
 * <p>
 * This interface provides default implementations for handling the Destination-Host AVP
 * as defined in RFC 6733. The Destination-Host AVP identifies the endpoint where the Diameter message should be routed.
 * </p>
 */
public interface HasDestinationHostAVP extends AVPContainer {

    /**
     * Sets the Destination-Host AVP.
     *
     * @param destinationHost The destination host identifier to set.
     */
    default void setDestinationHost(final String destinationHost) {
        setAVP(AVP.create(new AVPKey(DiameterConstants.AVP_DESTINATION_HOST, 0), destinationHost));
    }

    /**
     * Gets the Destination-Host from this message.
     *
     * @return The destination host identifier, or null if not found.
     */
    default String getDestinationHost() {
        final AVP destinationHostAVP = findAVP(new AVPKey(DiameterConstants.AVP_DESTINATION_HOST, 0));
        if (destinationHostAVP != null) {
            return destinationHostAVP.getDataAsString();
        }
        return null;
    }
}
