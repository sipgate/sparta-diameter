package com.sipgate.sparta.diameter.ietf.cc.messages;

import com.sipgate.sparta.diameter.base.core.DiameterAnswer;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.IncomingAnswer;
import com.sipgate.sparta.diameter.base.core.OutgoingAnswer;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasAcctMultiSessionIdAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasAuthApplicationIdAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasEventTimestampAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasOriginStateIdAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasRedirectHostAVPs;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasRedirectHostUsageAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasRedirectMaxCacheTimeAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasRouteRecordAVPs;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasUserNameAVP;
import com.sipgate.sparta.diameter.ietf.cc.CcConstants;
import com.sipgate.sparta.diameter.ietf.cc.mixins.HasCcRequestNumberAVP;
import com.sipgate.sparta.diameter.ietf.cc.mixins.HasCcRequestTypeAVP;
import com.sipgate.sparta.diameter.ietf.cc.mixins.HasCcSessionFailoverAVP;
import com.sipgate.sparta.diameter.ietf.cc.mixins.HasCcSubSessionIdAVP;
import com.sipgate.sparta.diameter.ietf.cc.mixins.HasCheckBalanceResultAVP;
import com.sipgate.sparta.diameter.ietf.cc.mixins.HasCostInformationAVP;
import com.sipgate.sparta.diameter.ietf.cc.mixins.HasCreditControlFailureHandlingAVP;
import com.sipgate.sparta.diameter.ietf.cc.mixins.HasDirectDebitingFailureHandlingAVP;
import com.sipgate.sparta.diameter.ietf.cc.mixins.HasFinalUnitIndicationAVP;
import com.sipgate.sparta.diameter.ietf.cc.mixins.HasGrantedServiceUnitAVP;
import com.sipgate.sparta.diameter.ietf.cc.mixins.HasMultipleServicesCreditControlAVPs;
import com.sipgate.sparta.diameter.ietf.cc.mixins.HasValidityTimeAVP;

/**
 * Credit-Control-Answer (CCA) — RFC 4006 §3.2.
 */
public interface CreditControlAnswer
        extends DiameterAnswer,
                HasAuthApplicationIdAVP, HasCcRequestTypeAVP, HasCcRequestNumberAVP,
                HasUserNameAVP, HasCcSessionFailoverAVP, HasCcSubSessionIdAVP,
                HasAcctMultiSessionIdAVP, HasOriginStateIdAVP, HasEventTimestampAVP,
                HasGrantedServiceUnitAVP, HasMultipleServicesCreditControlAVPs,
                HasCostInformationAVP, HasFinalUnitIndicationAVP, HasCheckBalanceResultAVP,
                HasCreditControlFailureHandlingAVP, HasDirectDebitingFailureHandlingAVP,
                HasValidityTimeAVP,
                HasRedirectHostAVPs, HasRedirectHostUsageAVP, HasRedirectMaxCacheTimeAVP,
                HasRouteRecordAVPs {

    final class In extends IncomingAnswer
            implements CreditControlAnswer {

        In(final HopByHopId hopByHop, final EndToEndId endToEnd) {
            super(CcConstants.CMD_CREDIT_CONTROL, true, CcConstants.APP_ID_CREDIT_CONTROL,
                hopByHop, endToEnd);
        }

        @Override
        public String getCommandName() {
            return "Credit-Control Answer";
        }
    }

    final class Out extends OutgoingAnswer
            implements CreditControlAnswer {

        Out(final HopByHopId hopByHop, final EndToEndId endToEnd) {
            super(CcConstants.CMD_CREDIT_CONTROL, true, CcConstants.APP_ID_CREDIT_CONTROL,
                hopByHop, endToEnd);
        }

        @Override
        public String getCommandName() {
            return "Credit-Control Answer";
        }
    }
}
