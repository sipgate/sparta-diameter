package com.sipgate.sparta.diameter._3gpp.cxdx.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.cxdx.CxDxConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Mixin for messages carrying zero or more SCSCF-Restoration-Info grouped AVPs
 * (3GPP TS 29.229 §6.3.46, code 639).
 * <p>
 * Used where the ABNF declares {@code *[ SCSCF-Restoration-Info ]} (SAA). For the single-valued
 * {@code [ SCSCF-Restoration-Info ]} case (SAR) use {@link HasScscfRestorationInfoAVP}.
 * </p>
 */
public interface HasScscfRestorationInfoAVPs extends AVPContainer {

    default void addScscfRestorationInfo(final List<AVP> avps) {
        addAVP(AVP.create(new AVPKey(CxDxConstants.AVP_SCSCF_RESTORATION_INFO, _3gppConstants.VENDOR_ID_3GPP), avps));
    }

    default List<AVPContainer> getScscfRestorationInfos() {
        final List<AVPContainer> result = new ArrayList<>();
        for (final AVP avp : findAVPs(new AVPKey(CxDxConstants.AVP_SCSCF_RESTORATION_INFO, _3gppConstants.VENDOR_ID_3GPP))) {
            if (avp instanceof final GroupedAVP grouped) {
                result.add(grouped);
            }
        }
        return result;
    }

    default void addAllScscfRestorationInfos(final Collection<List<AVP>> values) {
        for (final List<AVP> avps : values) {
            addScscfRestorationInfo(avps);
        }
    }
}
