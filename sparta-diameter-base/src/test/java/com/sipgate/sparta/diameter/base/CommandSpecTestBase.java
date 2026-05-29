package com.sipgate.sparta.diameter.base;

import com.sipgate.sparta.diameter.base.core.Command;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.spec.AvpRule;
import com.sipgate.sparta.diameter.spec.CommandDef;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Reusable JUnit 5 test class that verifies a module's command implementations against their CCFs.
 */
public abstract class CommandSpecTestBase {

    private static final HopByHopId HOP = new HopByHopId(0);
    private static final EndToEndId END = new EndToEndId(0);

    protected abstract String messagesPackage();

    protected String classNameFor(final String commandName) {
        final String[] parts = commandName.split("-");
        final StringBuilder sb = new StringBuilder();
        for (final String part : parts) {
            sb.append(Character.toUpperCase(part.charAt(0)));
            sb.append(part.substring(1).toLowerCase());
        }
        return sb.toString();
    }

    protected long expectedApplicationId(final CommandDef def) {
        return def.applicationId();
    }

    protected static Stream<Arguments> named(final Set<CommandDef> defs) {
        return defs.stream().map(d -> Arguments.of(Named.of(d.commandName(), d)));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideCommandDefs")
    void it_defines_all_commands(final CommandDef def) throws Exception {
        // GIVEN
        final String simpleName = classNameFor(def.commandName());
        final Class<?> outer = Class.forName(messagesPackage() + "." + simpleName);

        // WHEN
        final Command in = newIn(outer, def.isRequest());
        final Command out = newOut(outer, def.isRequest());

        // THEN
        assertHeader(in, def, simpleName + "$In");
        assertHeader(out, def, simpleName + "$Out");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideCommandDefs")
    void it_defines_all_command_avps(final CommandDef def) throws Exception {
        final String simpleName = classNameFor(def.commandName());
        final Class<?> in = Class.forName(messagesPackage() + "." + simpleName + "$In");
        final Method[] methods = in.getMethods();

        for (final AvpRule rule : def.fixed()) {
            verifyAvpAccessors(methods, rule, def.commandName());
        }
        for (final AvpRule rule : def.required()) {
            verifyAvpAccessors(methods, rule, def.commandName());
        }
        for (final AvpRule rule : def.optional()) {
            verifyAvpAccessors(methods, rule, def.commandName());
        }
    }

    private void assertHeader(final Command command, final CommandDef def, final String label) {
        assertThat(command.getCommandCode()).as("%s commandCode", label).isEqualTo(def.commandId());
        assertThat((long) command.getApplicationId()).as("%s applicationId", label).isEqualTo(expectedApplicationId(def));
        assertThat(command.isRequest()).as("%s isRequest", label).isEqualTo(def.isRequest());
        assertThat(command.isProxiable()).as("%s isProxiable", label).isEqualTo(def.isProxiable());
        assertThat(command.isError()).as("%s isError", label).isEqualTo(def.isError());
    }

    private static void verifyAvpAccessors(final Method[] methods, final AvpRule rule, final String commandName) {
        if ("AVP".equals(rule.avpName())) {
            return; // *[ AVP ] is the wildcard catch-all from RFC 6733 §4.4, not a typed AVP
        }

        final String base = methodBase(rule.avpName());
        final boolean multi = rule.max() > 1;
        final String tag = commandName + ":" + rule.avpName();

        if (multi) {
            final String plural = pluralize(base);
            assertThat(methods).as("%s missing list getter get%s returning List", tag, plural).anyMatch(m ->
                m.getName().equals("get" + plural)
                    && m.getParameterCount() == 0
                    && List.class.isAssignableFrom(m.getReturnType()));
            assertThat(methods).as("%s missing bulk setter addAll%s accepting Collection", tag, plural).anyMatch(m ->
                m.getName().equals("addAll" + plural)
                    && m.getParameterCount() == 1
                    && Collection.class.isAssignableFrom(m.getParameterTypes()[0]));
        } else {
            assertThat(methods).as("%s missing flat getter get%s", tag, base).anyMatch(m ->
                m.getName().equals("get" + base) && m.getParameterCount() == 0);
            assertThat(methods).as("%s missing flat setter set%s", tag, base).anyMatch(m ->
                m.getName().equals("set" + base) && m.getParameterCount() == 1);
        }
    }

    private static String pluralize(final String base) {
        if (base.endsWith("ss")) {
            return base + "es"; // "Address" -> "Addresses", "Class" -> "Classes"
        }
        if (base.endsWith("s")) {
            return base; // "Features" already plural
        }
        return base + "s";
    }

    private static String methodBase(final String avpName) {
        final String[] parts = avpName.split("-");
        final StringBuilder sb = new StringBuilder();
        for (final String part : parts) {
            if ("AVP".equals(part)) {
                sb.append("AVP");
            } else {
                sb.append(Character.toUpperCase(part.charAt(0)));
                sb.append(part.substring(1).toLowerCase());
            }
        }
        return sb.toString();
    }

    private static Command newIn(final Class<?> outer, final boolean isRequest) throws Exception {
        final Class<?> in = Class.forName(outer.getName() + "$In");
        if (isRequest) {
            final Constructor<?> ctor = in.getDeclaredConstructor(HopByHopId.class, EndToEndId.class, boolean.class);
            ctor.setAccessible(true);
            return (Command) ctor.newInstance(HOP, END, false);
        }
        final Constructor<?> ctor = in.getDeclaredConstructor(HopByHopId.class, EndToEndId.class);
        ctor.setAccessible(true);
        return (Command) ctor.newInstance(HOP, END);
    }

    private static Command newOut(final Class<?> outer, final boolean isRequest) throws Exception {
        final Class<?> out = Class.forName(outer.getName() + "$Out");
        if (isRequest) {
            final Constructor<?> ctor = out.getDeclaredConstructor();
            ctor.setAccessible(true);
            return (Command) ctor.newInstance();
        }
        final Constructor<?> ctor = out.getDeclaredConstructor(HopByHopId.class, EndToEndId.class);
        ctor.setAccessible(true);
        return (Command) ctor.newInstance(HOP, END);
    }
}
