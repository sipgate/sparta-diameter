package com.sipgate.sparta.diameter.core;

import com.sipgate.sparta.diameter.core.avp.AVP;

/**
 * Base class for all Diameter answer messages.
 * <p>
 * This class represents a Diameter answer message as defined in
 * <a href="https://datatracker.ietf.org/doc/html/rfc6733#section-3">RFC 6733, Section 3</a>.
 * Answer messages have the R-bit cleared in the Diameter header flags.
 * </p>
 */
public abstract class Answer extends Command {

    /**
     * Constructs a Diameter answer message.
     *
     * @param commandCode        The command code of the answer.
     * @param proxiable          Indicates whether the message is proxiable.
     * @param error              Indicates whether the message is an error.
     * @param retransmitted      Indicates whether the message is retransmitted.
     * @param applicationId      The application ID of the answer.
     * @param hopByHopIdentifier The hop-by-hop identifier.
     * @param endToEndIdentifier The end-to-end identifier.
     */
    protected Answer(final int commandCode, final boolean proxiable, final boolean error, final boolean retransmitted,
                     final int applicationId, final int hopByHopIdentifier, final int endToEndIdentifier) {
        super(commandCode, false, proxiable, error, retransmitted, applicationId,
              hopByHopIdentifier, endToEndIdentifier);
    }

    /**
     * Constructs a Diameter answer message for successful responses (no error flag set).
     *
     * @param commandCode        The command code of the answer.
     * @param proxiable          Indicates whether the message is proxiable.
     * @param retransmitted      Indicates whether the message is retransmitted.
     * @param applicationId      The application ID of the answer.
     * @param hopByHopIdentifier The hop-by-hop identifier.
     * @param endToEndIdentifier The end-to-end identifier.
     */
    protected Answer(final int commandCode, final boolean proxiable, final boolean retransmitted,
                     final int applicationId, final int hopByHopIdentifier, final int endToEndIdentifier) {
        this(commandCode, proxiable, false, retransmitted, applicationId, hopByHopIdentifier, endToEndIdentifier);
    }

    /**
     * Retrieves the Result-Code AVP from this answer.
     *
     * @return The result code, or -1 if not found.
     */
    public int getResultCode() {
        final AVP resultCodeAVP = findAVP(DiameterConstants.AVP_RESULT_CODE);
        if (resultCodeAVP != null && resultCodeAVP.getData().length >= 4) {
            return resultCodeAVP.getDataAsInt();
        }
        return -1;
    }

    /**
     * Sets the Result-Code AVP for this answer.
     *
     * @param resultCode The result code to set.
     */
    public void setResultCode(final int resultCode) {
        setAVP(AVP.create(DiameterConstants.AVP_RESULT_CODE, resultCode));
    }
}
