package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

/**
 * Interface for Diameter messages that include Proxy-State AVP.
 * <p>
 * This interface provides default implementations for handling the Proxy-State AVP
 * as defined in RFC 6733. The Proxy-State AVP contains state information that a proxy wishes to preserve.
 * </p>
 */
public interface HasProxyStateAVP extends AVPContainer {

    /**
     * Sets the Proxy-State AVP.
     *
     * @param proxyState The proxy state to set.
     */
    default void setProxyState(final byte[] proxyState) {
        setAVP(AVP.create(new AVPKey(DiameterConstants.AVP_PROXY_STATE, 0), proxyState));
    }

    /**
     * Gets the Proxy-State from this message.
     *
     * @return The proxy state, or null if not found.
     */
    default byte[] getProxyState() {
        final AVP proxyStateAVP = findAVP(new AVPKey(DiameterConstants.AVP_PROXY_STATE, 0));
        if (proxyStateAVP != null) {
            return proxyStateAVP.getData();
        }
        return null;
    }
}
