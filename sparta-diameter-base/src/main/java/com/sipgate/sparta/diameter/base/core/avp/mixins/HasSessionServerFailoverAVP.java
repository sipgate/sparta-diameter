package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

/**
 * Interface for Diameter messages that include Session-Server-Failover AVP.
 * <p>
 * This interface provides default implementations for handling the Session-Server-Failover AVP
 * as defined in RFC 6733. The Session-Server-Failover AVP is used to indicate the failover capabilities of the server.
 * </p>
 */
public interface HasSessionServerFailoverAVP<T extends HasSessionServerFailoverAVP<T>> extends AVPContainer<T> {

    /**
     * Sets the Session-Server-Failover AVP.
     *
     * @param sessionServerFailover The session server failover value to set.
     */
    default T setSessionServerFailover(final int sessionServerFailover) {
        setAVP(AVP.create(new AVPKey(DiameterConstants.AVP_SESSION_SERVER_FAILOVER, 0), sessionServerFailover));
        return self();
    }

    /**
     * Gets the Session-Server-Failover from this message.
     *
     * @return The session server failover value, or -1 if not found.
     */
    default int getSessionServerFailover() {
        final AVP sessionServerFailoverAVP = findAVP(new AVPKey(DiameterConstants.AVP_SESSION_SERVER_FAILOVER, 0));
        if (sessionServerFailoverAVP != null) {
            return sessionServerFailoverAVP.getDataAsInt();
        }
        return -1;
    }
}
