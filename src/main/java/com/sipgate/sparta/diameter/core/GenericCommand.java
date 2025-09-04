package com.sipgate.sparta.diameter.core;

/**
 * Generic command implementation for unknown or unsupported command codes.
 * <p>
 * This class represents a generic Diameter command as defined in
 * <a href="https://datatracker.ietf.org/doc/html/rfc6733#section-3">RFC 6733, Section 3</a>.
 * It is used by the parser when encountering command codes that do not have specific implementations.
 * </p>
 */
@SuppressWarnings("rawtypes")
public class GenericCommand extends Command {

    /**
     * Constructs a GenericCommand with the specified parameters.
     *
     * @param commandCode        The command code of the message.
     * @param request            Indicates whether the message is a request.
     * @param proxiable          Indicates whether the message is proxiable.
     * @param error              Indicates whether the message is an error.
     * @param retransmitted      Indicates whether the message is retransmitted.
     * @param applicationId      The application ID of the message.
     * @param hopByHopIdentifier The hop-by-hop identifier.
     * @param endToEndIdentifier The end-to-end identifier.
     */
    public GenericCommand(final int commandCode, final boolean request, final boolean proxiable,
                         final boolean error, final boolean retransmitted, final int applicationId,
                         final int hopByHopIdentifier, final int endToEndIdentifier) {
        super(commandCode, request, proxiable, error, retransmitted, applicationId,
              hopByHopIdentifier, endToEndIdentifier);
    }
}
