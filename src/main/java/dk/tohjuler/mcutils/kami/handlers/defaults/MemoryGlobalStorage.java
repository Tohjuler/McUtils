package dk.tohjuler.mcutils.kami.handlers.defaults;

import dk.tohjuler.mcutils.kami.handlers.IGlobalStorage;
import dk.tohjuler.mcutils.kami.storage.KamiStorage;
import dk.tohjuler.mcutils.kami.storage.TypeItem;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Memory global storage.
 * <br>
 * This storage is not persistent.
 * <br>
 * It is used for testing.
 */
public class MemoryGlobalStorage implements IGlobalStorage {
    private final KamiStorage<String> storage = new KamiStorage<>();

    @Override
    public void load(String id) {
    }

    @Override
    public void save() {
    }

    @Override
    public void set(String key, Object value) {
        storage.set(key, value);
    }

    @Override
    public @NotNull TypeItem<Object> get(String key) {
        return storage.get(key);
    }

    @Override
    public void remove(String key) {
        storage.remove(key);
    }

    @Override
    public void clear() {
        storage.clear();
    }

    @Override
    public Map<String, TypeItem<Object>> getStorage() {
        return storage.getStorage();
    }
}
