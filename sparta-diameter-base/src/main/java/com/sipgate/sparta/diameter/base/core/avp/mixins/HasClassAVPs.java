package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Mixin for Diameter messages carrying zero or more Class AVPs.
 * <p>
 * RFC 6733 defines {@code * [ Class ]} in STR/STA.
 * Method names use the {@code ClassAVP} suffix to avoid collision with
 * {@link Object#getClass()}.
 * </p>
 */
public interface HasClassAVPs<T extends HasClassAVPs<T>> extends AVPContainer<T> {

    default T addClassAVP(final byte[] classValue) {
        addAVP(AVP.create(DiameterConstants.AVP_CLASS, classValue));
        return self();
    }

    default List<byte[]> getClassAVPs() {
        final List<byte[]> result = new ArrayList<>();
        for (final AVP avp : findAVPs(DiameterConstants.AVP_CLASS)) {
            result.add(avp.getData());
        }
        return result;
    }

    default byte[] getFirstClassAVP() {
        final List<byte[]> all = getClassAVPs();
        return all.isEmpty() ? null : all.get(0);
    }

    default T addAllClassAVPs(final Collection<byte[]> classValues) {
        for (final byte[] value : classValues) {
            addClassAVP(value);
        }
        return self();
    }
}
