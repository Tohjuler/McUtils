package dk.tohjuler.mcutils.kami.storage;

import java.util.Optional;
import java.util.function.Function;

public class TypeItem<BASE> {
    private static final TypeItem<?> EMPTY = new TypeItem<>(null);

    private final BASE value;

    public TypeItem(BASE value) {
        this.value = value;
    }

    // Getters

    /**
     * Gets the plane value.
     * <br>
     *
     * @return The plane value.
     */
    public BASE getPlane() {
        return value;
    }

    /**
     * Gets the value.
     * <br>
     *
     * @param <T> The type to cast to.
     * @return The value.
     */
    @SuppressWarnings("unchecked")
    public <T> T get() {
        return (T) value;
    }

    /**
     * Casts the value to the provided type.
     * <br>
     *
     * @param type The type to cast to.
     * @param <T>  The type to cast to.
     * @return The cast value.
     */
    public <T> T cast(Class<T> type) {
        return type.cast(value);
    }

    /**
     * Gets the value as a string.
     * <br>
     *
     * @param defaultValue The default value to return if the value is null.
     * @return The value as a string.
     */
    public String asString(String defaultValue) {
        if (value == null) return defaultValue;
        return value.toString();
    }

    /**
     * Gets the value as a string.
     * <br>
     *
     * @return The value as a string.
     */
    public String asString() {
        return asString(null);
    }

    /**
     * Gets the value as an integer.
     * <br>
     *
     * @param defaultValue The default value to return if the value is null or fails to be converted.
     * @return The value as an integer.
     */
    public int asInt(int defaultValue) {
        if (value == null) return defaultValue;
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Gets the value as an integer.
     * <br>
     *
     * @return The value as an integer.
     */
    public int asInt() {
        return asInt(0);
    }

    /**
     * Gets the value as a long.
     * <br>
     *
     * @param defaultValue The default value to return if the value is null or fails to be converted.
     * @return The value as a long.
     */
    public long asLong(long defaultValue) {
        if (value == null) return defaultValue;
        try {
            return Long.parseLong(value.toString());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Gets the value as a long.
     * <br>
     *
     * @return The value as a long.
     */
    public long asLong() {
        return asLong(0);
    }

    /**
     * Gets the value as a double.
     * <br>
     *
     * @param defaultValue The default value to return if the value is null or fails to be converted.
     * @return The value as a double.
     */
    public double asDouble(double defaultValue) {
        if (value == null) return defaultValue;
        try {
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Gets the value as a double.
     * <br>
     *
     * @return The value as a double.
     */
    public double asDouble() {
        return asDouble(0);
    }

    /**
     * Gets the value as a boolean.
     * <br>
     *
     * @param defaultValue The default value to return if the value is null or fails to be converted.
     * @return The value as a boolean.
     */
    public boolean asBoolean(boolean defaultValue) {
        if (value == null) return defaultValue;
        try {
            return Boolean.parseBoolean(value.toString());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Gets the value as a boolean.
     * <br>
     *
     * @return The value as a boolean.
     */
    public boolean asBoolean() {
        return asBoolean(false);
    }

    /**
     * Maps the value to another type.
     * <br>
     *
     * @param mapper The mapper function.
     * @param <T>    The type to map to.
     * @return The mapped value.
     */
    public <T> T map(Function<BASE, T> mapper) {
        return mapper.apply(value);
    }

    /**
     * Converts the value to an optional.
     * <br>
     *
     * @return The optional value.
     */
    public Optional<BASE> toOptional() {
        return Optional.ofNullable(value);
    }

    /**
     * Checks if the value is present.
     * <br>
     *
     * @return If the value is present.
     */
    public boolean isPresent() {
        return value != null;
    }

    // Statics

    /**
     * Get an empty type item.
     * <br>
     *
     * @param <T> The type of the type item.
     * @return The empty type item.
     */
    @SuppressWarnings("unchecked")
    public static <T> TypeItem<T> empty() {
        return (TypeItem<T>) EMPTY;
    }
}
