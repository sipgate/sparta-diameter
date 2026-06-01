package com.sipgate.sparta.diameter.base;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPDefinition;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;
import com.sipgate.sparta.diameter.spec.AvpDef;
import com.sipgate.sparta.diameter.spec.AvpFlagRule;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.reflections.Reflections;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.abort;

/**
 * Reusable JUnit 5 test class that verifies a module's AVP mixin accessors against an RFC AVP table.
 */
public abstract class AvpSpecTestBase {

    protected abstract String mixinsPackage();

    protected abstract Collection<AVPDefinition> getDefinitions();

    protected static Stream<Arguments> named(final Stream<AvpDef> defs) {
        return defs.map(d -> Arguments.of(Named.of(d.attributeName(), d)));
    }

    protected int exampleEnumValueFor(final AvpDef def) {
        return 42;
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideAvpDefs")
    void it_defines_all_avp_accessors(final AvpDef def) {
        final String base = methodBase(def.attributeName());
        final Class<AVPContainer> single = tryLoad(mixinsPackage() + "." + singleMixinName(base));
        final Class<AVPContainer> multi = tryLoad(mixinsPackage() + "." + multiMixinName(base));
        if (single == null && multi == null) {
            abort("AVP mixin is not implemented");
            return;
        }

        final Type<?> shape = shapeOf(def);
        if (single != null) {
            verifySingleAccessors(single.getMethods(), base, shape, def.attributeName());
        }
        if (multi != null) {
            verifyMultiAccessors(multi.getMethods(), base, shape, def.attributeName());
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideAvpDefs")
    void it_roundtrips_all_avps(final AvpDef def) throws Throwable {
        final String base = methodBase(def.attributeName());
        final Class<AVPContainer> single = tryLoad(mixinsPackage() + "." + singleMixinName(base));
        final Class<AVPContainer> multi = tryLoad(mixinsPackage() + "." + multiMixinName(base));
        if (single == null && multi == null) {
            abort("AVP mixin is not implemented");
            return;
        }

        final Type<?> shape = shapeOf(def);
        final boolean grouped = "Grouped".equals(def.valueType());

        if (single != null) {
            final AVPContainer container = newContainer(single);
            final var name = getSingleName(base);
            single.getMethod("set" + name, shape.setterParam()).invoke(container, shape.value());
            assertStoredAvp(container, def);
            final Object got = single.getMethod("get" + name).invoke(container);
            if (grouped) {
                assertThat(got).as(def.attributeName()).isNotNull();
            } else {
                assertThat(got).as(def.attributeName()).isEqualTo(shape.value());
            }
        }

        if (multi != null) {
            final AVPContainer container = newContainer(multi);
            multi.getMethod("add" + base, shape.setterParam()).invoke(container, shape.value());
            assertStoredAvp(container, def);
            final List<?> list = (List<?>) multi.getMethod("get" + pluralize(base)).invoke(container);
            assertThat(list).as(def.attributeName()).hasSize(1);
            if (grouped) {
                assertThat(list.get(0)).as(def.attributeName()).isNotNull();
            } else {
                assertThat(list.get(0)).as(def.attributeName()).isEqualTo(shape.value());
            }
        }
    }

    @ParameterizedTest
    @MethodSource("provideAvpDefs")
    void it_defines_all_avps(final AvpDef avpDef) {
        final var definitions = getDefinitions()
            .stream()
            .filter(d -> avpDef.attributeName().equals(d.name()))
            .toList();

        if (definitions.isEmpty()) {
            abort("AVP not defined");
            return;
        }

        assertThat(definitions).hasSize(1);
        final var definition = definitions.get(0);
        assertThat(definition.code()).isEqualTo(avpDef.avpCode());
        assertThat(definition.name()).isEqualTo(avpDef.attributeName());
        assertFlag(definition.mandatory(), avpDef.mandatoryBit(), avpDef.attributeName(), "M");
        assertFlag(definition.vendorSpecific(), avpDef.vendorSpecificBit(), avpDef.attributeName(), "V");
        assertThat(definition.vendorId()).isEqualTo(avpDef.vendorId());

        if ("Grouped".equals(avpDef.valueType())) {
            assertThat(definition.dataType()).isEqualTo(GroupedAVP.class);
        } else {
            final var shape = shapeOf(avpDef);
            // accepts Inet4Address for InetAddress
            assertThat(definition.dataType()).isAssignableFrom(shape.value.getClass());
        }
    }

    protected static Set<AvpDef> loadAvpDefs(final String resourceName) {
        final ObjectMapper mapper = JsonMapper.builder().build();
        try (final InputStream in = AvpSpecTestBase.class.getResourceAsStream(resourceName)) {
            if (in == null) {
                throw new IllegalStateException(resourceName + " not found on the test classpath");
            }
            return mapper.readValue(in, new TypeReference<Set<AvpDef>>() {
            });
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void assertStoredAvp(final AVPContainer container, final AvpDef def) {
        final var avps = container.findAVPs(new AVPKey(def.avpCode(), def.vendorId()));
        assertThat(avps).as("%s no AVP stored after setter", def.attributeName()).hasSize(1);
        final AVP stored = avps.get(0);
        assertThat(stored.getCode()).as("%s code", def.attributeName()).isEqualTo(def.avpCode());
        assertFlag(stored.isMandatory(), def.mandatoryBit(), def.attributeName(), "M");
        assertFlag(stored.isVendorSpecific(), def.vendorSpecificBit(), def.attributeName(), "V");
    }

    private static void assertFlag(final boolean actual, final AvpFlagRule rule, final String avpName, final String bitName) {
        switch (rule) {
            case MUST -> assertThat(actual).as("%s %s bit must be set", avpName, bitName).isTrue();
            case MUST_NOT -> assertThat(actual).as("%s %s bit must not be set", avpName, bitName).isFalse();
            case MAY, SHOULD_NOT -> { /* advisory — no assertion */ }
        }
    }

    /**
     * Proxy implementing {@code mixin} backed by an in-memory AVP list. Default methods run as-is;
     * the four {@link AVPContainer} methods route to the list.
     */
    private static AVPContainer newContainer(final Class<AVPContainer> mixin) {
        final List<AVP> avps = new ArrayList<>();
        final AVPContainer backing = new AVPContainer() {
            @Override
            public void addAVP(final AVP avp) {
                avps.add(avp);
            }

            @Override
            public void setAVP(final AVP avp) {
                avps.removeIf(a -> a.isSameKey(avp));
                avps.add(avp);
            }

            @Override
            public AVP findAVP(final AVPKey key) {
                for (final AVP a : avps) {
                    if (a.isSameKey(key)) {
                        return a;
                    }
                }

                return null;
            }

            @Override
            public List<AVP> findAVPs(final AVPKey key) {
                final List<AVP> result = new ArrayList<>();
                for (final AVP a : avps) {
                    if (a.isSameKey(key)) {
                        result.add(a);
                    }
                }

                return result;
            }
        };
        return (AVPContainer) Proxy.newProxyInstance(
            mixin.getClassLoader(),
            new Class<?>[]{mixin},
            (proxy, method, args) -> method.isDefault()
                ? InvocationHandler.invokeDefault(proxy, method, args)
                : method.invoke(backing, args));
    }

    /**
     * Asserts a single (flat) setter and getter exist on {@code methods}, name only.
     */
    protected static void verifySingleAccessors(final Method[] methods, final String base, final String label) {
        assertThat(methods).as("%s missing flat getter get%s", label, base).anyMatch(m ->
            m.getName().equals("get" + base) && m.getParameterCount() == 0);
        assertThat(methods).as("%s missing flat setter set%s", label, base).anyMatch(m ->
            m.getName().equals("set" + base) && m.getParameterCount() == 1);
    }

    /**
     * Asserts a single (flat) setter and getter exist on {@code methods}, name, parameter and return types.
     */
    protected static void verifySingleAccessors(final Method[] methods, final String base,
                                                final Type<?> shape, final String label) {
        final var name = getSingleName(base);
        assertThat(methods).as("%s missing set%s(%s)", label, name, shape.setterParam.getSimpleName()).anyMatch(m ->
            m.getName().equals("set" + name)
            && m.getParameterCount() == 1
            && m.getParameterTypes()[0] == shape.setterParam);
        assertThat(methods).as("%s missing get%s(): %s", label, name, shape.getterReturn.getSimpleName()).anyMatch(m ->
            m.getName().equals("get" + name)
            && m.getParameterCount() == 0
            && shape.getterReturn.isAssignableFrom(m.getReturnType()));
    }

    private static String getSingleName(final String base) {
        // "Class" single accessors use the AVP suffix (setClassAVP / getClassAVP) to avoid the
        // Object.getClass() collision; every other base maps directly to set<base> / get<base>.
        return "Class".equals(base) ? "ClassAVP" : base;
    }

    /**
     * Asserts multi (list) accessors exist on {@code methods}, name only.
     */
    protected static void verifyMultiAccessors(final Method[] methods, final String base, final String label) {
        final String plural = pluralize(base);
        assertThat(methods).as("%s missing list getter get%s returning List", label, plural).anyMatch(m ->
            m.getName().equals("get" + plural)
            && m.getParameterCount() == 0
            && List.class.isAssignableFrom(m.getReturnType()));
        assertThat(methods).as("%s missing bulk setter addAll%s accepting Collection", label, plural).anyMatch(m ->
            m.getName().equals("addAll" + plural)
            && m.getParameterCount() == 1
            && Collection.class.isAssignableFrom(m.getParameterTypes()[0]));
    }

    /**
     * Asserts multi (list) accessors exist on {@code methods}. name, types.
     */
    protected static void verifyMultiAccessors(final Method[] methods, final String base,
                                               final Type<?> shape, final String label) {
        final String plural = pluralize(base);
        assertThat(methods).as("%s missing add%s(%s)", label, base, shape.setterParam.getSimpleName()).anyMatch(m ->
            m.getName().equals("add" + base)
            && m.getParameterCount() == 1
            && m.getParameterTypes()[0] == shape.setterParam);
        assertThat(methods).as("%s missing get%s(): List", label, plural).anyMatch(m ->
            m.getName().equals("get" + plural)
            && m.getParameterCount() == 0
            && List.class.isAssignableFrom(m.getReturnType()));
        assertThat(methods).as("%s missing addAll%s(Collection)", label, plural).anyMatch(m ->
            m.getName().equals("addAll" + plural)
            && m.getParameterCount() == 1
            && Collection.class.isAssignableFrom(m.getParameterTypes()[0]));
    }

    /**
     * Setter parameter type, getter return type, and a concrete example value the codebase uses
     * for a given RFC value type.
     */
    protected record Type<S>(Class<S> setterParam, Class<?> getterReturn, S value) {
    }

    private Type<?> shapeOf(final AvpDef def) {
        return switch (def.valueType()) {
            case "OctetString" ->
                new Type<>(byte[].class, byte[].class, new byte[]{'o', 'c', 't', 'e', 't', '-', 's', 't', 'r', 'i', 'n', 'g'});
            case "Integer32" -> new Type<>(int.class, int.class, -32);
            case "Integer64" -> new Type<>(long.class, long.class, -64L);
            case "Unsigned32" -> new Type<>(long.class, long.class, 32L);
            case "Unsigned64" -> new Type<>(BigInteger.class, BigInteger.class, new BigInteger("64"));
            case "Float32" -> new Type<>(float.class, float.class, 32f);
            case "Float64" -> new Type<>(double.class, double.class, 64d);
            case "Address" -> new Type<>(InetAddress.class, InetAddress.class, Inet4Address.getLoopbackAddress());
            // must be seconds, without milliseconds, because diameter doesn't have millis
            case "Time" -> new Type<>(Date.class, Date.class, Date.from(Instant.ofEpochSecond(1780090087)));
            case "UTF8String" -> new Type<>(String.class, String.class, "UTF-8");
            case "DiameterIdentity", "DiamIdent" -> new Type<>(String.class, String.class, "diameter-identity");
            case "DiameterURI", "DiamURI" -> new Type<>(String.class, String.class, "diameter-uri");
            case "Enumerated" -> new Type<>(int.class, int.class, exampleEnumValueFor(def));
            case "Grouped" -> new Type<>(List.class, AVPContainer.class, List.<AVP>of());
            default -> throw new IllegalArgumentException("Unknown valueType: " + def.valueType());
        };
    }

    protected static String methodBase(final String avpName) {
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

    protected static String pluralize(final String base) {
        if (base.endsWith("ss")) {
            return base + "es"; // "Address" -> "Addresses", "Class" -> "Classes"
        }
        if (base.endsWith("s")) {
            return base; // "Features" already plural
        }
        return base + "s";
    }

    protected static String singleMixinName(final String base) {
        return base.endsWith("AVP") ? "Has" + base : "Has" + base + "AVP";
    }

    protected static String multiMixinName(final String base) {
        return base.endsWith("AVP") ? "Has" + base + "s" : "Has" + base + "AVPs";
    }

    private static Class<AVPContainer> tryLoad(final String fqcn) {
        try {
            return (Class<AVPContainer>) Class.forName(fqcn);
        } catch (final ClassNotFoundException e) {
            return null;
        }
    }
}
