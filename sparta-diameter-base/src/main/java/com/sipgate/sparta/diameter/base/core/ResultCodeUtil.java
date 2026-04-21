package com.sipgate.sparta.diameter.base.core;

import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/**
 * Utility class for working with Diameter result codes.
 * <p>
 * Provides methods to determine if a result code indicates an error
 * as defined in RFC 6733.
 * </p>
 */
public final class ResultCodeUtil {

    /**
     * Determines if a result code indicates an error condition.
     * <p>
     * According to RFC 6733:
     * - 1xxx: Informational (not error)
     * - 2xxx: Success (not error)
     * - 3xxx: Protocol Errors (error)
     * - 4xxx: Transient Failures (error)
     * - 5xxx: Permanent Failures (error)
     * </p>
     *
     * @param resultCode The result code to check
     * @return true if the result code indicates an error, false otherwise
     */
    public static boolean isErrorCode(final long resultCode) {
        return resultCode >= 3000L && resultCode <= 5999L;
    }

    /**
     * Formats the result code from an {@link ErrorAnswer} for logging.
     * <p>
     * Prefers the Experimental-Result-Code nested inside the Experimental-Result
     * grouped AVP. Falls back to the top-level Result-Code.
     * </p>
     *
     * @param answer The error answer to extract from
     * @return A human-readable string like "Result-Code 3002" or "Experimental-Result-Code 5001"
     */
    public static String describeResultCode(final ErrorAnswer answer) {
        final var experimentalResult = answer.getExperimentalResult();
        if (experimentalResult == null) {
            return "Result-Code " + answer.getResultCode();
        }
        final var avp = experimentalResult.findAVP(new AVPKey(DiameterConstants.AVP_EXPERIMENTAL_RESULT_CODE, 0));
        if (avp == null) {
            return "Result-Code " + answer.getResultCode();
        }
        final var vendorIdAvp = experimentalResult.findAVP(new AVPKey(DiameterConstants.AVP_VENDOR_ID, 0));
        final var vendorId = vendorIdAvp == null ? 0L : vendorIdAvp.getDataAsUnsignedInt();
        return "Experimental-Result-Code " + avp.getDataAsUnsignedInt() + " (Vendor-Id " + vendorId + ")";
    }

    private ResultCodeUtil() {
        // Utility class - no instantiation
    }
}
