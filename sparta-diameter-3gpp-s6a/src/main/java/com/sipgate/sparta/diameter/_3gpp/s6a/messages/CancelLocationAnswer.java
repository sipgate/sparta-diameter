package com.sipgate.sparta.diameter._3gpp.s6a.messages;

import com.sipgate.sparta.diameter._3gpp.common._3gppAnswer;
import com.sipgate.sparta.diameter._3gpp.s6a.S6aConstants;
import com.sipgate.sparta.diameter._3gpp.common.mixins.HasSupportedFeaturesAVPs;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasRouteRecordAVPs;
import com.sipgate.sparta.diameter.ietf.drmp.mixins.HasDrmpAVP;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.IncomingAnswer;
import com.sipgate.sparta.diameter.base.core.OutgoingAnswer;

/** Cancel-Location-Request/Answer (Answer) (CLA) — 3GPP TS 29.272 §7.2.7-8. */
public interface CancelLocationAnswer
        extends _3gppAnswer,
                HasDrmpAVP, HasSupportedFeaturesAVPs, HasRouteRecordAVPs {

    final class In extends IncomingAnswer implements CancelLocationAnswer {
        In(final HopByHopId hopByHop, final EndToEndId endToEnd) {
            super(S6aConstants.CMD_CANCEL_LOCATION, true, S6aConstants.APP_ID_S6A_S6D, hopByHop, endToEnd);
        }

        @Override
        public String getCommandName() {
            return "Cancel-Location Answer";
        }
    }

    final class Out extends OutgoingAnswer implements CancelLocationAnswer {
        Out(final HopByHopId hopByHop, final EndToEndId endToEnd) {
            super(S6aConstants.CMD_CANCEL_LOCATION, true, S6aConstants.APP_ID_S6A_S6D, hopByHop, endToEnd);
        }

        @Override
        public String getCommandName() {
            return "Cancel-Location Answer";
        }
    }
}
