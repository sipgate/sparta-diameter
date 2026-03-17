package com.sipgate.sparta.diameter.session;

/**
 * Watchdog (liveness) states per RFC 3539 Appendix A.
 * <p>
 * Orthogonal to {@link PeerState} — tracks whether the peer is considered
 * reachable, independent of where the connection state machine currently sits.
 * </p>
 */
public enum WatchdogState {
    INITIAL,
    OKAY,
    SUSPECT,
    DOWN,
    REOPEN
}
