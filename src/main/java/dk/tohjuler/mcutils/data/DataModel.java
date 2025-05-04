package dk.tohjuler.mcutils.data;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.concurrent.TimeUnit;

public abstract class DataModel<T, S> {
    @Getter
    private final String id;

    private final int timeToSave;
    private final TimeUnit timeUnit;

    public DataModel(String id, int timeToSave, TimeUnit timeUnit, JavaPlugin plugin) {
        this.id = id;
        this.timeToSave = timeToSave;
        this.timeUnit = timeUnit;

        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, this::save, getSaveTime(), getSaveTime());
    }

    public long getSaveTime() {
        return timeUnit.toMillis(timeToSave);
    }

    public abstract T get();
    public abstract void set(S data);
    public abstract boolean load();
    public abstract boolean loadFrom(File path);

    @SuppressWarnings("UnusedReturnValue")
    public abstract boolean save();
    public abstract boolean saveTo(File path);

    public abstract boolean delete();
}
