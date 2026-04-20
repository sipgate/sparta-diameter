package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

/**
 * Interface for Diameter messages that include Route-Record AVP.
 * <p>
 * This interface provides default implementations for handling the Route-Record AVP
 * as defined in RFC 6733. The Route-Record AVP is used to identify the Diameter peers that have processed the message.
 * </p>
 */
public interface HasRouteRecordAVP extends AVPContainer {

    /**
     * Sets the Route-Record AVP.
     *
     * @param routeRecord The route record to set.
     */
    default void setRouteRecord(final String routeRecord) {
        setAVP(AVP.create(new AVPKey(DiameterConstants.AVP_ROUTE_RECORD, 0), routeRecord));
    }

    /**
     * Gets the Route-Record from this message.
     *
     * @return The route record, or null if not found.
     */
    default String getRouteRecord() {
        final AVP routeRecordAVP = findAVP(new AVPKey(DiameterConstants.AVP_ROUTE_RECORD, 0));
        if (routeRecordAVP != null) {
            return routeRecordAVP.getDataAsString();
        }
        return null;
    }
}
