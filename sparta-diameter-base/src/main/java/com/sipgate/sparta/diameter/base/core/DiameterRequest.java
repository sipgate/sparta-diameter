package com.sipgate.sparta.diameter.base.core;

import com.sipgate.sparta.diameter.base.core.avp.mixins.HasProxyInfoAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasProxyInfoAVPs;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasRouteRecordAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasRouteRecordAVPs;

public interface DiameterRequest extends HasProxyInfoAVPs, HasRouteRecordAVPs {
}
