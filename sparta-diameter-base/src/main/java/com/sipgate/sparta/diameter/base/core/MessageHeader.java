package com.sipgate.sparta.diameter.base.core;

import java.io.Serializable;

/**
 * Header fields of a received message, carried by parse errors to build the error answer.
 *
 * @param request       whether the R-bit was set
 * @param commandCode   the command code
 * @param proxiable     whether the P-bit was set
 * @param applicationId the application ID
 * @param hopByHop      the hop-by-hop identifier
 * @param endToEnd      the end-to-end identifier
 */
public record MessageHeader(
        boolean request,
        int commandCode,
        boolean proxiable,
        int applicationId,
        HopByHopId hopByHop,
        EndToEndId endToEnd) implements Serializable {
}
