package com.sipgate.sparta.diameter._3gpp.s6a.messages;

import com.sipgate.sparta.diameter._3gpp.common._3gppRequest;
import com.sipgate.sparta.diameter._3gpp.s6a.S6aConstants;
import com.sipgate.sparta.diameter._3gpp.common.mixins.HasSupportedFeaturesAVPs;
import com.sipgate.sparta.diameter._3gpp.s6a.mixins.HasAlertReasonAVP;
import com.sipgate.sparta.diameter._3gpp.s6a.mixins.HasContextIdentifierAVP;
import com.sipgate.sparta.diameter._3gpp.s6a.mixins.HasEmergencyServicesAVP;
import com.sipgate.sparta.diameter._3gpp.s6a.mixins.HasHomogeneousSupportOfImsVoiceOverPsSessionsAVP;
import com.sipgate.sparta.diameter._3gpp.s6a.mixins.HasMaximumUeAvailabilityTimeAVP;
import com.sipgate.sparta.diameter.ietf.mip6.split.mixins.HasMip6AgentInfoAVP;
import com.sipgate.sparta.diameter._3gpp.s6t.mixins.HasMonitoringEventConfigStatusAVPs;
import com.sipgate.sparta.diameter._3gpp.s6a.mixins.HasNorFlagsAVP;
import com.sipgate.sparta.diameter.ietf.mip6.integrated.mixins.HasServiceSelectionAVP;
import com.sipgate.sparta.diameter._3gpp.s6a.mixins.HasTerminalInformationAVP;
import com.sipgate.sparta.diameter._3gpp.s6a.mixins.HasUeSrvccCapabilityAVP;
import com.sipgate.sparta.diameter._3gpp.cxdx.mixins.HasVisitedNetworkIdentifierAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasProxyInfoAVPs;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasRouteRecordAVPs;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasUserNameAVP;
import com.sipgate.sparta.diameter.ietf.doic.mixins.HasOcSupportedFeaturesAVP;
import com.sipgate.sparta.diameter.ietf.drmp.mixins.HasDrmpAVP;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.IncomingRequest;
import com.sipgate.sparta.diameter.base.core.OutgoingRequest;

/** Notify-Request/Answer (Request) (NOR) — 3GPP TS 29.272 §7.2.17-18. */
public interface NotifyRequest
        extends _3gppRequest,
                HasDrmpAVP, HasUserNameAVP, HasOcSupportedFeaturesAVP, HasSupportedFeaturesAVPs, HasTerminalInformationAVP, HasMip6AgentInfoAVP, HasVisitedNetworkIdentifierAVP, HasContextIdentifierAVP, HasServiceSelectionAVP, HasAlertReasonAVP, HasUeSrvccCapabilityAVP, HasNorFlagsAVP, HasHomogeneousSupportOfImsVoiceOverPsSessionsAVP, HasMaximumUeAvailabilityTimeAVP, HasMonitoringEventConfigStatusAVPs, HasEmergencyServicesAVP, HasProxyInfoAVPs, HasRouteRecordAVPs {

    final class In extends IncomingRequest<NotifyAnswer.Out> implements NotifyRequest {
        In(final HopByHopId hopByHop, final EndToEndId endToEnd, final boolean retransmitted) {
            super(S6aConstants.CMD_NOTIFY, true, retransmitted, S6aConstants.APP_ID_S6A_S6D, hopByHop, endToEnd);
        }
    }

    final class Out extends OutgoingRequest<NotifyAnswer.In> implements NotifyRequest {
        public Out() {
            super(S6aConstants.CMD_NOTIFY, true, S6aConstants.APP_ID_S6A_S6D);
        }
    }
}
