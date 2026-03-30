package com.sipgate.sparta.diameter._3gpp.sgdgdd.messages;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.common.mixins.HasMmeNumberForMtSmsAVP;
import com.sipgate.sparta.diameter._3gpp.common.mixins.HasSgsnNumberAVP;
import com.sipgate.sparta.diameter._3gpp.common.mixins.HasSupportedFeaturesAVPs;
import com.sipgate.sparta.diameter._3gpp.sgdgdd.SgdGddConstants;
import com.sipgate.sparta.diameter._3gpp.sgdgdd.mixins.HasMaximumRetransmissionTimeAVP;
import com.sipgate.sparta.diameter._3gpp.sgdgdd.mixins.HasScAddressAVP;
import com.sipgate.sparta.diameter._3gpp.sgdgdd.mixins.HasSmDeliveryStartTimeAVP;
import com.sipgate.sparta.diameter._3gpp.sgdgdd.mixins.HasSmDeliveryTimerAVP;
import com.sipgate.sparta.diameter._3gpp.sgdgdd.mixins.HasSmRpUiAVP;
import com.sipgate.sparta.diameter._3gpp.sgdgdd.mixins.HasSmsMiCorrelationIdAVP;
import com.sipgate.sparta.diameter._3gpp.sgdgdd.mixins.HasSmsGmscAddressAVP;
import com.sipgate.sparta.diameter._3gpp.sgdgdd.mixins.HasTfrFlagsAVP;
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
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasUserNameAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasVendorSpecificApplicationIdAVP;
import com.sipgate.sparta.diameter.ietf.drmp.mixins.HasDrmpAVP;

/**
 * MT-Forward-Short-Message Request (TFR) — 3GPP TS 29.338 §6.3.2.5.
 */
public interface MtForwardShortMessageRequest<T extends MtForwardShortMessageRequest<T>>
        extends HasSessionIdAVP<T>, HasDrmpAVP<T>, HasVendorSpecificApplicationIdAVP<T>,
                HasAuthSessionStateAVP<T>,
                HasDestinationHostAVP<T>, HasDestinationRealmAVP<T>,
                HasUserNameAVP<T>,
                HasSupportedFeaturesAVPs<T>, HasSmsMiCorrelationIdAVP<T>,
                HasScAddressAVP<T>, HasSmRpUiAVP<T>,
                HasMmeNumberForMtSmsAVP<T>, HasSgsnNumberAVP<T>,
                HasTfrFlagsAVP<T>, HasSmDeliveryTimerAVP<T>, HasSmDeliveryStartTimeAVP<T>,
                HasMaximumRetransmissionTimeAVP<T>, HasSmsGmscAddressAVP<T>,
                HasProxyInfoAVPs<T>, HasRouteRecordAVPs<T> {

    final class In extends IncomingRequest<In, MtForwardShortMessageAnswer.Out>
            implements MtForwardShortMessageRequest<In> {

        In(final HopByHopId hopByHop, final EndToEndId endToEnd, final boolean retransmitted) {
            super(SgdGddConstants.CMD_MT_FORWARD_SHORT_MESSAGE, true, retransmitted,
                  _3gppConstants.APP_ID_SGD_GDD, hopByHop, endToEnd);
        }
    }

    final class Out extends OutgoingRequest<Out, MtForwardShortMessageAnswer.In>
            implements MtForwardShortMessageRequest<Out> {

        public Out() {
            super(SgdGddConstants.CMD_MT_FORWARD_SHORT_MESSAGE, true,
                  _3gppConstants.APP_ID_SGD_GDD);
        }
    }
}
