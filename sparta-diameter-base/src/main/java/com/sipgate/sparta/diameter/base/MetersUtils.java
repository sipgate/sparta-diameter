package com.sipgate.sparta.diameter.base;

import java.io.UncheckedIOException;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

public class MetersUtils {
    public static final String TAG_CAUSE = "cause";

    public static String extractCauseTag(final Throwable cause) {
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
}
