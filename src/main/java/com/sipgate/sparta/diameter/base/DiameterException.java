package com.sipgate.sparta.diameter.base;

/**
 * Exception thrown for Diameter protocol related errors.
 */
public class DiameterException extends Exception {
    private final int resultCode;

    public DiameterException(final String message) {
        super(message);
        this.resultCode = -1;
    }

    public DiameterException(final String message, final Throwable cause) {
        super(message, cause);
        this.resultCode = -1;
    }

    public DiameterException(final String message, final int resultCode) {
        super(message);
        this.resultCode = resultCode;
    }

    public DiameterException(final String message, final int resultCode, final Throwable cause) {
        super(message, cause);
        this.resultCode = resultCode;
    }

    public int getResultCode() {
        return resultCode;
    }

    public boolean hasResultCode() {
        return resultCode != -1;
    }
}
