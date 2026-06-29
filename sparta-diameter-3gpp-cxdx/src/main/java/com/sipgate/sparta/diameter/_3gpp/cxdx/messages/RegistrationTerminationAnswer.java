package com.sipgate.sparta.diameter._3gpp.cxdx.messages;

import com.sipgate.sparta.diameter._3gpp.common._3gppAnswer;
import com.sipgate.sparta.diameter._3gpp.cxdx.CxDxConstants;
import com.sipgate.sparta.diameter._3gpp.cxdx.mixins.HasAssociatedIdentitiesAVP;
import com.sipgate.sparta.diameter._3gpp.cxdx.mixins.HasIdentityWithEmergencyRegistrationAVPs;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.IncomingAnswer;
import com.sipgate.sparta.diameter.base.core.OutgoingAnswer;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasRouteRecordAVPs;
import com.sipgate.sparta.diameter.ietf.drmp.mixins.HasDrmpAVP;

/** Registration-Termination-Answer (RTA) — 3GPP TS 29.229 §6.1.10. */
public interface RegistrationTerminationAnswer
        extends _3gppAnswer,
                HasDrmpAVP, HasAssociatedIdentitiesAVP, HasIdentityWithEmergencyRegistrationAVPs,
                HasRouteRecordAVPs {

    final class In extends IncomingAnswer implements RegistrationTerminationAnswer {
        In(final HopByHopId hopByHop, final EndToEndId endToEnd) {
            super(CxDxConstants.CMD_REGISTRATION_TERMINATION, true, CxDxConstants.APP_ID_CX_DX, hopByHop, endToEnd);
        }

        @Override
        public String getCommandName() {
            return "Registration-Termination Answer";
        }
    }

    final class Out extends OutgoingAnswer implements RegistrationTerminationAnswer {
        Out(final HopByHopId hopByHop, final EndToEndId endToEnd) {
            super(CxDxConstants.CMD_REGISTRATION_TERMINATION, true, CxDxConstants.APP_ID_CX_DX, hopByHop, endToEnd);
        }

        @Override
        public String getCommandName() {
            return "Registration-Termination Answer";
        }
    }
}
