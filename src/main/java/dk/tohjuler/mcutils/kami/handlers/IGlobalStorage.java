package dk.tohjuler.mcutils.kami.handlers;

import dk.tohjuler.mcutils.kami.storage.TypeItem;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Represents a global storage.
 * <br>
 * A global storage is used to store global variables.
 * And need to be persistence.
 */
public interface IGlobalStorage {

    /**
     * Loads the storage, with the provided id.
     * <br>
     *
     * @param id The id.
     */
    void load(String id);

    /**
     * Saves the storage.
     */
    void save();

    /**
     * Sets a value in the storage.
     * <br>
     *
     * @param key   The key.
     * @param value The value.
     */
    void set(String key, Object value);

    /**
     * Gets a value from the storage.
     * <br>
     *
     * @param key The key.
     * @return The value.
     */
    @NotNull TypeItem<Object> get(String key);

    /**
     * Removes a value from the storage.
     * <br>
     *
     * @param key The key.
     */
    void remove(String key);

    /**
     * Clears the storage.
     */
    void clear();

    /**
     * Gets the storage.
     * <br>
     *
     * @return The storage.
     */
    Map<String, TypeItem<Object>> getStorage();

}
