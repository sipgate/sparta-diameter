package com.sipgate.sparta.diameter._3gpp.cxdx.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.cxdx.CxDxConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

import java.util.ArrayList;
import java.util.List;

/**
 * Mixin for messages carrying zero or more Public-Identity AVPs (3GPP TS 29.229 §6.3.2, code 601).
 * <p>
 * Used where the ABNF declares {@code *[ Public-Identity ]} (SAR, RTR). For the single-valued
 * {@code { Public-Identity }} / {@code [ Public-Identity ]} cases (MAR, MAA) use
 * {@link HasPublicIdentityAVP}.
 * </p>
 */
public interface HasPublicIdentityAVPs extends AVPContainer {

    default void addPublicIdentity(final String value) {
        addAVP(AVP.create(new AVPKey(CxDxConstants.AVP_PUBLIC_IDENTITY, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default List<String> getPublicIdentities() {
        final List<String> result = new ArrayList<>();
        for (final AVP avp : findAVPs(new AVPKey(CxDxConstants.AVP_PUBLIC_IDENTITY, _3gppConstants.VENDOR_ID_3GPP))) {
            result.add(avp.getDataAsString());
        }
        return result;
    }
}
