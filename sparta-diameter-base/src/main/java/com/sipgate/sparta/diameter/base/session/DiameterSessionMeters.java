package com.sipgate.sparta.diameter.base.session;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

final class DiameterSessionMeters {

    static final String COMMAND_TYPE_REQUEST = "request";
    static final String COMMAND_TYPE_ANSWER = "answer";
    static final String ERROR_TYPE_TIMEOUT = "timeout";
    static final String ERROR_TYPE_WRITE_FAILURE = "write_failure";
    static final String ERROR_TYPE_ERROR_ANSWER = "error_answer";
    static final String ERROR_TYPE_HANDLER_ERROR_ANSWER = "handler_error_answer";
    static final String ERROR_TYPE_HANDLER_EXCEPTION = "handler_exception";

    private static final String PREFIX = "diameter.";
    private static final String TAG_COMMAND_CODE = "command_code";
    private static final String TAG_COMMAND_TYPE = "command_type";
    private static final String TAG_APPLICATION_ID = "application_id";
    private static final String TAG_ERROR_TYPE = "error_type";

    private final MeterRegistry registry;

    DiameterSessionMeters(final MeterRegistry registry) {
        this.registry = registry;
    }

    /**
     * @param commandCode as specified in diameter
     * @param applicationId as specified in diameter
     * @param commandType one of the COMMAND_TYPE_* constants
     */
    void recordSent(final int commandCode, final int applicationId, final String commandType) {
        registry.counter(PREFIX + "commands.sent",
                TAG_COMMAND_CODE, String.valueOf(commandCode),
                TAG_APPLICATION_ID, String.valueOf(applicationId),
                TAG_COMMAND_TYPE, commandType).increment();
    }

    /**
     * @param commandCode as specified in diameter
     * @param applicationId as specified in diameter
     * @param errorType one of the ERROR_TYPE_* constants
     */
    void recordError(final int commandCode, final int applicationId, final String errorType) {
        registry.counter(PREFIX + "requests.errors",
                TAG_COMMAND_CODE, String.valueOf(commandCode),
                TAG_APPLICATION_ID, String.valueOf(applicationId),
                TAG_ERROR_TYPE, errorType).increment();
    }

    Timer.Sample startTimer() {
        return Timer.start(registry);
    }

    /**
     * Stops the sample and records elapsed time in {@code diameter.request.duration}.
     * Only called on answer receipt — timeouts and write failures are excluded.
     *
     * @param commandCode as specified in diameter
     * @param applicationId as specified in diameter
     */
    void stopRequestTimer(final Timer.Sample sample, final int commandCode, final int applicationId) {
        sample.stop(registry.timer(PREFIX + "request.duration",
                TAG_COMMAND_CODE, String.valueOf(commandCode),
                TAG_APPLICATION_ID, String.valueOf(applicationId)));
    }

    /**
     * Stops the sample and records elapsed time in {@code diameter.handler.duration}.
     *
     * @param commandCode as specified in diameter
     * @param applicationId as specified in diameter
     */
    void stopHandlerTimer(final Timer.Sample sample, final int commandCode, final int applicationId) {
        sample.stop(registry.timer(PREFIX + "handler.duration",
                TAG_COMMAND_CODE, String.valueOf(commandCode),
                TAG_APPLICATION_ID, String.valueOf(applicationId)));
    }
}
