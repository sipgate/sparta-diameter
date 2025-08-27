package com.sipgate.sparta.diameter.core.avp.mixins;

import com.sipgate.sparta.diameter.core.avp.AVP;
import com.sipgate.sparta.diameter.core.DiameterConstants;
import com.sipgate.sparta.diameter.core.avp.AVPContainer;

/**
 * Interface for Diameter messages that include Proxy-State AVP.
 * <p>
 * This interface provides default implementations for handling the Proxy-State AVP
 * as defined in RFC 6733. The Proxy-State AVP contains state information that a proxy wishes to preserve.
 * </p>
 */
public interface HasProxyStateAVP<T extends HasProxyStateAVP<T>> extends AVPContainer {

    /**
     * Sets the Proxy-State AVP.
     *
     * @param proxyState The proxy state to set.
     */
    default T setProxyState(final byte[] proxyState) {
        setAVP(AVP.create(DiameterConstants.AVP_PROXY_STATE, proxyState));
        return self();
    }

    /**
     * Gets the Proxy-State from this message.
     *
     * @return The proxy state, or null if not found.
     */
    default byte[] getProxyState() {
        final AVP proxyStateAVP = findAVP(DiameterConstants.AVP_PROXY_STATE);
        if (proxyStateAVP != null) {
            return proxyStateAVP.getData();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    default T self() {
        return (T) this;
    }
}
