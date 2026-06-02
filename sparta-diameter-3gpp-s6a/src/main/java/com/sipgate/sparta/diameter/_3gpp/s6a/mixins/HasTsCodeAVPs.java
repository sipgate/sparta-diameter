package com.sipgate.sparta.diameter._3gpp.s6a.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.s6a.S6aConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** Mixin for messages carrying zero or more TS-Code AVPs (3GPP TS 29.272 §7.3.100, code 1487). */
public interface HasTsCodeAVPs extends AVPContainer {

    default void addTsCode(final byte[] value) {
        addAVP(AVP.create(new AVPKey(S6aConstants.AVP_TS_CODE, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default List<byte[]> getTsCodes() {
        final List<byte[]> result = new ArrayList<>();
        for (final AVP avp : findAVPs(new AVPKey(S6aConstants.AVP_TS_CODE, _3gppConstants.VENDOR_ID_3GPP))) {
            result.add(avp.getDataAsOctetString());
        }
        return result;
    }

    default void addAllTsCodes(final Collection<byte[]> values) {
        for (final byte[] v : values) {
            addTsCode(v);
        }
    }
}
