package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

import java.util.List;

/**
 * Interface for Diameter messages that include Proxy-Info AVP.
 * <p>
 * This interface provides default implementations for handling the Proxy-Info AVP
 * as defined in RFC 6733. The Proxy-Info AVP is used to convey proxy-specific information.
 * </p>
 */
public interface HasProxyInfoAVP extends AVPContainer {

    /**
     * Sets the Proxy-Info AVP.
     *
     * @param avps The child AVPs of the Proxy-Info grouped AVP.
     */
    default void setProxyInfo(final List<AVP> avps) {
        setAVP(AVP.create(new AVPKey(DiameterConstants.AVP_PROXY_INFO, 0), avps));
    }

    /**
     * Gets the Proxy-Info from this message.
     *
     * @return The proxy info, or null if not found.
     */
    default AVPContainer getProxyInfo() {
        final var avp = findAVP(new AVPKey(DiameterConstants.AVP_PROXY_INFO, 0));
        return avp instanceof final GroupedAVP grouped ? grouped : null;
    }
}
