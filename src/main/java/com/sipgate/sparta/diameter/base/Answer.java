package com.sipgate.sparta.diameter.base;

/**
 * Base class for all Diameter answer messages.
 * Answer messages have the R-bit cleared in the Diameter header flags.
 */
public abstract class Answer extends Command {

    protected Answer(final int commandCode, final boolean proxiable, final boolean error, final boolean retransmitted,
                     final int applicationId, final int hopByHopIdentifier, final int endToEndIdentifier) {
        super(commandCode, false, proxiable, error, retransmitted, applicationId,
              hopByHopIdentifier, endToEndIdentifier);
    }

    /**
     * Convenience constructor for successful answers (no error flag set).
     */
    protected Answer(final int commandCode, final boolean proxiable, final boolean retransmitted,
                     final int applicationId, final int hopByHopIdentifier, final int endToEndIdentifier) {
        this(commandCode, proxiable, false, retransmitted, applicationId, hopByHopIdentifier, endToEndIdentifier);
    }

    /**
     * Gets the Result-Code AVP from this answer.
     * @return the result code, or -1 if not found
     */
    public int getResultCode() {
        final AVP resultCodeAVP = findAVP(DiameterConstants.RESULT_CODE);
        if (resultCodeAVP != null && resultCodeAVP.getData().length >= 4) {
            return resultCodeAVP.getDataAsInt();
        }
        return -1;
    }

    /**
     * Sets the Result-Code AVP for this answer.
     */
    public void setResultCode(final int resultCode) {
        setAVP(AVP.createIntegerAVP(DiameterConstants.RESULT_CODE, true, resultCode));
    }
}
