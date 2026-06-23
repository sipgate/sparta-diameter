package com.sipgate.sparta.diameter._3gpp.swx.messages;

import com.sipgate.sparta.diameter._3gpp.common._3gppRequest;
import com.sipgate.sparta.diameter._3gpp.s6a.mixins.HasActiveApnAVPs;
import com.sipgate.sparta.diameter._3gpp.common.mixins.HasContextIdentifierAVP;
import com.sipgate.sparta.diameter._3gpp.s6a.mixins.HasEmergencyServicesAVP;
import com.sipgate.sparta.diameter._3gpp.common.mixins.HasServerAssignmentTypeAVP;
import com.sipgate.sparta.diameter._3gpp.common.mixins.HasSupportedFeaturesAVPs;
import com.sipgate.sparta.diameter._3gpp.common.mixins.HasTerminalInformationAVP;
import com.sipgate.sparta.diameter._3gpp.common.mixins.HasVisitedNetworkIdentifierAVP;
import com.sipgate.sparta.diameter._3gpp.swx.SwxConstants;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.IncomingRequest;
import com.sipgate.sparta.diameter.base.core.OutgoingRequest;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasUserNameAVP;
import com.sipgate.sparta.diameter.ietf.doic.mixins.HasOcSupportedFeaturesAVP;
import com.sipgate.sparta.diameter.ietf.drmp.mixins.HasDrmpAVP;
import com.sipgate.sparta.diameter.ietf.mip6.integrated.mixins.HasServiceSelectionAVP;
import com.sipgate.sparta.diameter.ietf.mip6.split.mixins.HasMip6AgentInfoAVP;

/** Server-Assignment-Request (SAR) — 3GPP TS 29.273 §8.1.2.4.2. */
public interface ServerAssignmentRequest
        extends _3gppRequest,
                HasDrmpAVP, HasUserNameAVP, HasOcSupportedFeaturesAVP, HasSupportedFeaturesAVPs,
                HasServiceSelectionAVP, HasContextIdentifierAVP, HasMip6AgentInfoAVP,
                HasVisitedNetworkIdentifierAVP, HasServerAssignmentTypeAVP,
                HasActiveApnAVPs, HasTerminalInformationAVP, HasEmergencyServicesAVP {

    final class In extends IncomingRequest<ServerAssignmentAnswer.Out> implements ServerAssignmentRequest {
        In(final HopByHopId hopByHop, final EndToEndId endToEnd, final boolean retransmitted) {
            super(SwxConstants.CMD_SERVER_ASSIGNMENT, true, retransmitted, SwxConstants.APP_ID_SWX, hopByHop, endToEnd);
        }
    }

    final class Out extends OutgoingRequest<ServerAssignmentAnswer.In> implements ServerAssignmentRequest {
        public Out() {
            super(SwxConstants.CMD_SERVER_ASSIGNMENT, true, SwxConstants.APP_ID_SWX);
        }
    }
}
