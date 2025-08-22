package com.sipgate.sparta.diameter;

/**
 * Exception thrown for Diameter protocol related errors.
 */
public class DiameterException extends Exception {

    /**
     * Constructs a DiameterException with the specified message.
     *
     * @param message the detail message
     */
    public DiameterException(final String message) {
        super(message);
    }

    /**
     * Constructs a DiameterException with the specified message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public DiameterException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
