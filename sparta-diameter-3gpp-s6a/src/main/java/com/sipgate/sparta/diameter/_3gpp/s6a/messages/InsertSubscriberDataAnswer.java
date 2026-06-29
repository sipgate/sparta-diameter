package com.sipgate.sparta.diameter._3gpp.s6a.messages;

import com.sipgate.sparta.diameter._3gpp.common._3gppAnswer;
import com.sipgate.sparta.diameter._3gpp.s6a.S6aConstants;
import com.sipgate.sparta.diameter._3gpp.common.mixins.HasEpsLocationInformationAVP;
import com.sipgate.sparta.diameter._3gpp.common.mixins.HasSupportedFeaturesAVPs;
import com.sipgate.sparta.diameter._3gpp.s6a.mixins.HasEpsUserStateAVP;
import com.sipgate.sparta.diameter._3gpp.s6a.mixins.HasIdaFlagsAVP;
import com.sipgate.sparta.diameter._3gpp.s6a.mixins.HasImsVoiceOverPsSessionsSupportedAVP;
import com.sipgate.sparta.diameter._3gpp.s6a.mixins.HasLastUeActivityTimeAVP;
import com.sipgate.sparta.diameter._3gpp.s6a.mixins.HasLocalTimeZoneAVP;
import com.sipgate.sparta.diameter._3gpp.s6t.mixins.HasMonitoringEventConfigStatusAVPs;
import com.sipgate.sparta.diameter._3gpp.s6t.mixins.HasMonitoringEventReportAVPs;
import com.sipgate.sparta.diameter._3gpp.gx.mixins.HasRatTypeAVP;
import com.sipgate.sparta.diameter._3gpp.s6a.mixins.HasSupportedServicesAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasRouteRecordAVPs;
import com.sipgate.sparta.diameter.ietf.drmp.mixins.HasDrmpAVP;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.IncomingAnswer;
import com.sipgate.sparta.diameter.base.core.OutgoingAnswer;

/** Insert-Subscriber-Data-Request/Answer (Answer) (IDA) — 3GPP TS 29.272 §7.2.9-10. */
public interface InsertSubscriberDataAnswer
        extends _3gppAnswer,
                HasDrmpAVP, HasSupportedFeaturesAVPs, HasImsVoiceOverPsSessionsSupportedAVP, HasLastUeActivityTimeAVP, HasRatTypeAVP, HasIdaFlagsAVP, HasEpsUserStateAVP, HasEpsLocationInformationAVP, HasLocalTimeZoneAVP, HasSupportedServicesAVP, HasMonitoringEventReportAVPs, HasMonitoringEventConfigStatusAVPs, HasRouteRecordAVPs {

    final class In extends IncomingAnswer implements InsertSubscriberDataAnswer {
        In(final HopByHopId hopByHop, final EndToEndId endToEnd) {
            super(S6aConstants.CMD_INSERT_SUBSCRIBER_DATA, true, S6aConstants.APP_ID_S6A_S6D, hopByHop, endToEnd);
        }

        @Override
        public String getCommandName() {
            return "Insert-Subscriber-Data Answer";
        }
    }

    final class Out extends OutgoingAnswer implements InsertSubscriberDataAnswer {
        Out(final HopByHopId hopByHop, final EndToEndId endToEnd) {
            super(S6aConstants.CMD_INSERT_SUBSCRIBER_DATA, true, S6aConstants.APP_ID_S6A_S6D, hopByHop, endToEnd);
        }

        @Override
        public String getCommandName() {
            return "Insert-Subscriber-Data Answer";
        }
    }
}
