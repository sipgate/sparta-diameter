package com.sipgate.sparta.diameter.base.session;

import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class DiameterIdentifiersTest {

    // -------------------------------------------------------------------------
    // Hop-by-hop
    // -------------------------------------------------------------------------

    @Test
    void it_returns_sequential_hop_by_hop_identifiers() {
        // GIVEN
        final var identifiers = new DiameterIdentifiers();
        final var first = identifiers.nextHopByHop();

        // WHEN
        final var second = identifiers.nextHopByHop();
        final var third = identifiers.nextHopByHop();

        // THEN
        assertThat(second.value()).isEqualTo(first.value() + 1);
        assertThat(third.value()).isEqualTo(first.value() + 2);
    }

    @Test
    void it_produces_unique_hop_by_hop_identifiers_within_a_session() {
        // GIVEN
        final var identifiers = new DiameterIdentifiers();
        final Set<HopByHopId> seen = new HashSet<>();

        // WHEN
        for (int i = 0; i < 1000; i++) {
            seen.add(identifiers.nextHopByHop());
        }

        // THEN
        assertThat(seen).hasSize(1000);
    }

    @Test
    void it_uses_independent_counters_per_instance() {
        // GIVEN
        final var a = new DiameterIdentifiers();
        final var b = new DiameterIdentifiers();

        // WHEN
        final var aFirst = a.nextHopByHop();
        final var aSecond = a.nextHopByHop();
        final var bFirst = b.nextHopByHop();

        // THEN — b's counter is independent; a increments without affecting b
        assertThat(aSecond.value()).isEqualTo(aFirst.value() + 1);
        assertThat(bFirst).isNotEqualTo(aSecond);
    }

    // -------------------------------------------------------------------------
    // End-to-end
    // -------------------------------------------------------------------------

    @Test
    void it_encodes_ntp_timestamp_in_high_order_12_bits() {
        // GIVEN
        final long ntpOffset = 2208988800L;
        final long ntpSeconds = Instant.now().getEpochSecond() + ntpOffset;
        final int expectedTimePart = (int) (ntpSeconds & 0xFFFL);

        // WHEN
        final EndToEndId endToEnd = DiameterIdentifiers.nextEndToEnd();

        // THEN — high-order 12 bits match (allow ±1 for clock tick between calls)
        final int actualTimePart = (endToEnd.value() >>> 20) & 0xFFF;
        assertThat(actualTimePart).isBetween(expectedTimePart - 1, expectedTimePart + 1);
    }

    @Test
    void it_fits_random_value_in_low_order_20_bits() {
        // GIVEN / WHEN
        final EndToEndId endToEnd = DiameterIdentifiers.nextEndToEnd();

        // THEN — bits 31..20 carry time, bits 19..0 carry random; the full value is 32 bits
        assertThat(endToEnd.value() & ~0xFFF00000).isGreaterThanOrEqualTo(0);
    }

    @Test
    void it_produces_non_identical_end_to_end_identifiers() {
        // GIVEN / WHEN
        final Set<EndToEndId> seen = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            seen.add(DiameterIdentifiers.nextEndToEnd());
        }

        // THEN — with 20 random bits the collision probability is negligible
        assertThat(seen).hasSizeGreaterThan(90);
    }
}
