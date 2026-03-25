package com.sipgate.sparta.diameter.session;

import com.sipgate.sparta.diameter.core.EndToEndId;
import com.sipgate.sparta.diameter.core.HopByHopId;

import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Generates Diameter message identifiers per RFC 6733 §3.
 *
 * <p>One instance per session. Hop-by-hop identifiers are issued from an
 * {@link AtomicInteger} counter seeded randomly at construction time,
 * guaranteeing uniqueness on a given connection (RFC 6733 §3: "MUST be
 * unique on a given connection").
 *
 * <p>End-to-end identifiers are computed statically: the high-order 12 bits
 * carry the low-order 12 bits of the current NTP timestamp (seconds since
 * 1900-01-01), and the low-order 20 bits are random (RFC 6733 §3: "SHOULD
 * set the high-order 12 bits … and the low-order 20 bits to a random value").
 */
final class DiameterIdentifiers {

    private static final long NTP_OFFSET_SECONDS = 2208988800L;

    private final AtomicInteger hopByHop;

    DiameterIdentifiers() {
        hopByHop = new AtomicInteger(ThreadLocalRandom.current().nextInt());
    }

    HopByHopId nextHopByHop() {
        return new HopByHopId(hopByHop.getAndIncrement());
    }

    static EndToEndId nextEndToEnd() {
        final long ntpSeconds = Instant.now().getEpochSecond() + NTP_OFFSET_SECONDS;
        final int timePart = (int) (ntpSeconds & 0xFFFL) << 20;
        final int randPart = ThreadLocalRandom.current().nextInt() & 0xFFFFF;
        return new EndToEndId(timePart | randPart);
    }
}
