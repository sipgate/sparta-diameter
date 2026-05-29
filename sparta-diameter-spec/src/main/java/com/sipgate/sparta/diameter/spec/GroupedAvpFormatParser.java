package com.sipgate.sparta.diameter.spec;

import java.util.LinkedHashSet;
import java.util.Set;

import com.sipgate.sparta.diameter.spec.Ccf.Body;
import com.sipgate.sparta.diameter.spec.Ccf.Token;
import com.sipgate.sparta.diameter.spec.Ccf.TokenStream;
import com.sipgate.sparta.diameter.spec.Ccf.Type;

/**
 * Parses Grouped AVP definitions per RFC 6733 §4.4
 * ({@code grouped-avp-def}).
 *
 * <p>Same body grammar as §3.2 {@code command-def}; the only difference
 * is the header: {@code < AVP-Header: avpcode [vendor] >} instead of
 * the Diameter command header. Tolerant of {@code AVP Header} with a
 * space (matches the §4.4.1 Example-AVP) as well as the hyphenated
 * {@code AVP-Header} from the ABNF.
 */
public final class GroupedAvpFormatParser {

    private GroupedAvpFormatParser() {
    }

    public static Set<GroupedAvpDef> parse(final String input) {
        final TokenStream tokens = new TokenStream(input);
        final Set<GroupedAvpDef> result = new LinkedHashSet<>();
        while (tokens.peek().type() != Type.EOF) {
            result.add(parseDefinition(tokens));
        }
        return result;
    }

    private static GroupedAvpDef parseDefinition(final TokenStream tokens) {
        final String avpName = Ccf.parseName(tokens);
        tokens.expect(Type.ASSIGN);
        final Header header = parseHeader(tokens);
        final Body body = Ccf.parseBody(tokens);
        return new GroupedAvpDef(
                header.vendor, header.avpCode, avpName,
                body.fixed(), body.required(), body.optional());
    }

    private record Header(long avpCode, long vendor) {
    }

    private static Header parseHeader(final TokenStream tokens) {
        tokens.expect(Type.LT);
        final Token first = tokens.expect(Type.NAME);
        if (first.text().equalsIgnoreCase("AVP")) {
            final Token second = tokens.expect(Type.NAME);
            if (!second.text().equalsIgnoreCase("Header")) {
                throw new IllegalStateException(
                        "Expected 'Header' after 'AVP' at position " + second.position() + ", got '" + second.text() + "'");
            }
        } else if (!first.text().equalsIgnoreCase("AVP-Header")) {
            throw new IllegalStateException(
                    "Expected 'AVP Header' or 'AVP-Header' at position " + first.position() + ", got '" + first.text() + "'");
        }
        tokens.expect(Type.COLON);
        final long avpCode = Long.parseLong(tokens.expect(Type.NUMBER).text());
        long vendor = 0L;
        if (tokens.peek().type() == Type.COMMA) {
            tokens.next();
        }
        if (tokens.peek().type() == Type.NUMBER) {
            vendor = Long.parseLong(tokens.next().text());
        }
        tokens.expect(Type.GT);
        return new Header(avpCode, vendor);
    }
}
