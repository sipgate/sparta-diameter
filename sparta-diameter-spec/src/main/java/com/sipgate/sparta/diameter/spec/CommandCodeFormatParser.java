package com.sipgate.sparta.diameter.spec;

import java.util.LinkedHashSet;
import java.util.Set;

import com.sipgate.sparta.diameter.spec.Ccf.Body;
import com.sipgate.sparta.diameter.spec.Ccf.Token;
import com.sipgate.sparta.diameter.spec.Ccf.TokenStream;
import com.sipgate.sparta.diameter.spec.Ccf.Type;

/**
 * Parses Command Code Format specifications per RFC 6733 §3.2.
 *
 * <p>Tolerant of common spec-writing variations: whitespace inside
 * {@code < Diameter Header: ... >} (the RFC's own §3.2 example deviates
 * from its ABNF by using a space instead of the hyphen), an optional
 * {@code < ... >} around the command name, and rules listed in any order
 * rather than the strict {@code header *fixed *required *optional} the
 * ABNF prescribes.
 */
public final class CommandCodeFormatParser {

    private CommandCodeFormatParser() {
    }

    public static Set<CommandDef> parse(final String input) {
        final TokenStream tokens = new TokenStream(input);
        final Set<CommandDef> result = new LinkedHashSet<>();
        while (tokens.peek().type() != Type.EOF) {
            result.add(parseCommand(tokens));
        }
        return result;
    }

    private static CommandDef parseCommand(final TokenStream tokens) {
        final String commandName = Ccf.parseName(tokens);
        tokens.expect(Type.ASSIGN);
        final Header header = parseHeader(tokens);
        final Body body = Ccf.parseBody(tokens);
        return new CommandDef(
                header.applicationId, header.commandId, commandName,
                header.isRequest, header.isProxiable, header.isError,
                body.fixed(), body.required(), body.optional());
    }

    private record Header(int commandId, boolean isRequest, boolean isProxiable, boolean isError, long applicationId) {
    }

    private static Header parseHeader(final TokenStream tokens) {
        tokens.expect(Type.LT);
        final Token first = tokens.expect(Type.NAME);
        if (first.text().equalsIgnoreCase("Diameter")) {
            final Token second = tokens.expect(Type.NAME);
            if (!second.text().equalsIgnoreCase("Header")) {
                throw new IllegalStateException(
                        "Expected 'Header' after 'Diameter' at position " + second.position() + ", got '" + second.text() + "'");
            }
        } else if (!first.text().equalsIgnoreCase("Diameter-Header")) {
            throw new IllegalStateException(
                    "Expected 'Diameter Header' or 'Diameter-Header' at position " + first.position() + ", got '" + first.text() + "'");
        }
        tokens.expect(Type.COLON);
        final int commandCode = Integer.parseInt(tokens.expect(Type.NUMBER).text());

        boolean isRequest = false;
        boolean isProxiable = false;
        boolean isError = false;
        long applicationId = 0L;

        while (tokens.peek().type() == Type.COMMA) {
            tokens.next();
            final Token t = tokens.next();
            if (t.type() == Type.NAME) {
                switch (t.text()) {
                    case "REQ" -> isRequest = true;
                    case "PXY" -> isProxiable = true;
                    case "ERR" -> isError = true;
                    default -> throw new IllegalStateException(
                            "Unknown header flag '" + t.text() + "' at position " + t.position());
                }
            } else if (t.type() == Type.NUMBER) {
                applicationId = Long.parseLong(t.text());
            } else {
                throw new IllegalStateException(
                        "Expected flag or application-id but got " + t.type() + " at position " + t.position());
            }
        }
        tokens.expect(Type.GT);

        return new Header(commandCode, isRequest, isProxiable, isError, applicationId);
    }
}
