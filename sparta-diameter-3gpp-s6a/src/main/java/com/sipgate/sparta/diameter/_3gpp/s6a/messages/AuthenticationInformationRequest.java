package com.sipgate.sparta.diameter._3gpp.s6a.messages;

import com.sipgate.sparta.diameter._3gpp.common._3gppRequest;
import com.sipgate.sparta.diameter._3gpp.s6a.S6aConstants;
import com.sipgate.sparta.diameter._3gpp.common.mixins.HasSupportedFeaturesAVPs;
import com.sipgate.sparta.diameter._3gpp.s6a.mixins.HasAirFlagsAVP;
import com.sipgate.sparta.diameter._3gpp.s6a.mixins.HasRequestedEutranAuthenticationInfoAVP;
import com.sipgate.sparta.diameter._3gpp.s6a.mixins.HasRequestedUtranGeranAuthenticationInfoAVP;
import com.sipgate.sparta.diameter._3gpp.s6a.mixins.HasVisitedPlmnIdAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasProxyInfoAVPs;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasRouteRecordAVPs;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasUserNameAVP;
import com.sipgate.sparta.diameter.ietf.doic.mixins.HasOcSupportedFeaturesAVP;
import com.sipgate.sparta.diameter.ietf.drmp.mixins.HasDrmpAVP;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.IncomingRequest;
import com.sipgate.sparta.diameter.base.core.OutgoingRequest;

/** Authentication-Information-Request/Answer (Request) (AIR) — 3GPP TS 29.272 §7.2.5-6. */
public interface AuthenticationInformationRequest
        extends _3gppRequest,
                HasDrmpAVP, HasUserNameAVP, HasOcSupportedFeaturesAVP, HasSupportedFeaturesAVPs, HasRequestedEutranAuthenticationInfoAVP, HasRequestedUtranGeranAuthenticationInfoAVP, HasVisitedPlmnIdAVP, HasAirFlagsAVP, HasProxyInfoAVPs, HasRouteRecordAVPs {

    final class In extends IncomingRequest<AuthenticationInformationAnswer.Out> implements AuthenticationInformationRequest {
        In(final HopByHopId hopByHop, final EndToEndId endToEnd, final boolean retransmitted) {
            super(S6aConstants.CMD_AUTHENTICATION_INFORMATION, true, retransmitted, S6aConstants.APP_ID_S6A_S6D, hopByHop, endToEnd);
        }
    }

    final class Out extends OutgoingRequest<AuthenticationInformationAnswer.In> implements AuthenticationInformationRequest {
        public Out() {
            super(S6aConstants.CMD_AUTHENTICATION_INFORMATION, true, S6aConstants.APP_ID_S6A_S6D);
        }
    }
}
