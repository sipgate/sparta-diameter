package com.sipgate.sparta.diameter._3gpp.gx.messages;

import com.sipgate.sparta.diameter._3gpp.gx.GxConstants;
import com.sipgate.sparta.diameter._3gpp.gx.mixins.HasAnGwAddressAVP;
import com.sipgate.sparta.diameter._3gpp.gx.mixins.HasAnTrustedAVP;
import com.sipgate.sparta.diameter._3gpp.gx.mixins.HasBearerIdentifierAVP;
import com.sipgate.sparta.diameter._3gpp.gx.mixins.HasBearerOperationAVP;
import com.sipgate.sparta.diameter._3gpp.gx.mixins.HasDefaultEpsBearerQosAVP;
import com.sipgate.sparta.diameter._3gpp.gx.mixins.HasIpCanTypeAVP;
import com.sipgate.sparta.diameter._3gpp.gx.mixins.HasNetworkRequestSupportAVP;
import com.sipgate.sparta.diameter._3gpp.gx.mixins.HasQosInformationAVP;
import com.sipgate.sparta.diameter._3gpp.gx.mixins.HasRatTypeAVP;
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
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasSessionIdAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasTerminationCauseAVP;
import com.sipgate.sparta.diameter.ietf.cc.CcConstants;
import com.sipgate.sparta.diameter.ietf.cc.mixins.HasCcRequestNumberAVP;
import com.sipgate.sparta.diameter.ietf.cc.mixins.HasCcRequestTypeAVP;
import com.sipgate.sparta.diameter.ietf.cc.mixins.HasSubscriptionIdAVPs;
import com.sipgate.sparta.diameter.ietf.cc.mixins.HasUserEquipmentInfoAVP;

/**
 * Gx Credit-Control-Request (CCR) — 3GPP TS 29.212 §5.6.2. It has command code 272 like the RFC 4006 CCR, but belongs
 * to the Gx application (16777238); {@code CreditControlRequest} of {@code sparta-diameter-ietf-cc} is bound to
 * application 4 (Gy).
 */
public interface GxCreditControlRequest
        extends DiameterRequest,
                HasSessionIdAVP, HasAuthApplicationIdAVP,
                HasOriginHostAVP, HasOriginRealmAVP, HasDestinationRealmAVP, HasDestinationHostAVP,
                HasCcRequestTypeAVP, HasCcRequestNumberAVP, HasOriginStateIdAVP,
                HasSubscriptionIdAVPs, HasNetworkRequestSupportAVP,
                HasBearerIdentifierAVP, HasBearerOperationAVP,
                HasIpCanTypeAVP, HasRatTypeAVP, HasTerminationCauseAVP, HasUserEquipmentInfoAVP,
                HasQosInformationAVP, HasDefaultEpsBearerQosAVP, HasAnGwAddressAVP, HasAnTrustedAVP {

    final class In extends IncomingRequest<GxCreditControlAnswer.Out>
            implements GxCreditControlRequest {

        In(final HopByHopId hopByHop, final EndToEndId endToEnd, final boolean retransmitted) {
            super(CcConstants.CMD_CREDIT_CONTROL, true, retransmitted, GxConstants.APP_ID_GX, hopByHop, endToEnd);
        }

        @Override
        public String getCommandName() {
            return "Gx Credit-Control Request";
        }
    }

    final class Out extends OutgoingRequest<GxCreditControlAnswer.In>
            implements GxCreditControlRequest {

        public Out() {
            super(CcConstants.CMD_CREDIT_CONTROL, true, GxConstants.APP_ID_GX);
        }

        @Override
        public String getCommandName() {
            return "Gx Credit-Control Request";
        }
    }
}
