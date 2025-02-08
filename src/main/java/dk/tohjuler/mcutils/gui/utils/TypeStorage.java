package dk.tohjuler.mcutils.gui.utils;

import dk.tohjuler.mcutils.config.ConfigurationFile;
import dk.tohjuler.mcutils.kami.storage.TypeItem;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * A copy of the {@link Storage} class, but with the {@link TypeItem} class.
 */
public class TypeStorage implements IStorage {
    private final Map<String, TypeItem<Object>> vars = new HashMap<>();

    public TypeStorage(TypeStorage storage) {
        if (storage == null || storage.vars.isEmpty()) return;
        vars.putAll(storage.vars);
    }

    public TypeStorage() {
    }

    /**
     * Set a variable in the storage.
     * <br/>
     *
     * @param key   The key of the variable
     * @param value The value of the variable
     * @since 1.5.0
     */
    public void set(String key, Object value) {
        vars.put(key, new TypeItem<>(value));
    }

    /**
     * Get a variable from the storage.
     * <br/>
     *
     * @param key The key of the variable
     * @return The value of the variable
     * @since 1.5.0
     */
    public @NotNull TypeItem<Object> get(String key) {
        return vars.getOrDefault(key, TypeItem.empty());
    }

    /**
     * Get a variable from the storage with a default value.
     * <br/>
     *
     * @param key The key of the variable
     * @param def The default value
     * @return The value of the variable
     * @since 1.13.0
     */
    public @NotNull TypeItem<Object> get(String key, Object def) {
        return vars.getOrDefault(key, new TypeItem<>(def));
    }

    /**
     * Remove a variable from the storage.
     * <br/>
     *
     * @param key The key of the variable
     * @since 1.5.0
     */
    public void remove(String key) {
        vars.remove(key);
    }

    /**
     * Check if the storage contains a variable.
     * <br/>
     *
     * @param key The key of the variable
     * @return If the storage contains the variable
     * @since 1.5.0
     */
    public boolean contains(String key) {
        return vars.containsKey(key);
    }

    /**
     * Clear the storage.
     * <br/>
     *
     * @since 1.5.0
     */
    public void clear() {
        vars.clear();
    }

    /**
     * Save the storage to a config file.
     * <br/>
     *
     * @param cf   The config file to save to
     * @param path The path to save the storage to
     * @since 1.5.0
     */
    public void save(ConfigurationFile cf, String path) {
        vars.forEach((key, value) -> cf.cf().set(path + "." + key, value.getPlane()));
    }

    /**
     * Load the storage from a config file.
     * <br/>
     *
     * @param cf   The config file to load from
     * @param path The path to load the storage from
     * @since 1.5.0
     */
    public void load(ConfigurationFile cf, String path) {
        vars.clear();
        if (cf.cf().isSet(path))
            cf.cf().getConfigurationSection(path).getKeys(false).forEach(key -> vars.put(key, new TypeItem<>(cf.cf().get(path + "." + key))));
    }
}
