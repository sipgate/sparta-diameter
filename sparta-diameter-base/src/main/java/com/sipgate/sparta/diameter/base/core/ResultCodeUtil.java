package com.sipgate.sparta.diameter.base.core;

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

    private ResultCodeUtil() {
        // Utility class - no instantiation
    }
}
