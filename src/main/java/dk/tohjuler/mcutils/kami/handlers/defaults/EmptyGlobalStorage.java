package dk.tohjuler.mcutils.kami.handlers.defaults;

import dk.tohjuler.mcutils.kami.handlers.IGlobalStorage;
import dk.tohjuler.mcutils.kami.storage.TypeItem;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Map;

/**
 * Empty global storage.
 */
public class EmptyGlobalStorage implements IGlobalStorage {
    @Override
    public void load(String id) {
    }

    @Override
    public void save() {
    }

    @Override
    public void set(String key, Object value) {
    }

    @Override
    public @NotNull TypeItem<Object> get(String key) {
        return new TypeItem<>(null);
    }

    @Override
    public void remove(String key) {
    }

    @Override
    public void clear() {
    }

    @Override
    public Map<String, TypeItem<Object>> getStorage() {
        return Collections.emptyMap();
    }
}
