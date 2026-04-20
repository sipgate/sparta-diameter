package com.sipgate.sparta.diameter._3gpp.sgdgdd.messages;

import com.sipgate.sparta.diameter._3gpp.common.mixins.HasEpsLocationInformationAVP;
import com.sipgate.sparta.diameter._3gpp.common.mixins.HasSmDeliveryOutcomeAVP;
import com.sipgate.sparta.diameter._3gpp.common.mixins.HasSupportedFeaturesAVPs;
import com.sipgate.sparta.diameter._3gpp.common.mixins.HasUserIdentifierAVP;
import com.sipgate.sparta.diameter._3gpp.sgdgdd.SgdGddConstants;
import com.sipgate.sparta.diameter._3gpp.sgdgdd.mixins.HasOfrFlagsAVP;
import com.sipgate.sparta.diameter._3gpp.sgdgdd.mixins.HasScAddressAVP;
import com.sipgate.sparta.diameter._3gpp.sgdgdd.mixins.HasSmRpUiAVP;
import com.sipgate.sparta.diameter._3gpp.sgdgdd.mixins.HasSmsMiCorrelationIdAVP;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.IncomingRequest;
import com.sipgate.sparta.diameter.base.core.OutgoingRequest;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasAuthSessionStateAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasDestinationHostAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasDestinationRealmAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasProxyInfoAVPs;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasRouteRecordAVPs;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasSessionIdAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasVendorSpecificApplicationIdAVP;
import com.sipgate.sparta.diameter.ietf.drmp.mixins.HasDrmpAVP;

/**
 * MO-Forward-Short-Message Request (OFR) — 3GPP TS 29.338 §6.3.2.3.
 */
public interface MoForwardShortMessageRequest
        extends HasSessionIdAVP, HasDrmpAVP, HasVendorSpecificApplicationIdAVP,
                HasAuthSessionStateAVP,
                HasScAddressAVP, HasOfrFlagsAVP,
                HasSupportedFeaturesAVPs, HasUserIdentifierAVP,
                HasEpsLocationInformationAVP,
                HasSmRpUiAVP, HasSmsMiCorrelationIdAVP,
                HasSmDeliveryOutcomeAVP,
                HasProxyInfoAVPs, HasRouteRecordAVPs {

    final class In extends IncomingRequest<MoForwardShortMessageAnswer.Out>
            implements MoForwardShortMessageRequest {

        In(final HopByHopId hopByHop, final EndToEndId endToEnd, final boolean retransmitted) {
            super(SgdGddConstants.CMD_MO_FORWARD_SHORT_MESSAGE, true, retransmitted,
                SgdGddConstants.APP_ID_SGD_GDD, hopByHop, endToEnd);
        }
    }

    final class Out extends OutgoingRequest<MoForwardShortMessageAnswer.In>
            implements MoForwardShortMessageRequest {

        public Out() {
            super(SgdGddConstants.CMD_MO_FORWARD_SHORT_MESSAGE, true,
                  SgdGddConstants.APP_ID_SGD_GDD);
        }
    }
}
