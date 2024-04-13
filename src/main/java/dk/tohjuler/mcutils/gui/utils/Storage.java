package dk.tohjuler.mcutils.gui.utils;

import dk.tohjuler.mcutils.config.ConfigurationFile;

import java.util.HashMap;
import java.util.Map;

public class Storage {
    private final Map<String, Object> vars = new HashMap<>();

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
        cf.cf().getConfigurationSection(path).getKeys(false).forEach(key -> vars.put(key, cf.cf().get(path + "." + key)));
    }
}
