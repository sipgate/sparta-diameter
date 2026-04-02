package com.sipgate.sparta.diameter.base.session;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

final class DiameterSessionMeters {

    static final String ERROR_TYPE_TIMEOUT = "timeout";
    static final String ERROR_TYPE_ERROR_ANSWER = "error_answer";
    static final String ERROR_TYPE_HANDLER_ERROR_ANSWER = "handler_error_answer";
    static final String ERROR_TYPE_HANDLER_EXCEPTION = "handler_exception";

    private static final String PREFIX = "diameter.";
    private static final String TAG_COMMAND_CODE = "command_code";
    private static final String TAG_APPLICATION_ID = "application_id";
    private static final String TAG_ERROR_TYPE = "error_type";

    private final MeterRegistry registry;

    DiameterSessionMeters(final MeterRegistry registry) {
        this.registry = registry;
    }

    /**
     * @param commandCode as specified in diameter
     * @param applicationId as specified in diameter
     * @param errorType one of the ERROR_TYPE_* constants
     */
    void recordError(final int commandCode, final int applicationId, final String errorType) {
        Counter.builder(PREFIX + "requests.errors")
                .description("Errors encountered while handling a Diameter request, tagged by error_type (timeout, error_answer, handler_error_answer, handler_exception).")
                .tag(TAG_COMMAND_CODE, String.valueOf(commandCode))
                .tag(TAG_APPLICATION_ID, String.valueOf(applicationId))
                .tag(TAG_ERROR_TYPE, errorType)
                .register(registry)
                .increment();
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
        sample.stop(Timer.builder(PREFIX + "request.duration")
                .description("Round-trip time from sending a Diameter request to receiving the answer; timeouts and write failures are excluded.")
                .tag(TAG_COMMAND_CODE, String.valueOf(commandCode))
                .tag(TAG_APPLICATION_ID, String.valueOf(applicationId))
                .register(registry));
    }

    /**
     * Stops the sample and records elapsed time in {@code diameter.handler.duration}.
     *
     * @param commandCode as specified in diameter
     * @param applicationId as specified in diameter
     */
    void stopHandlerTimer(final Timer.Sample sample, final int commandCode, final int applicationId) {
        sample.stop(Timer.builder(PREFIX + "handler.duration")
                .description("Time spent inside the application handler processing a received Diameter request.")
                .tag(TAG_COMMAND_CODE, String.valueOf(commandCode))
                .tag(TAG_APPLICATION_ID, String.valueOf(applicationId))
                .register(registry));
    }
}
