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
public class Storage {
    private final Map<String, Object> vars = new HashMap<>();

    public Storage(Storage storage) {
        vars.putAll(storage.vars);
    }

    public Storage() {
    }

    public void set(String key, Object value) {
        vars.put(key, value);
    }

    public Object get(String key) {
        return vars.get(key);
    }

    public void remove(String key) {
        vars.remove(key);
    }

    public boolean contains(String key) {
        return vars.containsKey(key);
    }

    public void clear() {
        vars.clear();
    }

    public void save(ConfigurationFile cf, String path) {
        vars.forEach((key, value) -> cf.cf().set(path + "." + key, value));
    }

    public void load(ConfigurationFile cf, String path) {
        vars.clear();
        if (cf.cf().isSet(path))
            cf.cf().getConfigurationSection(path).getKeys(false).forEach(key -> vars.put(key, cf.cf().get(path + "." + key)));
    }
}
