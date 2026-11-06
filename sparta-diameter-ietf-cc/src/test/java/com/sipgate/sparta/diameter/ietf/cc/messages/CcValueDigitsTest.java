package com.sipgate.sparta.diameter.ietf.cc.messages;

import com.sipgate.sparta.diameter.base.core.Command;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.ietf.cc.CcAvps;
import com.sipgate.sparta.diameter.ietf.cc.CcConstants;
import com.sipgate.sparta.diameter.ietf.cc.mixins.HasUnitValueAVP;
import com.sipgate.sparta.diameter.ietf.cc.mixins.HasValueDigitsAVP;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CcValueDigitsTest {

    private static final long NEGATIVE_BEYOND_INT_RANGE = -5_000_000_000L;

    @Test
    void it_encodes_value_digits_as_eight_bytes() {
        // GIVEN/WHEN
        final var valueDigits = CcAvps.valueDigits(-12345L);

        // THEN
        assertThat(valueDigits.getData()).hasSize(8);
        assertThat(valueDigits.getDataAsLong()).isEqualTo(-12345L);
        assertThat(valueDigits.isMandatory()).isTrue();
        assertThat(valueDigits.toString()).contains("-12345");
    }

    @Test
    void it_sets_and_gets_value_digits_through_the_mixin() {
        // GIVEN
        final var container = new TestContainer();

        // WHEN
        container.setValueDigits(NEGATIVE_BEYOND_INT_RANGE);

        // THEN
        assertThat(container.getValueDigits()).isEqualTo(NEGATIVE_BEYOND_INT_RANGE);
        assertThat(container.findAVP(new AVPKey(CcConstants.AVP_VALUE_DIGITS, 0)).getData()).hasSize(8);
    }

    @Test
    void it_builds_a_unit_value_from_digits_and_exponent() {
        // GIVEN
        final var container = new TestContainer();

        // WHEN
        container.setUnitValue(NEGATIVE_BEYOND_INT_RANGE, -2);

        // THEN
        final var unitValue = container.getUnitValue();
        assertThat(unitValue.findAVP(new AVPKey(CcConstants.AVP_VALUE_DIGITS, 0)).getDataAsLong())
            .isEqualTo(NEGATIVE_BEYOND_INT_RANGE);
        assertThat(unitValue.findAVP(new AVPKey(CcConstants.AVP_EXPONENT, 0)).getDataAsInt()).isEqualTo(-2);
    }

    @Test
    void it_round_trips_a_negative_unit_value_in_cost_information() throws Exception {
        // GIVEN
        final var answer = (CreditControlAnswer.Out) new CcMessageFactory().createAnswer(
            CcConstants.CMD_CREDIT_CONTROL, CcConstants.APP_ID_CREDIT_CONTROL,
            new HopByHopId(1), new EndToEndId(2));
        answer.setCostInformation(List.of(
            AVP.create(new AVPKey(CcConstants.AVP_UNIT_VALUE, 0), List.of(
                CcAvps.valueDigits(NEGATIVE_BEYOND_INT_RANGE),
                AVP.create(new AVPKey(CcConstants.AVP_EXPONENT, 0), -2)))));
        final var bytes = new ByteArrayOutputStream();
        answer.writeTo(new DataOutputStream(bytes));

        // WHEN
        final var parsed = (CreditControlAnswer.In) Command.parseMessage(ByteBuffer.wrap(bytes.toByteArray()));

        // THEN
        final var unitValue = (AVPContainer) parsed.getCostInformation()
            .findAVP(new AVPKey(CcConstants.AVP_UNIT_VALUE, 0));
        assertThat(unitValue.findAVP(new AVPKey(CcConstants.AVP_VALUE_DIGITS, 0)).getDataAsLong())
            .isEqualTo(NEGATIVE_BEYOND_INT_RANGE);
        assertThat(unitValue.findAVP(new AVPKey(CcConstants.AVP_EXPONENT, 0)).getDataAsInt()).isEqualTo(-2);
    }

    private static final class TestContainer implements HasValueDigitsAVP, HasUnitValueAVP {

        private final List<AVP> avps = new ArrayList<>();

        @Override
        public void addAVP(final AVP avp) {
            avps.add(avp);
        }

        @Override
        public void setAVP(final AVP avp) {
            avps.removeIf(existing -> existing.isSameKey(avp));
            avps.add(avp);
        }

        @Override
        public AVP findAVP(final AVPKey key) {
            for (final AVP avp : avps) {
                if (avp.isSameKey(key)) {
                    return avp;
                }
            }
            return null;
        }

        @Override
        public List<AVP> findAVPs(final AVPKey key) {
            final List<AVP> matching = new ArrayList<>();
            for (final AVP avp : avps) {
                if (avp.isSameKey(key)) {
                    matching.add(avp);
                }
            }
            return matching;
        }
    }
}
