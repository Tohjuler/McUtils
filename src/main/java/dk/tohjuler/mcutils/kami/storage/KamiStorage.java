package dk.tohjuler.mcutils.kami.storage;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a storage for Kami.
 * Used for storing values between expressions.
 */
@Getter
public class KamiStorage<KEY> {
    private final Map<KEY, TypeItem<Object>> storage = new HashMap<>();

    /**
     * Sets a value into the storage.
     * Alias for {@link #put(KEY, Object)}.
     * <br>
     *
     * @param key   The key.
     * @param value The value.
     */
    public void set(KEY key, Object value) {
        put(key, value);
    }

    /**
     * Puts a value into the storage.
     * <br>
     *
     * @param key   The key.
     * @param value The value.
     */
    public void put(KEY key, Object value) {
        storage.put(key, new TypeItem<>(value));
    }

    /**
     * Gets a value from the storage.
     * <br>
     *
     * @param key The key.
     * @return The value.
     */
    public @NotNull TypeItem<Object> get(KEY key) {
        if (!storage.containsKey(key)) return new TypeItem<>(null);
        return storage.get(key);
    }

    /**
     * Removes a value from the storage.
     * <br>
     *
     * @param key The key.
     */
    public void remove(KEY key) {
        storage.remove(key);
    }

    /**
     * Clears the storage.
     */
    public void clear() {
        storage.clear();
    }

    /**
     * Checks if the storage contains a key.
     * <br>
     *
     * @param key The key.
     * @return If the storage contains the key.
     */
    public boolean contains(KEY key) {
        return storage.containsKey(key);
    }

    /**
     * Gets the size of the storage.
     * <br>
     *
     * @return The size of the storage.
     */
    public int size() {
        return storage.size();
    }
}
