package com.sipgate.sparta.diameter.ietf.mip6.split.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.mip6.split.Mip6SplitConstants;

/** Mixin for containers carrying the MIP6-Home-Link-Prefix AVP (RFC 5447 §4.2.4, code 125). */
public interface HasMip6HomeLinkPrefixAVP extends AVPContainer {

    default void setMip6HomeLinkPrefix(final byte[] value) {
        setAVP(AVP.create(new AVPKey(Mip6SplitConstants.AVP_MIP6_HOME_LINK_PREFIX, 0), value));
    }

    default byte[] getMip6HomeLinkPrefix() {
        final var avp = findAVP(new AVPKey(Mip6SplitConstants.AVP_MIP6_HOME_LINK_PREFIX, 0));
        return avp != null ? avp.getDataAsOctetString() : null;
    }
}
