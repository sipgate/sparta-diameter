package com.sipgate.sparta.diameter._3gpp.cxdx;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;
import com.sipgate.sparta.diameter.etsi.e2.E2Constants;
import com.sipgate.sparta.diameter.ietf.diameternas.DiameterNasConstants;
import com.sipgate.sparta.diameter.ietf.radiusdigestauthentication.RadiusDigestAuthenticationConstants;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class CxDxGroupedAvpRoundTripTest {

    private static final int V = _3gppConstants.VENDOR_ID_3GPP;

    @Test
    void it_round_trips_a_sip_auth_data_item_nesting_avps_from_all_four_modules() throws Exception {
        // GIVEN a SIP-Auth-Data-Item (cxdx) nesting: a SIP-Digest-Authenticate (cxdx grouped)
        //       holding Digest-Realm (RFC 5090, vendor 0) + Alternate-Digest-Algorithm (cxdx);
        //       a Framed-Interface-Id (RFC 7155, vendor 0, Unsigned64); a Line-Identifier (ETSI 13019)
        final List<AVP> digest = List.of(
            AVP.create(new AVPKey(RadiusDigestAuthenticationConstants.AVP_DIGEST_REALM, 0), "ims.example.org"),
            AVP.create(new AVPKey(CxDxConstants.AVP_ALTERNATE_DIGEST_ALGORITHM, V), "SHA-256"));
        final List<AVP> sipAuthDataItem = List.of(
            AVP.create(new AVPKey(CxDxConstants.AVP_SIP_AUTHENTICATION_SCHEME, V), "Digest-AKAv1-MD5"),
            AVP.create(new AVPKey(CxDxConstants.AVP_SIP_DIGEST_AUTHENTICATE, V), digest),
            AVP.create(new AVPKey(DiameterNasConstants.AVP_FRAMED_INTERFACE_ID, 0), BigInteger.valueOf(0x0102030405060708L)),
            AVP.create(new AVPKey(E2Constants.AVP_LINE_IDENTIFIER, E2Constants.VENDOR_ID_ETSI),
                       "noc=deTGV;lac=00ab".getBytes(StandardCharsets.US_ASCII)));
        final AVP top = AVP.create(new AVPKey(CxDxConstants.AVP_SIP_AUTH_DATA_ITEM, V), sipAuthDataItem);

        // WHEN it is serialized and parsed back via the public AVP encode/decode path
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        top.writeTo(new DataOutputStream(baos));
        final AVP[] parsed = new AVP[1];
        assertThatCode(() -> parsed[0] = AVP.readFrom(ByteBuffer.wrap(baos.toByteArray())))
            .doesNotThrowAnyException();

        // THEN every nested AVP resolved (no 5001) and the structure survived
        assertThat(parsed[0]).isInstanceOf(GroupedAVP.class);
        final GroupedAVP item = (GroupedAVP) parsed[0];
        assertThat(item.findAVP(new AVPKey(CxDxConstants.AVP_SIP_DIGEST_AUTHENTICATE, V)))
            .isInstanceOf(GroupedAVP.class);
        assertThat(item.findAVP(new AVPKey(DiameterNasConstants.AVP_FRAMED_INTERFACE_ID, 0)).getDataAsUnsignedLong())
            .isEqualTo(BigInteger.valueOf(0x0102030405060708L));
        assertThat(item.findAVP(new AVPKey(E2Constants.AVP_LINE_IDENTIFIER, E2Constants.VENDOR_ID_ETSI)))
            .isNotNull();
        final GroupedAVP digestOut = (GroupedAVP) item.findAVP(new AVPKey(CxDxConstants.AVP_SIP_DIGEST_AUTHENTICATE, V));
        assertThat(digestOut.findAVP(new AVPKey(RadiusDigestAuthenticationConstants.AVP_DIGEST_REALM, 0)).getDataAsString())
            .isEqualTo("ims.example.org");
    }
}
