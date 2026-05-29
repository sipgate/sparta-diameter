package com.sipgate.sparta.diameter._3gpp.cxdx.messages;

import com.sipgate.sparta.diameter._3gpp.common._3gppRequest;
import com.sipgate.sparta.diameter._3gpp.common.mixins.HasSupportedFeaturesAVPs;
import com.sipgate.sparta.diameter._3gpp.cxdx.CxDxConstants;
import com.sipgate.sparta.diameter._3gpp.cxdx.mixins.HasAssociatedIdentitiesAVP;
import com.sipgate.sparta.diameter._3gpp.cxdx.mixins.HasDeregistrationReasonAVP;
import com.sipgate.sparta.diameter._3gpp.cxdx.mixins.HasPublicIdentityAVPs;
import com.sipgate.sparta.diameter._3gpp.cxdx.mixins.HasRtrFlagsAVP;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.IncomingRequest;
import com.sipgate.sparta.diameter.base.core.OutgoingRequest;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasDestinationRealmAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasProxyInfoAVPs;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasRouteRecordAVPs;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasUserNameAVP;
import com.sipgate.sparta.diameter.ietf.drmp.mixins.HasDrmpAVP;

/** Registration-Termination-Request (RTR) — 3GPP TS 29.229 §6.1.9. */
public interface RegistrationTerminationRequest
        extends _3gppRequest,
                HasDrmpAVP, HasDestinationRealmAVP, HasUserNameAVP, HasAssociatedIdentitiesAVP,
                HasSupportedFeaturesAVPs, HasPublicIdentityAVPs, HasDeregistrationReasonAVP, HasRtrFlagsAVP,
                HasProxyInfoAVPs, HasRouteRecordAVPs {

    final class In extends IncomingRequest<RegistrationTerminationAnswer.Out> implements RegistrationTerminationRequest {
        In(final HopByHopId hopByHop, final EndToEndId endToEnd, final boolean retransmitted) {
            super(CxDxConstants.CMD_REGISTRATION_TERMINATION, true, retransmitted, CxDxConstants.APP_ID_CX_DX, hopByHop, endToEnd);
        }
    }

    final class Out extends OutgoingRequest<RegistrationTerminationAnswer.In> implements RegistrationTerminationRequest {
        public Out() {
            super(CxDxConstants.CMD_REGISTRATION_TERMINATION, true, CxDxConstants.APP_ID_CX_DX);
        }
    }
}
