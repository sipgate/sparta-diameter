package com.sipgate.sparta.diameter._3gpp.sgdgdd.messages;

import com.sipgate.sparta.diameter._3gpp.common.mixins.HasAbsentUserDiagnosticSmAVP;
import com.sipgate.sparta.diameter._3gpp.common.mixins.HasEpsLocationInformationAVP;
import com.sipgate.sparta.diameter._3gpp.common.mixins.HasSupportedFeaturesAVPs;
import com.sipgate.sparta.diameter._3gpp.common.mixins.HasUserIdentifierAVP;
import com.sipgate.sparta.diameter._3gpp.sgdgdd.SgdGddConstants;
import com.sipgate.sparta.diameter._3gpp.sgdgdd.mixins.HasRequestedRetransmissionTimeAVP;
import com.sipgate.sparta.diameter._3gpp.sgdgdd.mixins.HasSmDeliveryFailureCauseAVP;
import com.sipgate.sparta.diameter._3gpp.sgdgdd.mixins.HasSmRpUiAVP;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.IncomingAnswer;
import com.sipgate.sparta.diameter.base.core.OutgoingAnswer;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasAuthSessionStateAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasExperimentalResultAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasFailedAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasProxyInfoAVPs;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasRouteRecordAVPs;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasSessionIdAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasVendorSpecificApplicationIdAVP;
import com.sipgate.sparta.diameter.ietf.drmp.mixins.HasDrmpAVP;

/**
 * MT-Forward-Short-Message Answer (TFA) — 3GPP TS 29.338 §6.3.2.6.
 */
public interface MtForwardShortMessageAnswer<T extends MtForwardShortMessageAnswer<T>>
        extends HasSessionIdAVP<T>, HasDrmpAVP<T>, HasVendorSpecificApplicationIdAVP<T>,
                HasExperimentalResultAVP<T>, HasAuthSessionStateAVP<T>,
                HasSupportedFeaturesAVPs<T>,
                HasAbsentUserDiagnosticSmAVP<T>,
                HasSmDeliveryFailureCauseAVP<T>, HasSmRpUiAVP<T>,
                HasRequestedRetransmissionTimeAVP<T>, HasUserIdentifierAVP<T>,
                HasEpsLocationInformationAVP<T>,
                HasFailedAVP<T>, HasProxyInfoAVPs<T>, HasRouteRecordAVPs<T> {

    final class In extends IncomingAnswer<In>
            implements MtForwardShortMessageAnswer<In> {

        In(final HopByHopId hopByHop, final EndToEndId endToEnd) {
            super(SgdGddConstants.CMD_MT_FORWARD_SHORT_MESSAGE, true,
                  SgdGddConstants.APP_ID_SGD_GDD, hopByHop, endToEnd);
        }
    }

    final class Out extends OutgoingAnswer<Out>
            implements MtForwardShortMessageAnswer<Out> {

        Out(final HopByHopId hopByHop, final EndToEndId endToEnd) {
            super(SgdGddConstants.CMD_MT_FORWARD_SHORT_MESSAGE, true,
                  SgdGddConstants.APP_ID_SGD_GDD, hopByHop, endToEnd);
        }
    }
}
