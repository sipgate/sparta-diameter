package com.sipgate.sparta.diameter.core.avp;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Factory for creating AVPs with automatic type deduction and flag handling.
 * Provides overloaded create methods for different value types and automatically
 * applies the correct flags based on registered AVP definitions.
 */
public final class AVPFactory {

    private static final Map<Integer, AVPDefinition> registry = new ConcurrentHashMap<>();

    static {
        // Register core protocol AVPs by default
        registerProvider(new CoreAVPProvider());
    }

    /**
     * Registers all AVP definitions from the given provider.
     *
     * @param provider The AVP provider to register
     */
    public static void registerProvider(final AVPProvider provider) {
        for (final AVPDefinition definition : provider.getDefinitions()) {
            registry.put(definition.code(), definition);
        }
    }

    /**
     * Creates an AVP with an integer value.
     *
     * @param avpCode The AVP code constant from DiameterConstants
     * @param value   The integer value
     * @return The created AVP with appropriate flags
     * @throws IllegalArgumentException if AVP code is unknown or type mismatch
     */
    public static AVP create(final int avpCode, final int value) {
        final AVPDefinition definition = getDefinition(avpCode);
        validateType(definition, Integer.class);
        return AVP.createIntegerAVP(avpCode, definition.mandatory(), value);
    }

    /**
     * Creates an AVP with a long value.
     *
     * @param avpCode The AVP code constant from DiameterConstants
     * @param value   The long value
     * @return The created AVP with appropriate flags
     * @throws IllegalArgumentException if AVP code is unknown or type mismatch
     */
    public static AVP create(final int avpCode, final long value) {
        final AVPDefinition definition = getDefinition(avpCode);
        validateType(definition, Long.class);
        return AVP.createLongAVP(avpCode, definition.vendorSpecific(), value);
    }

    /**
     * Creates an AVP with a string value.
     *
     * @param avpCode The AVP code constant from DiameterConstants
     * @param value   The string value
     * @return The created AVP with appropriate flags
     * @throws IllegalArgumentException if AVP code is unknown or type mismatch
     */
    public static AVP create(final int avpCode, final String value) {
        final AVPDefinition definition = getDefinition(avpCode);
        validateType(definition, String.class);
        return new AVP(avpCode, definition.vendorSpecific(), definition.mandatory(),
                      false, definition.vendorId(), value.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Creates an AVP with a byte array value.
     *
     * @param avpCode The AVP code constant from DiameterConstants
     * @param value   The byte array value
     * @return The created AVP with appropriate flags
     * @throws IllegalArgumentException if AVP code is unknown or type mismatch
     */
    public static AVP create(final int avpCode, final byte[] value) {
        final AVPDefinition definition = getDefinition(avpCode);
        validateType(definition, byte[].class);
        return new AVP(avpCode, definition.vendorSpecific(), definition.mandatory(),
                      false, definition.vendorId(), value);
    }

    /**
     * Gets the definition for the given AVP code.
     *
     * @param avpCode The AVP code
     * @return The AVP definition
     * @throws IllegalArgumentException if AVP code is not registered
     */
    private static AVPDefinition getDefinition(final int avpCode) {
        final AVPDefinition definition = registry.get(avpCode);
        if (definition == null) {
            throw new IllegalArgumentException("Unknown AVP code: " + avpCode +
                ". Make sure the appropriate AVPProvider is registered.");
        }
        return definition;
    }

    /**
     * Validates that the provided value type matches the expected AVP type.
     *
     * @param definition The AVP definition
     * @param valueType  The actual value type
     * @throws IllegalArgumentException if types don't match
     */
    private static void validateType(final AVPDefinition definition, final Class<?> valueType) {
        if (!definition.dataType().equals(valueType)) {
            throw new IllegalArgumentException(String.format(
                "Type mismatch for AVP %s (code %d): expected %s, got %s",
                definition.name(), definition.code(),
                definition.dataType().getSimpleName(), valueType.getSimpleName()));
        }
    }

    private AVPFactory() {
        // Utility class - no instantiation
    }
}
