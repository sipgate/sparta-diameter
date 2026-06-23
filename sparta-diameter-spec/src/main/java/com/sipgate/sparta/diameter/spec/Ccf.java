package com.sipgate.sparta.diameter.spec;

import java.util.ArrayList;
import java.util.List;

/**
 * Shared infrastructure for RFC 6733 CCF parsing: lexer, name parsing,
 * and the {@code *fixed *required *optional} body grammar used by both
 * §3.2 {@code command-def} and §4.4 {@code grouped-avp-def}.
 */
final class Ccf {

    private Ccf() {
    }

    enum Type {
        LT, GT, LBRACE, RBRACE, LBRACKET, RBRACKET, COMMA, STAR, COLON, ASSIGN, NAME, NUMBER, EOF
    }

    record Token(Type type, String text, int position) {
    }

    record Body(List<AvpRule> fixed, List<AvpRule> required, List<AvpRule> optional) {
    }

    static String parseName(final TokenStream tokens) {
        if (tokens.peek().type() == Type.LT
                && tokens.peekAt(1).type() == Type.NAME
                && tokens.peekAt(2).type() == Type.GT) {
            tokens.expect(Type.LT);
            final String name = tokens.expect(Type.NAME).text();
            tokens.expect(Type.GT);
            return name;
        }
        return tokens.expect(Type.NAME).text();
    }

    static Body parseBody(final TokenStream tokens) {
        final List<AvpRule> fixed = new ArrayList<>();
        final List<AvpRule> required = new ArrayList<>();
        final List<AvpRule> optional = new ArrayList<>();
        while (isRuleStart(tokens)) {
            parseRule(tokens, fixed, required, optional);
        }
        return new Body(List.copyOf(fixed), List.copyOf(required), List.copyOf(optional));
    }

    private static boolean isRuleStart(final TokenStream tokens) {
        final Type t = tokens.peek().type();
        if (t == Type.LT
                && tokens.peekAt(1).type() == Type.NAME
                && tokens.peekAt(2).type() == Type.GT
                && tokens.peekAt(3).type() == Type.ASSIGN) {
            return false;
        }
        return switch (t) {
            case STAR, NUMBER, LT, LBRACE, LBRACKET -> true;
            default -> false;
        };
    }

    private static void parseRule(final TokenStream tokens,
                                  final List<AvpRule> fixed,
                                  final List<AvpRule> required,
                                  final List<AvpRule> optional) {
        Long min = null;
        Long max = null;
        boolean hasQual = false;
        if (tokens.peek().type() == Type.NUMBER) {
            min = Long.parseLong(tokens.next().text());
            hasQual = true;
            tokens.expect(Type.STAR);
            if (tokens.peek().type() == Type.NUMBER) {
                max = Long.parseLong(tokens.next().text());
            }
        } else if (tokens.peek().type() == Type.STAR) {
            tokens.next();
            hasQual = true;
            if (tokens.peek().type() == Type.NUMBER) {
                max = Long.parseLong(tokens.next().text());
            }
        }

        final Token bracket = tokens.next();
        switch (bracket.type()) {
            case LT -> {
                final String name = tokens.expect(Type.NAME).text();
                tokens.expect(Type.GT);
                fixed.add(buildRule(min, max, hasQual, 1L, 1L, name));
            }
            case LBRACE -> {
                final String name = tokens.expect(Type.NAME).text();
                tokens.expect(Type.RBRACE);
                required.add(buildRule(min, max, hasQual, 1L, 1L, name));
            }
            case LBRACKET -> {
                final String name = tokens.expect(Type.NAME).text();
                tokens.expect(Type.RBRACKET);
                optional.add(buildRule(min, max, hasQual, 0L, 1L, name));
            }
            default -> throw new IllegalStateException(
                    "Expected '<', '{' or '[' but got " + bracket.type() + " at position " + bracket.position());
        }
    }

    private static AvpRule buildRule(final Long min, final Long max, final boolean hasQual,
                                     final long defaultMin, final long defaultMax, final String name) {
        final long m = (min != null) ? min : (hasQual ? 0L : defaultMin);
        final long mx = (max != null) ? max : (hasQual ? Long.MAX_VALUE : defaultMax);
        return new AvpRule(m, mx, name);
    }

    static final class TokenStream {
        private final List<Token> tokens;
        private int index;

        TokenStream(final String input) {
            this.tokens = tokenize(input);
            this.index = 0;
        }

        Token peek() {
            return tokens.get(index);
        }

        Token peekAt(final int offset) {
            final int target = index + offset;
            return tokens.get(Math.min(target, tokens.size() - 1));
        }

        Token next() {
            return tokens.get(index++);
        }

        Token expect(final Type type) {
            final Token t = next();
            if (t.type() != type) {
                throw new IllegalStateException(
                        "Expected " + type + " but got " + t.type() + " ('" + t.text() + "') at position " + t.position());
            }
            return t;
        }

        private static List<Token> tokenize(final String input) {
            final List<Token> out = new ArrayList<>();
            int i = 0;
            while (i < input.length()) {
                final char c = input.charAt(i);
                if (Character.isWhitespace(c)) {
                    i++;
                    continue;
                }
                if (c == '<') {
                    out.add(new Token(Type.LT, "<", i));
                    i++;
                } else if (c == '>') {
                    out.add(new Token(Type.GT, ">", i));
                    i++;
                } else if (c == '{') {
                    out.add(new Token(Type.LBRACE, "{", i));
                    i++;
                } else if (c == '}') {
                    out.add(new Token(Type.RBRACE, "}", i));
                    i++;
                } else if (c == '[') {
                    out.add(new Token(Type.LBRACKET, "[", i));
                    i++;
                } else if (c == ']') {
                    out.add(new Token(Type.RBRACKET, "]", i));
                    i++;
                } else if (c == ',') {
                    out.add(new Token(Type.COMMA, ",", i));
                    i++;
                } else if (c == '*') {
                    out.add(new Token(Type.STAR, "*", i));
                    i++;
                } else if (c == ':') {
                    if (i + 2 < input.length() && input.charAt(i + 1) == ':' && input.charAt(i + 2) == '=') {
                        out.add(new Token(Type.ASSIGN, "::=", i));
                        i += 3;
                    } else {
                        out.add(new Token(Type.COLON, ":", i));
                        i++;
                    }
                } else if (Character.isDigit(c)) {
                    final int start = i;
                    while (i < input.length() && Character.isDigit(input.charAt(i))) {
                        i++;
                    }
                    if (i < input.length() && Character.isLetter(input.charAt(i))) {
                        // digit-prefixed AVP name (e.g. 3GPP-AAA-Server-Name) — scan as NAME
                        while (i < input.length()
                                && (Character.isLetterOrDigit(input.charAt(i)) || input.charAt(i) == '-')) {
                            i++;
                        }
                        out.add(new Token(Type.NAME, input.substring(start, i), start));
                    } else {
                        out.add(new Token(Type.NUMBER, input.substring(start, i), start));
                    }
                } else if (Character.isLetter(c)) {
                    final int start = i;
                    while (i < input.length()
                            && (Character.isLetterOrDigit(input.charAt(i)) || input.charAt(i) == '-')) {
                        i++;
                    }
                    out.add(new Token(Type.NAME, input.substring(start, i), start));
                } else {
                    throw new IllegalArgumentException("Unexpected character '" + c + "' at position " + i);
                }
            }
            out.add(new Token(Type.EOF, "", input.length()));
            return out;
        }
    }
}
