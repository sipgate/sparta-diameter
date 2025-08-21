package com.sipgate.sparta.diameter.base;

/**
 * Generic command implementation for unknown or unsupported command codes.
 * Used by the parser when encountering command codes that don't have specific implementations.
 */
public class GenericCommand extends Command {

    public GenericCommand(final int commandCode, final boolean request, final boolean proxiable,
                         final boolean error, final boolean retransmitted, final int applicationId,
                         final int hopByHopIdentifier, final int endToEndIdentifier) {
        super(commandCode, request, proxiable, error, retransmitted, applicationId,
              hopByHopIdentifier, endToEndIdentifier);
    }
}
