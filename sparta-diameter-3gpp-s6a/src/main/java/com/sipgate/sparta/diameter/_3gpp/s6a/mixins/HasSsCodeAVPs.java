package com.sipgate.sparta.diameter._3gpp.s6a.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.s6a.S6aConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** Mixin for messages carrying zero or more SS-Code AVPs (3GPP TS 29.272 §7.3.87, code 1476). */
public interface HasSsCodeAVPs extends AVPContainer {

    default void addSsCode(final byte[] value) {
        addAVP(AVP.create(new AVPKey(S6aConstants.AVP_SS_CODE, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default List<byte[]> getSsCodes() {
        final List<byte[]> result = new ArrayList<>();
        for (final AVP avp : findAVPs(new AVPKey(S6aConstants.AVP_SS_CODE, _3gppConstants.VENDOR_ID_3GPP))) {
            result.add(avp.getDataAsOctetString());
        }
        return result;
    }

    default void addAllSsCodes(final Collection<byte[]> values) {
        for (final byte[] v : values) {
            addSsCode(v);
        }
    }
}
