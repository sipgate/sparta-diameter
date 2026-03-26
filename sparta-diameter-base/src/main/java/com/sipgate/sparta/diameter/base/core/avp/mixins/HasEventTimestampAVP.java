package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

import java.util.Date;

/**
 * Interface for Diameter messages that include Event-Timestamp AVP.
 * <p>
 * This interface provides default implementations for handling the Event-Timestamp AVP
 * as defined in RFC 6733. The Event-Timestamp AVP is used to record the time that the reported event occurred.
 * </p>
 */
public interface HasEventTimestampAVP<T extends HasEventTimestampAVP<T>> extends AVPContainer<T> {

    /**
     * Sets the Event-Timestamp AVP.
     *
     * @param eventTimestamp The event timestamp to set.
     */
    default T setEventTimestamp(final Date eventTimestamp) {
        setAVP(AVP.create(DiameterConstants.AVP_EVENT_TIMESTAMP, eventTimestamp));
        return self();
    }

    /**
     * Gets the Event-Timestamp from this message.
     *
     * @return The event timestamp, or null if not found.
     */
    default Date getEventTimestamp() {
        final AVP eventTimestampAVP = findAVP(DiameterConstants.AVP_EVENT_TIMESTAMP);
        if (eventTimestampAVP != null) {
            return eventTimestampAVP.getDataAsTime();
        }
        return null;
    }
}
