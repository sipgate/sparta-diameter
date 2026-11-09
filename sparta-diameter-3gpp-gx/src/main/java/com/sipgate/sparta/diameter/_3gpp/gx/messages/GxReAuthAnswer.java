package com.sipgate.sparta.diameter._3gpp.gx.messages;

import com.sipgate.sparta.diameter._3gpp.gx.GxConstants;
import com.sipgate.sparta.diameter._3gpp.gx.mixins.HasChargingRuleReportAVPs;
import com.sipgate.sparta.diameter.base.core.DiameterAnswer;
import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.IncomingAnswer;
import com.sipgate.sparta.diameter.base.core.OutgoingAnswer;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasErrorReportingHostAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasExperimentalResultAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasOriginHostAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasOriginRealmAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasOriginStateIdAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasResultCodeAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasSessionIdAVP;

/**
 * Gx Re-Auth-Answer (RAA) — 3GPP TS 29.212 §5.6.5, with the Gx application id in the header. As for
 * {@link GxReAuthRequest}, the base factory parses and creates the RAA on the wire; this type is for tests and tools
 * that build one directly.
 */
public interface GxReAuthAnswer
        extends DiameterAnswer,
                HasSessionIdAVP, HasOriginHostAVP, HasOriginRealmAVP, HasResultCodeAVP,
                HasExperimentalResultAVP, HasOriginStateIdAVP, HasErrorReportingHostAVP,
                HasChargingRuleReportAVPs {

    final class In extends IncomingAnswer
            implements GxReAuthAnswer {

        In(final HopByHopId hopByHop, final EndToEndId endToEnd) {
            super(DiameterConstants.CMD_RE_AUTH, true, GxConstants.APP_ID_GX, hopByHop, endToEnd);
        }

        @Override
        public String getCommandName() {
            return "Gx Re-Auth Answer";
        }
    }

    final class Out extends OutgoingAnswer
            implements GxReAuthAnswer {

        Out(final HopByHopId hopByHop, final EndToEndId endToEnd) {
            super(DiameterConstants.CMD_RE_AUTH, true, GxConstants.APP_ID_GX, hopByHop, endToEnd);
        }

        @Override
        public String getCommandName() {
            return "Gx Re-Auth Answer";
        }
    }
}
