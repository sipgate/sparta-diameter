package com.sipgate.sparta.diameter._3gpp.common;

import com.sipgate.sparta.diameter.base.core.avp.mixins.HasAuthSessionStateAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasSessionIdAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasSupportedVendorIdAVPs;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasVendorSpecificApplicationIdAVP;

public interface _3gppRequest extends HasSessionIdAVP, HasVendorSpecificApplicationIdAVP, HasAuthSessionStateAVP, HasSupportedVendorIdAVPs {
}
