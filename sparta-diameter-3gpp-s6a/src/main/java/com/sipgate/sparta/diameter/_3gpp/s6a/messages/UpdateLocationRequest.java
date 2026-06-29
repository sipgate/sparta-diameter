package com.sipgate.sparta.diameter._3gpp.s6a.messages;

import com.sipgate.sparta.diameter._3gpp.common._3gppRequest;
import com.sipgate.sparta.diameter._3gpp.s6a.S6aConstants;
import com.sipgate.sparta.diameter._3gpp.common.mixins.HasMmeNumberForMtSmsAVP;
import com.sipgate.sparta.diameter._3gpp.common.mixins.HasSgsnNumberAVP;
import com.sipgate.sparta.diameter._3gpp.common.mixins.HasSupportedFeaturesAVPs;
import com.sipgate.sparta.diameter._3gpp.s6a.mixins.HasActiveApnAVPs;
import com.sipgate.sparta.diameter._3gpp.s6a.mixins.HasAdjacentPlmnsAVP;
import com.sipgate.sparta.diameter._3gpp.s6a.mixins.HasCoupledNodeDiameterIdAVP;
import com.sipgate.sparta.diameter._3gpp.s6a.mixins.HasEquivalentPlmnListAVP;
import com.sipgate.sparta.diameter._3gpp.slh.mixins.HasGmlcAddressAVP;
import com.sipgate.sparta.diameter._3gpp.s6a.mixins.HasHomogeneousSupportOfImsVoiceOverPsSessionsAVP;
import com.sipgate.sparta.diameter._3gpp.gx.mixins.HasRatTypeAVP;
import com.sipgate.sparta.diameter._3gpp.s6a.mixins.HasSfProvisionalIndicationAVP;
import com.sipgate.sparta.diameter._3gpp.s6a.mixins.HasSfUlrTimestampAVP;
import com.sipgate.sparta.diameter._3gpp.s6a.mixins.HasSgsMmeIdentityAVP;
import com.sipgate.sparta.diameter._3gpp.s6a.mixins.HasSmsRegisterRequestAVP;
import com.sipgate.sparta.diameter._3gpp.s6a.mixins.HasSupportedServicesAVP;
import com.sipgate.sparta.diameter._3gpp.s6a.mixins.HasTerminalInformationAVP;
import com.sipgate.sparta.diameter._3gpp.s6a.mixins.HasUeSrvccCapabilityAVP;
import com.sipgate.sparta.diameter._3gpp.s6a.mixins.HasUlrFlagsAVP;
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

/** Update-Location-Request/Answer (Request) (ULR) — 3GPP TS 29.272 §7.2.3-4. */
public interface UpdateLocationRequest
        extends _3gppRequest,
                HasDrmpAVP, HasUserNameAVP, HasOcSupportedFeaturesAVP, HasSupportedFeaturesAVPs, HasTerminalInformationAVP, HasRatTypeAVP, HasUlrFlagsAVP, HasUeSrvccCapabilityAVP, HasVisitedPlmnIdAVP, HasSgsnNumberAVP, HasHomogeneousSupportOfImsVoiceOverPsSessionsAVP, HasGmlcAddressAVP, HasActiveApnAVPs, HasEquivalentPlmnListAVP, HasMmeNumberForMtSmsAVP, HasSmsRegisterRequestAVP, HasSgsMmeIdentityAVP, HasCoupledNodeDiameterIdAVP, HasAdjacentPlmnsAVP, HasSupportedServicesAVP, HasSfUlrTimestampAVP, HasSfProvisionalIndicationAVP, HasProxyInfoAVPs, HasRouteRecordAVPs {

    final class In extends IncomingRequest<UpdateLocationAnswer.Out> implements UpdateLocationRequest {
        In(final HopByHopId hopByHop, final EndToEndId endToEnd, final boolean retransmitted) {
            super(S6aConstants.CMD_UPDATE_LOCATION, true, retransmitted, S6aConstants.APP_ID_S6A_S6D, hopByHop, endToEnd);
        }

        @Override
        public String getCommandName() {
            return "Update-Location Request";
        }
    }

    final class Out extends OutgoingRequest<UpdateLocationAnswer.In> implements UpdateLocationRequest {
        public Out() {
            super(S6aConstants.CMD_UPDATE_LOCATION, true, S6aConstants.APP_ID_S6A_S6D);
        }

        @Override
        public String getCommandName() {
            return "Update-Location Request";
        }
    }
}
