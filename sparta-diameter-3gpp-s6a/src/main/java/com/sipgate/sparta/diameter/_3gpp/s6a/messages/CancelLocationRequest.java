package com.sipgate.sparta.diameter._3gpp.s6a.messages;

import com.sipgate.sparta.diameter._3gpp.common._3gppRequest;
import com.sipgate.sparta.diameter._3gpp.s6a.S6aConstants;
import com.sipgate.sparta.diameter._3gpp.common.mixins.HasSupportedFeaturesAVPs;
import com.sipgate.sparta.diameter._3gpp.s6a.mixins.HasCancellationTypeAVP;
import com.sipgate.sparta.diameter._3gpp.s6a.mixins.HasClrFlagsAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasProxyInfoAVPs;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasRouteRecordAVPs;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasUserNameAVP;
import com.sipgate.sparta.diameter.ietf.drmp.mixins.HasDrmpAVP;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.IncomingRequest;
import com.sipgate.sparta.diameter.base.core.OutgoingRequest;

/** Cancel-Location-Request/Answer (Request) (CLR) — 3GPP TS 29.272 §7.2.7-8. */
public interface CancelLocationRequest
        extends _3gppRequest,
                HasDrmpAVP, HasUserNameAVP, HasSupportedFeaturesAVPs, HasCancellationTypeAVP, HasClrFlagsAVP, HasProxyInfoAVPs, HasRouteRecordAVPs {

    final class In extends IncomingRequest<CancelLocationAnswer.Out> implements CancelLocationRequest {
        In(final HopByHopId hopByHop, final EndToEndId endToEnd, final boolean retransmitted) {
            super(S6aConstants.CMD_CANCEL_LOCATION, true, retransmitted, S6aConstants.APP_ID_S6A_S6D, hopByHop, endToEnd);
        }

        @Override
        public String getCommandName() {
            return "Cancel-Location Request";
        }
    }

    final class Out extends OutgoingRequest<CancelLocationAnswer.In> implements CancelLocationRequest {
        public Out() {
            super(S6aConstants.CMD_CANCEL_LOCATION, true, S6aConstants.APP_ID_S6A_S6D);
        }

        @Override
        public String getCommandName() {
            return "Cancel-Location Request";
        }
    }
}
