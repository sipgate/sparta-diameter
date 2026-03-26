package com.sipgate.sparta.diameter.base.session;

/**
 * Connection state machine states per RFC 6733 §5.6.
 * <p>
 * The I- prefix (initiator) and R- prefix (responder) from the RFC are encoded
 * as {@link #I_OPEN} and {@link #R_OPEN}. All other states are direction-agnostic.
 * </p>
 */
public enum PeerState {
    CLOSED,
    WAIT_CONN_ACK,
    WAIT_I_CEA,
    WAIT_CONN_ACK_ELECT,
    WAIT_RETURNS,
    I_OPEN,
    R_OPEN,
    CLOSING
}
