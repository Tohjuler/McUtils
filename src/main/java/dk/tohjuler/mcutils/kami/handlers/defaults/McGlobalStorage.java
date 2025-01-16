package dk.tohjuler.mcutils.kami.handlers.defaults;

import com.google.common.reflect.TypeToken;
import dk.tohjuler.mcutils.data.JsonModel;
import dk.tohjuler.mcutils.kami.handlers.IGlobalStorage;
import dk.tohjuler.mcutils.kami.storage.TypeItem;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Default global storage.
 */
public class McGlobalStorage implements IGlobalStorage {
    private final JavaPlugin plugin;
    private JsonModel<Map<String, TypeItem<Object>>> storage;

    public McGlobalStorage(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    @SuppressWarnings("UnstableApiUsage")
    public void load(String id) {
        storage = new JsonModel<>(
                "kami-global-storage" + (id != null ? "-" + id : ""),
                5,
                TimeUnit.MINUTES,
                new TypeToken<Map<String, TypeItem<Object>>>() {
                }.getType(),
                plugin
        );
        if (storage.get() == null) storage.set(new HashMap<>());
    }

    @Override
    public void save() {
        if (storage != null) storage.save();
    }

    @Override
    public void set(String key, Object value) {
        if (storage == null) return;

        Map<String, TypeItem<Object>> map = storage.get();
        map.put(key, new TypeItem<>(value));
        storage.set(map);
    }

    @Override
    public @NotNull TypeItem<Object> get(String key) {
        if (storage == null) return new TypeItem<>(null);
        return storage.get().getOrDefault(key, new TypeItem<>(null));
    }

    @Override
    public void remove(String key) {
        if (storage == null) return;

        Map<String, TypeItem<Object>> map = storage.get();
        map.remove(key);
        storage.set(map);
    }

    @Override
    public void clear() {
        if (storage == null) return;
        storage.set(new HashMap<>());
    }

    @Override
    public Map<String, TypeItem<Object>> getStorage() {
        if (storage == null) return Collections.emptyMap();
        return storage.get();
    }
}
