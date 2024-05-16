package dk.tohjuler.mcutils.gui.utils;

import dk.tohjuler.mcutils.config.ConfigurationFile;

import java.util.HashMap;
import java.util.Map;

/**
 * The storage is used to store variables that can be used in the gui.
 * The vars can be loaded in from the config, witch is saved by the vars from the constructor of the gui.
 * When the gui is opened a new storage is created, witch is a copy of the storage from the gui.
 * Just remember that the original storage is not cloned it is just a reference to the storage from the gui.
 * So be careful when you change the original vars in the storage.
 */
public class Storage implements IStorage {
    private final Map<String, Object> vars = new HashMap<>();

    public Storage(Storage storage) {
        vars.putAll(storage.vars);
    }

    public Storage() {
    }

    /**
     * Set a variable in the storage.
     * <p>
     *
     * @param key   The key of the variable
     * @param value The value of the variable
     * @since 1.5.0
     */
    public void set(String key, Object value) {
        vars.put(key, value);
    }

    /**
     * Get a variable from the storage.
     * <p>
     *
     * @param key The key of the variable
     * @return The value of the variable
     * @since 1.5.0
     */
    public Object get(String key) {
        return vars.get(key);
    }

    /**
     * Get a variable from the storage with a default value.
     * <p>
     *
     * @param key The key of the variable
     * @param def The default value
     * @return The value of the variable
     * @since 1.13.0
     */
    public Object get(String key, Object def) {
        return vars.getOrDefault(key, def);
    }

    /**
     * Remove a variable from the storage.
     * <p>
     *
     * @param key The key of the variable
     * @since 1.5.0
     */
    public void remove(String key) {
        vars.remove(key);
    }

    /**
     * Check if the storage contains a variable.
     * <p>
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
     * <p>
     *
     * @since 1.5.0
     */
    public void clear() {
        vars.clear();
    }

    /**
     * Save the storage to a config file.
     * <p>
     *
     * @param cf   The config file to save to
     * @param path The path to save the storage to
     * @since 1.5.0
     */
    public void save(ConfigurationFile cf, String path) {
        vars.forEach((key, value) -> cf.cf().set(path + "." + key, value));
    }

    /**
     * Load the storage from a config file.
     * <p>
     *
     * @param cf   The config file to load from
     * @param path The path to load the storage from
     * @since 1.5.0
     */
    public void load(ConfigurationFile cf, String path) {
        vars.clear();
        if (cf.cf().isSet(path))
            cf.cf().getConfigurationSection(path).getKeys(false).forEach(key -> vars.put(key, cf.cf().get(path + "." + key)));
    }
}
