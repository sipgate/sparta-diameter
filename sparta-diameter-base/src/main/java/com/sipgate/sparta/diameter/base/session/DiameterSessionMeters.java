package com.sipgate.sparta.diameter.base.session;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Objects;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

final class DiameterSessionMeters {

    private static final String PREFIX = "diameter.";
    private static final String TAG_COMMAND_CODE = "command_code";
    private static final String TAG_APPLICATION_ID = "application_id";
    private static final String TAG_CAUSE = "cause";

    private final MeterRegistry registry;

    DiameterSessionMeters(final MeterRegistry registry) {
        this.registry = registry;
    }

    /**
     * @param commandCode as specified in diameter
     * @param applicationId as specified in diameter
     * @param cause class name used for tag
     */
    void recordOutgoingRequestError(final int commandCode, final int applicationId, final Throwable cause) {
        Objects.requireNonNull(cause, "cause must not be null");
        Counter.builder(PREFIX + "requests.errors")
                .description("Errors received after sending a Diameter request, e.g. write failures, error answers, timeouts, disconnects, cancellations")
                .tag(TAG_COMMAND_CODE, String.valueOf(commandCode))
                .tag(TAG_APPLICATION_ID, String.valueOf(applicationId))
                .tag(TAG_CAUSE, extractCauseTag(cause))
                .register(registry)
                .increment();
    }

    /**
     * @param commandCode as specified in diameter
     * @param applicationId as specified in diameter
     * @param cause class name used for tag
     */
    void recordHandlerError(final int commandCode, final int applicationId, final Throwable cause) {
        Objects.requireNonNull(cause, "cause must not be null");
        Counter.builder(PREFIX + "handler.errors")
                .description("Errors encountered while handling a Diameter request.")
                .tag(TAG_COMMAND_CODE, String.valueOf(commandCode))
                .tag(TAG_APPLICATION_ID, String.valueOf(applicationId))
                .tag(TAG_CAUSE, extractCauseTag(cause))
                .register(registry)
                .increment();
    }

    private static String extractCauseTag(final Throwable cause) {
        final var effectiveCause = extractEffectiveCause(cause);
        final var simple = effectiveCause.getClass().getSimpleName();
        return simple.isEmpty() ? effectiveCause.getClass().getName() : simple;
    }

    private static Throwable extractEffectiveCause(final Throwable wrapper) {
        if (wrapper instanceof CompletionException || wrapper instanceof UncheckedIOException || wrapper instanceof ExecutionException) {
            return wrapper.getCause() == null ? wrapper : wrapper.getCause();
        }

        return wrapper;
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
