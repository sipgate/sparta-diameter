package com.sipgate.sparta.diameter.ietf.cc.messages;

import com.sipgate.sparta.diameter.base.core.DiameterRequest;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.IncomingRequest;
import com.sipgate.sparta.diameter.base.core.OutgoingRequest;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasAcctMultiSessionIdAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasAuthApplicationIdAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasEventTimestampAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasOriginStateIdAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasTerminationCauseAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasUserNameAVP;
import com.sipgate.sparta.diameter.ietf.cc.CcConstants;
import com.sipgate.sparta.diameter.ietf.cc.mixins.HasCcCorrelationIdAVP;
import com.sipgate.sparta.diameter.ietf.cc.mixins.HasCcRequestNumberAVP;
import com.sipgate.sparta.diameter.ietf.cc.mixins.HasCcRequestTypeAVP;
import com.sipgate.sparta.diameter.ietf.cc.mixins.HasCcSubSessionIdAVP;
import com.sipgate.sparta.diameter.ietf.cc.mixins.HasMultipleServicesCreditControlAVPs;
import com.sipgate.sparta.diameter.ietf.cc.mixins.HasMultipleServicesIndicatorAVP;
import com.sipgate.sparta.diameter.ietf.cc.mixins.HasRequestedActionAVP;
import com.sipgate.sparta.diameter.ietf.cc.mixins.HasRequestedServiceUnitAVP;
import com.sipgate.sparta.diameter.ietf.cc.mixins.HasServiceContextIdAVP;
import com.sipgate.sparta.diameter.ietf.cc.mixins.HasServiceIdentifierAVP;
import com.sipgate.sparta.diameter.ietf.cc.mixins.HasServiceParameterInfoAVPs;
import com.sipgate.sparta.diameter.ietf.cc.mixins.HasSubscriptionIdAVPs;
import com.sipgate.sparta.diameter.ietf.cc.mixins.HasUsedServiceUnitAVPs;
import com.sipgate.sparta.diameter.ietf.cc.mixins.HasUserEquipmentInfoAVP;

/**
 * Credit-Control-Request (CCR) — RFC 4006 §3.1.
 */
public interface CreditControlRequest
        extends DiameterRequest,
                HasAuthApplicationIdAVP, HasServiceContextIdAVP,
                HasCcRequestTypeAVP, HasCcRequestNumberAVP,
                HasUserNameAVP, HasCcSubSessionIdAVP, HasAcctMultiSessionIdAVP,
                HasOriginStateIdAVP, HasEventTimestampAVP,
                HasSubscriptionIdAVPs, HasServiceIdentifierAVP, HasTerminationCauseAVP,
                HasRequestedServiceUnitAVP, HasRequestedActionAVP, HasUsedServiceUnitAVPs,
                HasMultipleServicesIndicatorAVP, HasMultipleServicesCreditControlAVPs,
                HasServiceParameterInfoAVPs, HasCcCorrelationIdAVP, HasUserEquipmentInfoAVP {

    final class In extends IncomingRequest<CreditControlAnswer.Out>
            implements CreditControlRequest {

        In(final HopByHopId hopByHop, final EndToEndId endToEnd, final boolean retransmitted) {
            super(CcConstants.CMD_CREDIT_CONTROL, true, retransmitted,
                CcConstants.APP_ID_CREDIT_CONTROL, hopByHop, endToEnd);
        }

        @Override
        public String getCommandName() {
            return "Credit-Control Request";
        }
    }

    final class Out extends OutgoingRequest<CreditControlAnswer.In>
            implements CreditControlRequest {

        public Out() {
            super(CcConstants.CMD_CREDIT_CONTROL, true, CcConstants.APP_ID_CREDIT_CONTROL);
        }

        @Override
        public String getCommandName() {
            return "Credit-Control Request";
        }
    }
}
