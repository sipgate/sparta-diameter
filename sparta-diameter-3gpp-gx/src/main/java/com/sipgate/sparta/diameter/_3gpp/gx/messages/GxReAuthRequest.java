package com.sipgate.sparta.diameter._3gpp.gx.messages;

import com.sipgate.sparta.diameter._3gpp.gx.GxConstants;
import com.sipgate.sparta.diameter._3gpp.gx.mixins.HasChargingRuleInstallAVPs;
import com.sipgate.sparta.diameter._3gpp.gx.mixins.HasChargingRuleRemoveAVPs;
import com.sipgate.sparta.diameter._3gpp.gx.mixins.HasDefaultEpsBearerQosAVP;
import com.sipgate.sparta.diameter._3gpp.gx.mixins.HasEventTriggerAVPs;
import com.sipgate.sparta.diameter._3gpp.gx.mixins.HasQosInformationAVP;
import com.sipgate.sparta.diameter._3gpp.gx.mixins.HasRevalidationTimeAVP;
import com.sipgate.sparta.diameter._3gpp.gx.mixins.HasSessionReleaseCauseAVP;
import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.DiameterRequest;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.IncomingRequest;
import com.sipgate.sparta.diameter.base.core.OutgoingRequest;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasAuthApplicationIdAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasDestinationHostAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasDestinationRealmAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasOriginHostAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasOriginRealmAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasOriginStateIdAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasReAuthRequestTypeAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasSessionIdAVP;
import com.sipgate.sparta.diameter.base.messages.ReAuthAnswer;

/**
 * Gx Re-Auth-Request (RAR) — 3GPP TS 29.212 §5.6.4. It carries the Gx application id in the header, unlike the base
 * {@code ReAuthRequest}, which is bound to application 0.
 * <p>
 * {@code GxMessageFactory} does not parse it: the base factory handles every RAR and RAA regardless of the
 * application id (RFC 6733 §8.3), so an incoming Gx RAR is a base {@code ReAuthRequest.In} and the answer to
 * {@link Out} is a base {@code ReAuthAnswer.In}. {@link In} exists for tests and tools that build one directly.
 * </p>
 */
public interface GxReAuthRequest
        extends DiameterRequest,
                HasSessionIdAVP, HasAuthApplicationIdAVP,
                HasOriginHostAVP, HasOriginRealmAVP, HasDestinationRealmAVP, HasDestinationHostAVP,
                HasReAuthRequestTypeAVP, HasSessionReleaseCauseAVP, HasOriginStateIdAVP,
                HasEventTriggerAVPs, HasChargingRuleRemoveAVPs, HasChargingRuleInstallAVPs,
                HasDefaultEpsBearerQosAVP, HasQosInformationAVP, HasRevalidationTimeAVP {

    final class In extends IncomingRequest<GxReAuthAnswer.Out>
            implements GxReAuthRequest {

        In(final HopByHopId hopByHop, final EndToEndId endToEnd, final boolean retransmitted) {
            super(DiameterConstants.CMD_RE_AUTH, true, retransmitted, GxConstants.APP_ID_GX, hopByHop, endToEnd);
        }

        @Override
        public String getCommandName() {
            return "Gx Re-Auth Request";
        }
    }

    final class Out extends OutgoingRequest<ReAuthAnswer.In>
            implements GxReAuthRequest {

        public Out() {
            super(DiameterConstants.CMD_RE_AUTH, true, GxConstants.APP_ID_GX);
        }

        @Override
        public String getCommandName() {
            return "Gx Re-Auth Request";
        }
    }
}
