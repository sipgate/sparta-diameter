package com.sipgate.sparta.diameter._3gpp.gx.messages;

import com.sipgate.sparta.diameter._3gpp.gx.GxConstants;
import com.sipgate.sparta.diameter._3gpp.gx.mixins.HasBearerControlModeAVP;
import com.sipgate.sparta.diameter._3gpp.gx.mixins.HasChargingRuleInstallAVPs;
import com.sipgate.sparta.diameter._3gpp.gx.mixins.HasChargingRuleRemoveAVPs;
import com.sipgate.sparta.diameter._3gpp.gx.mixins.HasDefaultEpsBearerQosAVP;
import com.sipgate.sparta.diameter._3gpp.gx.mixins.HasEventTriggerAVPs;
import com.sipgate.sparta.diameter._3gpp.gx.mixins.HasQosInformationAVP;
import com.sipgate.sparta.diameter._3gpp.gx.mixins.HasRevalidationTimeAVP;
import com.sipgate.sparta.diameter.base.core.DiameterAnswer;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.IncomingAnswer;
import com.sipgate.sparta.diameter.base.core.OutgoingAnswer;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasAuthApplicationIdAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasErrorReportingHostAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasExperimentalResultAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasOriginStateIdAVP;
import com.sipgate.sparta.diameter.ietf.cc.CcConstants;
import com.sipgate.sparta.diameter.ietf.cc.mixins.HasCcRequestNumberAVP;
import com.sipgate.sparta.diameter.ietf.cc.mixins.HasCcRequestTypeAVP;

/**
 * Gx Credit-Control-Answer (CCA) — 3GPP TS 29.212 §5.6.3.
 */
public interface GxCreditControlAnswer
        extends DiameterAnswer,
                HasAuthApplicationIdAVP, HasCcRequestTypeAVP, HasCcRequestNumberAVP,
                HasExperimentalResultAVP, HasOriginStateIdAVP, HasErrorReportingHostAVP,
                HasEventTriggerAVPs, HasBearerControlModeAVP,
                HasChargingRuleRemoveAVPs, HasChargingRuleInstallAVPs,
                HasDefaultEpsBearerQosAVP, HasQosInformationAVP, HasRevalidationTimeAVP {

    final class In extends IncomingAnswer
            implements GxCreditControlAnswer {

        In(final HopByHopId hopByHop, final EndToEndId endToEnd) {
            super(CcConstants.CMD_CREDIT_CONTROL, true, GxConstants.APP_ID_GX, hopByHop, endToEnd);
        }

        @Override
        public String getCommandName() {
            return "Gx Credit-Control Answer";
        }
    }

    final class Out extends OutgoingAnswer
            implements GxCreditControlAnswer {

        Out(final HopByHopId hopByHop, final EndToEndId endToEnd) {
            super(CcConstants.CMD_CREDIT_CONTROL, true, GxConstants.APP_ID_GX, hopByHop, endToEnd);
        }

        @Override
        public String getCommandName() {
            return "Gx Credit-Control Answer";
        }
    }
}
