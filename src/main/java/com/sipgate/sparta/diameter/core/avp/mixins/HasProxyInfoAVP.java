package com.sipgate.sparta.diameter.core.avp.mixins;

import com.sipgate.sparta.diameter.core.avp.AVP;
import com.sipgate.sparta.diameter.core.DiameterConstants;
import com.sipgate.sparta.diameter.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.core.avp.GroupedAVP;

/**
 * Interface for Diameter messages that include Proxy-Info AVP.
 * <p>
 * This interface provides default implementations for handling the Proxy-Info AVP
 * as defined in RFC 6733. The Proxy-Info AVP is used to convey proxy-specific information.
 * </p>
 */
public interface HasProxyInfoAVP<T extends HasProxyInfoAVP<T>> extends AVPContainer {

    /**
     * Sets the Proxy-Info AVP.
     *
     * @param proxyInfo The proxy info to set.
     */
    default T setProxyInfo(final GroupedAVP proxyInfo) {
        setAVP(proxyInfo);
        return self();
    }

    /**
     * Gets the Proxy-Info from this message.
     *
     * @return The proxy info, or null if not found.
     */
    default GroupedAVP getProxyInfo() {
        final AVP proxyInfoAVP = findAVP(DiameterConstants.AVP_PROXY_INFO);
        if (proxyInfoAVP != null) {
            return (GroupedAVP) proxyInfoAVP;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    default T self() {
        return (T) this;
    }
}
