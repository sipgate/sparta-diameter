package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

/**
 * Interface for Diameter messages that include Proxy-Info AVP.
 * <p>
 * This interface provides default implementations for handling the Proxy-Info AVP
 * as defined in RFC 6733. The Proxy-Info AVP is used to convey proxy-specific information.
 * </p>
 */
public interface HasProxyInfoAVP<T extends HasProxyInfoAVP<T>> extends AVPContainer<T> {

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
        final AVP proxyInfoAVP = findAVP(new AVPKey(DiameterConstants.AVP_PROXY_INFO, 0));
        if (proxyInfoAVP != null) {
            return (GroupedAVP) proxyInfoAVP;
        }
        return null;
    }
}
