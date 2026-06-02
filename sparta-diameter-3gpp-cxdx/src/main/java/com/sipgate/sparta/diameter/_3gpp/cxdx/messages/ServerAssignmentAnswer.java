package com.sipgate.sparta.diameter._3gpp.cxdx.messages;

import com.sipgate.sparta.diameter._3gpp.common._3gppAnswer;
import com.sipgate.sparta.diameter._3gpp.cxdx.CxDxConstants;
import com.sipgate.sparta.diameter._3gpp.cxdx.mixins.HasAllowedWafWwsfIdentitiesAVP;
import com.sipgate.sparta.diameter._3gpp.cxdx.mixins.HasAssociatedIdentitiesAVP;
import com.sipgate.sparta.diameter._3gpp.cxdx.mixins.HasAssociatedRegisteredIdentitiesAVP;
import com.sipgate.sparta.diameter._3gpp.cxdx.mixins.HasChargingInformationAVP;
import com.sipgate.sparta.diameter._3gpp.cxdx.mixins.HasLooseRouteIndicationAVP;
import com.sipgate.sparta.diameter._3gpp.cxdx.mixins.HasPriviledgedSenderIndicationAVP;
import com.sipgate.sparta.diameter._3gpp.cxdx.mixins.HasScscfRestorationInfoAVPs;
import com.sipgate.sparta.diameter._3gpp.cxdx.mixins.HasServerNameAVP;
import com.sipgate.sparta.diameter._3gpp.cxdx.mixins.HasUserDataAVP;
import com.sipgate.sparta.diameter._3gpp.cxdx.mixins.HasWildcardedPublicIdentityAVP;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.IncomingAnswer;
import com.sipgate.sparta.diameter.base.core.OutgoingAnswer;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasRouteRecordAVPs;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasUserNameAVP;
import com.sipgate.sparta.diameter.ietf.doic.mixins.HasOcOlrAVP;
import com.sipgate.sparta.diameter.ietf.doic.mixins.HasOcSupportedFeaturesAVP;
import com.sipgate.sparta.diameter.ietf.drmp.mixins.HasDrmpAVP;
import com.sipgate.sparta.diameter.ietf.load.mixins.HasLoadAVPs;

/** Server-Assignment-Answer (SAA) — 3GPP TS 29.229 §6.1.4. */
public interface ServerAssignmentAnswer
        extends _3gppAnswer,
                HasDrmpAVP, HasUserNameAVP, HasOcSupportedFeaturesAVP, HasOcOlrAVP, HasLoadAVPs,
                HasUserDataAVP, HasChargingInformationAVP,
                HasAssociatedIdentitiesAVP, HasLooseRouteIndicationAVP, HasScscfRestorationInfoAVPs,
                HasAssociatedRegisteredIdentitiesAVP, HasServerNameAVP, HasWildcardedPublicIdentityAVP,
                HasPriviledgedSenderIndicationAVP, HasAllowedWafWwsfIdentitiesAVP,
                HasRouteRecordAVPs {

    final class In extends IncomingAnswer implements ServerAssignmentAnswer {
        In(final HopByHopId hopByHop, final EndToEndId endToEnd) {
            super(CxDxConstants.CMD_SERVER_ASSIGNMENT, true, CxDxConstants.APP_ID_CX_DX, hopByHop, endToEnd);
        }
    }

    final class Out extends OutgoingAnswer implements ServerAssignmentAnswer {
        Out(final HopByHopId hopByHop, final EndToEndId endToEnd) {
            super(CxDxConstants.CMD_SERVER_ASSIGNMENT, true, CxDxConstants.APP_ID_CX_DX, hopByHop, endToEnd);
        }
    }
}
