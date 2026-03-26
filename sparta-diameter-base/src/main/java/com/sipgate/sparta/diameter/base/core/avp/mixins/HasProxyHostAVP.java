package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

/**
 * Interface for Diameter messages that include Proxy-Host AVP.
 * <p>
 * This interface provides default implementations for handling the Proxy-Host AVP
 * as defined in RFC 6733. The Proxy-Host AVP contains the identity of the host that added the Proxy-Info AVP.
 * </p>
 */
public interface HasProxyHostAVP<T extends HasProxyHostAVP<T>> extends AVPContainer<T> {

    /**
     * Sets the Proxy-Host AVP.
     *
     * @param proxyHost The proxy host identifier to set.
     */
    default T setProxyHost(final String proxyHost) {
        setAVP(AVP.create(DiameterConstants.AVP_PROXY_HOST, proxyHost));
        return self();
    }

    /**
     * Gets the Proxy-Host from this message.
     *
     * @return The proxy host identifier, or null if not found.
     */
    default String getProxyHost() {
        final AVP proxyHostAVP = findAVP(DiameterConstants.AVP_PROXY_HOST);
        if (proxyHostAVP != null) {
            return proxyHostAVP.getDataAsString();
        }
        return null;
    }
}
