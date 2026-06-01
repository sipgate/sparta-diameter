package com.sipgate.sparta.diameter.specextractor._3gpp;

/**
 * A 3GPP/ETSI specification source: where to fetch the document and
 * where the extracted JSON should land.
 *
 * <p>{@code url} points either at a {@code .zip} (the usual 3GPP
 * distribution form, containing one {@code .docx}) or directly at a
 * {@code .docx}. The extractor dispatches on the URL's file extension.
 *
 * <p>{@code jsonPath} is repository-root-relative. JSON files live in
 * the test resources of whichever module consumes them.
 */
import java.util.List;

public record SpecSource(
        String url,
        String jsonPath,
        long applicationId,
        long vendorId,
        List<String> tableCaptions) {
}
