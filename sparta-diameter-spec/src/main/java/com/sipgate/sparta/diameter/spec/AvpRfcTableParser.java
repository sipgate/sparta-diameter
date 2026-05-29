package com.sipgate.sparta.diameter.spec;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Parses an AVP definition table in RFC 6733 §4.5 format
 * ({@code Attribute Name | AVP Code | Section Defined | Data Type |
 * MUST | MUST NOT}).
 *
 * <p>The dashed separator line
 * ({@code   ---...---|----+-----|}) toggles between HEADER and DATA
 * regions. Page headers, footers, and box-drawing lines between two
 * separators are skipped; data rows between two separators are parsed.
 *
 * <p>Row blocks are detected by indentation. The row-start indent is
 * taken from the first separator line (so the parser self-calibrates
 * regardless of how much leading whitespace Java text-block stripping
 * left in place). Lines at that exact indent start a new row;
 * continuations are indented further. The Inband-Security-Id row
 * (RFC 6733 §4.5) is the awkward case — name + flags on line 1, code
 * + section + type on line 2 — and is handled by collecting tokens
 * across all lines of a block.
 *
 * <p>RFC 6733 §4.5 has no May-Encrypt column (the P flag is reserved),
 * so {@link AvpDef#mayBeEncrypted()} is always {@code false} here.
 */
public final class AvpRfcTableParser {

    private static final Set<String> KNOWN_TYPES = Set.of(
            "OctetString", "Integer32", "Integer64", "Unsigned32", "Unsigned64",
            "Float32", "Float64", "Grouped", "Address", "Time", "UTF8String",
            "DiameterIdentity", "DiameterURI", "Enumerated", "IPFilterRule", "QoSFilterRule",
            "DiamIdent", "DiamURI");

    private AvpRfcTableParser() {
    }

    public static Set<AvpDef> parse(final String input) {
        return parse(input, 0L);
    }

    public static Set<AvpDef> parse(final String input, final long applicationId) {
        final String[] lines = input.split("\\R", -1);
        final int rowStartIndent = findFirstSeparatorIndent(lines);
        if (rowStartIndent < 0) {
            return Set.of();
        }
        final Set<AvpDef> result = new LinkedHashSet<>();
        final List<String> currentBlock = new ArrayList<>();
        boolean inData = false;
        for (final String line : lines) {
            if (isSeparator(line)) {
                if (!currentBlock.isEmpty()) {
                    result.add(buildAvpDef(currentBlock, applicationId));
                    currentBlock.clear();
                }
                inData = !inData;
                continue;
            }
            if (!inData) {
                continue;
            }
            if (line.isBlank() || !line.contains("|")) {
                continue;
            }
            final boolean isStart = leadingSpaces(line) == rowStartIndent;
            if (isStart && !currentBlock.isEmpty()) {
                result.add(buildAvpDef(currentBlock, applicationId));
                currentBlock.clear();
            }
            if (isStart || !currentBlock.isEmpty()) {
                currentBlock.add(line);
            }
        }
        if (!currentBlock.isEmpty()) {
            result.add(buildAvpDef(currentBlock, applicationId));
        }
        return result;
    }

    private static int findFirstSeparatorIndent(final String[] lines) {
        for (final String line : lines) {
            if (isSeparator(line)) {
                return leadingSpaces(line);
            }
        }
        return -1;
    }

    private static boolean isSeparator(final String line) {
        return line.matches("\\s*-{5,}.*\\|.*");
    }

    private static int leadingSpaces(final String line) {
        int n = 0;
        while (n < line.length() && line.charAt(n) == ' ') {
            n++;
        }
        return n;
    }

    private static AvpDef buildAvpDef(final List<String> block, final long applicationId) {
        final StringBuilder name = new StringBuilder();
        Long code = null;
        String valueType = null;
        AvpFlagRule mandatoryBit = null;
        AvpFlagRule vendorSpecificBit = null;

        for (final String line : block) {
            final int firstPipe = line.indexOf('|');
            final String main = (firstPipe >= 0) ? line.substring(0, firstPipe) : line;
            for (final String tok : main.trim().split("\\s+")) {
                if (tok.isEmpty()) {
                    continue;
                }
                if (code == null && tok.matches("\\d+")) {
                    code = Long.parseLong(tok);
                } else if (tok.matches("\\d+(\\.\\d+)+")) {
                    // section — drop, not in AvpDef
                } else if (valueType == null && KNOWN_TYPES.contains(tok)) {
                    valueType = expandAlias(tok);
                } else {
                    name.append(tok);
                }
            }
            if (firstPipe >= 0) {
                final String[] columns = line.substring(firstPipe + 1).split("\\|", -1);
                if (columns.length >= 1) {
                    final Set<Character> flags = parseFlagChars(columns[0]);
                    if (flags.contains('M')) mandatoryBit = AvpFlagRule.MUST;
                    if (flags.contains('V')) vendorSpecificBit = AvpFlagRule.MUST;
                }
                if (columns.length >= 2) {
                    final Set<Character> flags = parseFlagChars(columns[1]);
                    if (flags.contains('M')) mandatoryBit = AvpFlagRule.MUST_NOT;
                    if (flags.contains('V')) vendorSpecificBit = AvpFlagRule.MUST_NOT;
                }
            }
        }

        if (code == null) {
            throw new IllegalStateException("Missing AVP Code in block: " + block);
        }
        if (valueType == null) {
            throw new IllegalStateException("Missing Data Type in block: " + block);
        }
        if (mandatoryBit == null) {
            throw new IllegalStateException("Missing M-bit flag rule in block: " + block);
        }
        if (vendorSpecificBit == null) {
            throw new IllegalStateException("Missing V-bit flag rule in block: " + block);
        }

        return new AvpDef(applicationId, code, name.toString(), valueType, mandatoryBit, vendorSpecificBit, false);
    }

    private static String expandAlias(final String dataType) {
        return switch (dataType) {
            case "DiamIdent" -> "DiameterIdentity";
            case "DiamURI" -> "DiameterURI";
            default -> dataType;
        };
    }

    private static Set<Character> parseFlagChars(final String segment) {
        final Set<Character> result = new LinkedHashSet<>();
        for (final char c : segment.toCharArray()) {
            if (c == 'M' || c == 'V' || c == 'P') {
                result.add(c);
            }
        }
        return result;
    }
}
