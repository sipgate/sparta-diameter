package com.sipgate.sparta.diameter._3gpp.s6a.messages;

import com.sipgate.sparta.diameter._3gpp.common._3gppAnswer;
import com.sipgate.sparta.diameter._3gpp.s6a.S6aConstants;
import com.sipgate.sparta.diameter._3gpp.common.mixins.HasSupportedFeaturesAVPs;
import com.sipgate.sparta.diameter._3gpp.s6a.mixins.HasAuthenticationInfoAVP;
import com.sipgate.sparta.diameter._3gpp.s6a.mixins.HasErrorDiagnosticAVP;
import com.sipgate.sparta.diameter._3gpp.s6a.mixins.HasUeUsageTypeAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasRouteRecordAVPs;
import com.sipgate.sparta.diameter.ietf.doic.mixins.HasOcOlrAVP;
import com.sipgate.sparta.diameter.ietf.doic.mixins.HasOcSupportedFeaturesAVP;
import com.sipgate.sparta.diameter.ietf.drmp.mixins.HasDrmpAVP;
import com.sipgate.sparta.diameter.ietf.load.mixins.HasLoadAVPs;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.IncomingAnswer;
import com.sipgate.sparta.diameter.base.core.OutgoingAnswer;

/** Authentication-Information-Request/Answer (Answer) (AIA) — 3GPP TS 29.272 §7.2.5-6. */
public interface AuthenticationInformationAnswer
        extends _3gppAnswer,
                HasDrmpAVP, HasErrorDiagnosticAVP, HasOcSupportedFeaturesAVP, HasOcOlrAVP, HasLoadAVPs, HasSupportedFeaturesAVPs, HasAuthenticationInfoAVP, HasUeUsageTypeAVP, HasRouteRecordAVPs {

    final class In extends IncomingAnswer implements AuthenticationInformationAnswer {
        In(final HopByHopId hopByHop, final EndToEndId endToEnd) {
            super(S6aConstants.CMD_AUTHENTICATION_INFORMATION, true, S6aConstants.APP_ID_S6A_S6D, hopByHop, endToEnd);
        }

        @Override
        public String getCommandName() {
            return "Authentication-Information Answer";
        }
    }

    final class Out extends OutgoingAnswer implements AuthenticationInformationAnswer {
        Out(final HopByHopId hopByHop, final EndToEndId endToEnd) {
            super(S6aConstants.CMD_AUTHENTICATION_INFORMATION, true, S6aConstants.APP_ID_S6A_S6D, hopByHop, endToEnd);
        }

        @Override
        public String getCommandName() {
            return "Authentication-Information Answer";
        }
    }
}
