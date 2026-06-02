package com.sipgate.sparta.diameter.spec;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Parses an AVP definition table from a Diameter RFC. Two flag-column
 * layouts are supported:
 *
 * <ul>
 *   <li>RFC 6733 §4.5: {@code | MUST | MUST NOT |} — two columns.</li>
 *   <li>RFC 4005 §6:   {@code | MUST | MAY | SHLD NOT | MUST NOT | Encr |}
 *       — five columns. The {@code Encr} column carries {@code Y}
 *       when the AVP may be encrypted.</li>
 * </ul>
 *
 * <p>The layout is detected per data row from the number of
 * pipe-separated segments, so the same parser handles either RFC
 * without a hint.
 *
 * <p>The dashed separator line
 * ({@code   ---...---|----+-----|}) marks the start of the data
 * region. Anything before the first separator (page headers, column
 * titles, box-drawing lines) is skipped. Subsequent separators just
 * delimit row blocks — RFC 6733 / RFC 4005 use one closing separator
 * after all rows; RFC 7683 puts a separator between every row.
 *
 * <p>Row blocks are detected by indentation. The row-start indent is
 * taken from the first separator line (so the parser self-calibrates
 * regardless of how much leading whitespace Java text-block stripping
 * left in place). Lines at that exact indent start a new row;
 * continuations are indented further. The Inband-Security-Id row
 * (RFC 6733 §4.5) is the awkward case — name + flags on line 1, code
 * + section + type on line 2 — and is handled by collecting tokens
 * across all lines of a block.
 */
public final class AvpRfcTableParser {

    // Canonical AvpDef types plus the abbreviated forms used in RFC 4005
    // tables (expanded back to the canonical name via expandAlias).
    private static final Set<String> KNOWN_TYPES;
    static {
        final Set<String> all = new HashSet<>(AvpDef.KNOWN_TYPES);
        all.add("DiamIdent");
        all.add("DiamURI");
        all.add("IPFltrRule");
        all.add("QoSFltrRule");
        KNOWN_TYPES = Set.copyOf(all);
    }

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
        boolean started = false;
        for (final String line : lines) {
            if (isSeparator(line)) {
                if (!currentBlock.isEmpty()) {
                    result.add(buildAvpDef(currentBlock, applicationId));
                    currentBlock.clear();
                }
                started = true;
                continue;
            }
            if (!started) {
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
        return line.matches("\\s*-{5,}.*[|+].*");
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
        boolean mayBeEncrypted = false;

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
                // Trailing empty segment after the closing pipe doesn't count.
                final int flagCount = columns.length - 1;
                final int mustIdx;
                final int mustNotIdx;
                final int encrIdx;
                if (flagCount >= 5) {
                    // RFC 4005: MUST | MAY | SHLD NOT | MUST NOT | Encr
                    mustIdx = 0;
                    mustNotIdx = 3;
                    encrIdx = 4;
                } else {
                    // RFC 6733: MUST | MUST NOT
                    mustIdx = 0;
                    mustNotIdx = 1;
                    encrIdx = -1;
                }
                if (flagCount > mustIdx) {
                    final Set<Character> flags = parseFlagChars(columns[mustIdx]);
                    if (flags.contains('M')) mandatoryBit = AvpFlagRule.MUST;
                    if (flags.contains('V')) vendorSpecificBit = AvpFlagRule.MUST;
                }
                if (flagCount > mustNotIdx) {
                    final Set<Character> flags = parseFlagChars(columns[mustNotIdx]);
                    if (flags.contains('M')) mandatoryBit = AvpFlagRule.MUST_NOT;
                    if (flags.contains('V')) vendorSpecificBit = AvpFlagRule.MUST_NOT;
                }
                if (encrIdx >= 0 && flagCount > encrIdx && columns[encrIdx].indexOf('Y') >= 0) {
                    mayBeEncrypted = true;
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
            mandatoryBit = AvpFlagRule.MAY;
        }
        if (vendorSpecificBit == null) {
            vendorSpecificBit = AvpFlagRule.MAY;
        }

        return new AvpDef(applicationId, code, name.toString(), valueType, mandatoryBit, vendorSpecificBit, mayBeEncrypted);
    }

    private static String expandAlias(final String dataType) {
        return switch (dataType) {
            case "DiamIdent" -> "DiameterIdentity";
            case "DiamURI" -> "DiameterURI";
            case "IPFltrRule" -> "IPFilterRule";
            case "QoSFltrRule" -> "QoSFilterRule";
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
