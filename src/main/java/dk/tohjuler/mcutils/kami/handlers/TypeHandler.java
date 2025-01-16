package dk.tohjuler.mcutils.kami.handlers;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Represents a type handler.
 */
public class TypeHandler {
    private final Map<Class<?>, TypeAdapter<?>> typeAdapters = new HashMap<>();

    public TypeHandler() {
        registerTypeAdapter(String.class, Pattern.compile("^\"[^\"]*\"$"), str -> "\"" + str + "\"", str -> str.substring(1, str.length() - 1));
        registerTypeAdapter(Integer.class, Pattern.compile("^-?\\d+$"), Object::toString, Integer::parseInt);
        registerTypeAdapter(Double.class, Pattern.compile("^-?\\d+(\\.\\d+)?$"), Object::toString, Double::parseDouble);
        registerTypeAdapter(Boolean.class, Pattern.compile("^true|false$"), Object::toString, Boolean::parseBoolean);
        registerTypeAdapter(List.class,
                Pattern.compile("^\\[.*]$"),
                list -> "[" + list.stream().map(Object::toString).collect(Collectors.joining(", ")) + "]",
                str -> Arrays.stream(str.substring(1, str.length() - 1).split(",")).map(this::deserialize).collect(Collectors.toList())
        );
    }

    /**
     * Register a type adapter.
     * <br>
     *
     * @param clazz   The class to register the type adapter for.
     * @param adapter The type adapter to register.
     */
    public void registerTypeAdapter(Class<?> clazz, TypeAdapter<?> adapter) {
        typeAdapters.put(clazz, adapter);
    }

    /**
     * Register a type adapter, using a pattern and serializer/deserializer functions.
     * <br>
     *
     * @param clazz        The class to register the type adapter for.
     * @param pattern      The pattern to use for the type adapter.
     * @param serializer   The serializer function.
     * @param deserializer The deserializer function.
     * @param <T>          The type of the type adapter.
     */
    public <T> void registerTypeAdapter(Class<?> clazz, Pattern pattern, Function<T, String> serializer, Function<String, T> deserializer) {
        typeAdapters.put(clazz, new TypeAdapter<T>() {
            @Override
            public boolean isType(String str) {
                return pattern.matcher(str).matches();
            }

            @Override
            public String serialize(T obj) {
                return serializer.apply(obj);
            }

            @Override
            public T deserialize(String str) {
                return deserializer.apply(str);
            }
        });
    }

    /**
     * Get a type adapter.
     * <br>
     *
     * @param clazz The class to get the type adapter for.
     * @param <T>   The type of the type adapter.
     * @return The type adapter.
     */
    @SuppressWarnings("unchecked")
    public <T> TypeAdapter<T> getTypeAdapter(Class<T> clazz) {
        return (TypeAdapter<T>) typeAdapters.get(clazz);
    }

    /**
     * Serialize an object to a string.
     * <br>
     *
     * @param obj The object to serialize.
     * @return The serialized string.
     */
    @SuppressWarnings("unchecked")
    public String serialize(Object obj) {
        if (getTypeAdapter(obj.getClass()) == null) return obj.toString();

        TypeAdapter<Object> adapter = (TypeAdapter<Object>) getTypeAdapter(obj.getClass());
        if (adapter == null) return null;
        return adapter.serialize(obj);
    }

    /**
     * Deserialize a string to an object.
     * <br>
     *
     * @param clazz The class to serialize the string to.
     * @param str   The string to serialize.
     * @param <T>   The type of the object.
     * @return The serialized object.
     */
    public <T> T deserialize(Class<T> clazz, String str) {
        TypeAdapter<T> adapter = getTypeAdapter(clazz);
        if (adapter == null) return null;
        return adapter.deserialize(str);
    }

    /**
     * Deserialize an object to a string.
     * <br>
     *
     * @param clazz The class to deserialize the object to.
     * @param obj   The object to deserialize.
     * @param <T>   The type of the object.
     * @return The deserialized string.
     */
    public <T> String serialize(Class<T> clazz, T obj) {
        TypeAdapter<T> adapter = getTypeAdapter(clazz);
        if (adapter == null) return null;
        return adapter.serialize(obj);
    }

    /**
     * Deserialize a string to an object.
     * <br>
     *
     * @param str The string to deserialize.
     * @return The deserialized object.
     */
    public @Nullable Object deserialize(String str) {
        str = str.trim();
        for (TypeAdapter<?> adapter : typeAdapters.values())
            if (adapter.isType(str)) return adapter.deserialize(str);
        return null;
    }

    /**
     * Detect the class of a string.
     * <br>
     *
     * @param str The string to detect the class of.
     * @return The detected class.
     */
    public Class<?> detectClass(String str) {
        for (Map.Entry<Class<?>, TypeAdapter<?>> e : typeAdapters.entrySet())
            if (e.getValue().isType(str)) return e.getKey();
        return null;
    }

    public interface TypeAdapter<T> {

        /**
         * Check if the string is of the type.
         * <br>
         *
         * @param str The string to check.
         * @return If the string is of the type.
         */
        boolean isType(String str);

        /**
         * Deserialize the object to a string.
         * <br>
         *
         * @param obj The object to deserialize.
         * @return The deserialized string.
         */
        String serialize(T obj);

        /**
         * Serialize the string to an object.
         * <br>
         *
         * @param str The string to serialize.
         * @return The serialized object.
         */
        T deserialize(String str);
    }
}
