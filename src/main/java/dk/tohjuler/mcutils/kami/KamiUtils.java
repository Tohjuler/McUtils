package dk.tohjuler.mcutils.kami;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Utility methods for Kami.
 */
public class KamiUtils {
    public static final Pattern OBJECT_REF_PATTERN = Pattern.compile("obj:\\{\\b[0-9a-fA-F]{8}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{12}\\b}");

    private static final Map<String, Map<String, Function<Object[], Object>>> methodOverrides = new HashMap<>();

    public static final List<Class<?>> PRINT_TYPES = Arrays.asList(
            String.class, Integer.class, Long.class, Double.class, Float.class, Boolean.class
    );

    static {
        addMethodOverride(
                ArrayList.class,
                "get",
                input -> ((List<?>) input[0]).get((int) input[1])
        );
        addMethodOverride(
                ArrayList.class,
                "size",
                input -> ((List<?>) input[0]).size()
        );
    }

    /**
     * Adds a method override.
     * This is used to override methods for specific classes.
     * <br>
     *
     * @param clazz      The class to override the method for.
     * @param methodName The name of the method to override.
     * @param function   The function to override the method with.
     */
    public static void addMethodOverride(Class<?> clazz, String methodName, Function<Object[], Object> function) {
        methodOverrides.computeIfAbsent(clazz.getSimpleName(), k -> new HashMap<>()).put(methodName, function);
    }

    /**
     * Checks if a method is overridden.
     * <br>
     *
     * @param clazz      The class to check the method for.
     * @param methodName The name of the method to check.
     * @return If the method is overridden.
     */
    public static boolean hasMethodOverridden(Class<?> clazz, String methodName) {
        return methodOverrides.get(clazz.getSimpleName()) != null && methodOverrides.get(clazz.getSimpleName()).containsKey(methodName);
    }

    /**
     * Checks if a method is overridden.
     * <br>
     *
     * @param clazz      The class to check the method for.
     * @param methodName The name of the method to check.
     * @param input      The input to check the method with. First element is the object, the rest are the parameters.
     * @return The overridden method, or null if not overridden.
     */
    public static @Nullable Object runMethodOverride(Class<?> clazz, String methodName, Object... input) {
        Map<String, Function<Object[], Object>> overrides = methodOverrides.get(clazz.getSimpleName());
        if (overrides == null) return null;

        Function<Object[], Object> override = overrides.get(methodName);
        if (override == null) return null;

        return override.apply(input);
    }

    /**
     * Parses a string into an object.
     * <br>
     * Types:
     * - String: ".*"
     * - Boolean: true|false
     * - Integer: -?\d+
     * - Double: -?\d+\.\d+
     * - List: \[.*]
     * <br>
     *
     * @param input The input to parse.
     * @return The parsed object.
     */
    public static @Nullable Object parseObject(String input) {
        if (input.matches("\".*\"")) {
            return input.substring(1, input.length() - 1);
        } else if (input.matches("true|false")) {
            return Boolean.parseBoolean(input);
        } else if (input.matches("-?\\d+")) {
            return Integer.parseInt(input);
        } else if (input.matches("-?\\d+\\.\\d+")) {
            return Double.parseDouble(input);
        } else if (input.matches("\\[.*]")) {
            return Arrays.stream(input.substring(1, input.length() - 1).split(",")).map(KamiUtils::parseObject).collect(Collectors.toList());
        } else {
            return null;
        }
    }

    /**
     * Determines the class of a parameter.
     * <br>
     *
     * @param param The parameter to determine the class of.
     * @return The class of the parameter.
     */
    public static Class<?> determineClass(String param) {
        if (param.matches("\".*\"")) {
            return String.class;
        } else if (param.matches("true|false")) {
            return boolean.class;
        } else if (param.matches("-?\\d+")) {
            return int.class;
        } else if (param.matches("-?\\d+\\.\\d+")) {
            return double.class;
        } else if (param.matches("\\[.*]")) {
            return List.class;
        } else {
            return null;
        }
    }

    /**
     * Gets a method from a class.
     * <br>
     *
     * @param clazz      The class to get the method from.
     * @param methodName The name of the method.
     * @return The method.
     */
    public static @Nullable Method getMethod(Class<?> clazz, String methodName, Class<?>... params) {
        Class<?> current = clazz;
        // Check superclasses
        do {
            try {
                return current.getDeclaredMethod(methodName, params);
            } catch (NoSuchMethodException ignored) {
            }

            // Check interfaces
            for (Class<?> iface : current.getInterfaces()) {
                Method method = getMethod(iface, methodName, params);
                if (method != null) return method;
            }
        } while ((current = current.getSuperclass()) != null);

        return null;
    }
}
