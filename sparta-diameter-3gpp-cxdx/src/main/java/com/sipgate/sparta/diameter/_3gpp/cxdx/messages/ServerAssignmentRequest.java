package com.sipgate.sparta.diameter._3gpp.cxdx.messages;

import com.sipgate.sparta.diameter._3gpp.common._3gppRequest;
import com.sipgate.sparta.diameter._3gpp.common.mixins.HasSupportedFeaturesAVPs;
import com.sipgate.sparta.diameter._3gpp.cxdx.CxDxConstants;
import com.sipgate.sparta.diameter._3gpp.cxdx.mixins.HasFailedPcscfAVP;
import com.sipgate.sparta.diameter._3gpp.cxdx.mixins.HasMultipleRegistrationIndicationAVP;
import com.sipgate.sparta.diameter._3gpp.cxdx.mixins.HasPublicIdentityAVPs;
import com.sipgate.sparta.diameter._3gpp.cxdx.mixins.HasSarFlagsAVP;
import com.sipgate.sparta.diameter._3gpp.cxdx.mixins.HasScscfRestorationInfoAVP;
import com.sipgate.sparta.diameter._3gpp.cxdx.mixins.HasServerAssignmentTypeAVP;
import com.sipgate.sparta.diameter._3gpp.cxdx.mixins.HasServerNameAVP;
import com.sipgate.sparta.diameter._3gpp.cxdx.mixins.HasSessionPriorityAVP;
import com.sipgate.sparta.diameter._3gpp.cxdx.mixins.HasUserDataAlreadyAvailableAVP;
import com.sipgate.sparta.diameter._3gpp.cxdx.mixins.HasWildcardedPublicIdentityAVP;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.IncomingRequest;
import com.sipgate.sparta.diameter.base.core.OutgoingRequest;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasProxyInfoAVPs;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasRouteRecordAVPs;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasUserNameAVP;
import com.sipgate.sparta.diameter.ietf.doic.mixins.HasOcSupportedFeaturesAVP;
import com.sipgate.sparta.diameter.ietf.drmp.mixins.HasDrmpAVP;

/** Server-Assignment-Request (SAR) — 3GPP TS 29.229 §6.1.3. */
public interface ServerAssignmentRequest
        extends _3gppRequest,
                HasDrmpAVP, HasUserNameAVP, HasOcSupportedFeaturesAVP,
                HasSupportedFeaturesAVPs, HasPublicIdentityAVPs, HasWildcardedPublicIdentityAVP,
                HasServerNameAVP, HasServerAssignmentTypeAVP, HasUserDataAlreadyAvailableAVP,
                HasScscfRestorationInfoAVP, HasMultipleRegistrationIndicationAVP, HasSessionPriorityAVP,
                HasSarFlagsAVP, HasFailedPcscfAVP,
                HasProxyInfoAVPs, HasRouteRecordAVPs {

    final class In extends IncomingRequest<ServerAssignmentAnswer.Out> implements ServerAssignmentRequest {
        In(final HopByHopId hopByHop, final EndToEndId endToEnd, final boolean retransmitted) {
            super(CxDxConstants.CMD_SERVER_ASSIGNMENT, true, retransmitted, CxDxConstants.APP_ID_CX_DX, hopByHop, endToEnd);
        }

        @Override
        public String getCommandName() {
            return "Server-Assignment Request";
        }
    }

    final class Out extends OutgoingRequest<ServerAssignmentAnswer.In> implements ServerAssignmentRequest {
        public Out() {
            super(CxDxConstants.CMD_SERVER_ASSIGNMENT, true, CxDxConstants.APP_ID_CX_DX);
        }

        @Override
        public String getCommandName() {
            return "Server-Assignment Request";
        }
    }
}
