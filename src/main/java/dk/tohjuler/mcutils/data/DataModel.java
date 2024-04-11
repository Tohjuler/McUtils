package dk.tohjuler.mcutils.data;

import lombok.Getter;

import java.util.concurrent.TimeUnit;

public abstract class DataModel<T, S> {
    @Getter
    private final String id;

    private final int timeToSave;
    private final TimeUnit timeUnit;

    public DataModel(String id, int timeToSave, TimeUnit timeUnit) {
        this.id = id;
        this.timeToSave = timeToSave;
        this.timeUnit = timeUnit;
    }

    public long getSaveTime() {
        return timeUnit.toMillis(timeToSave);
    }

    public abstract T get();
    public abstract void set(S data);
    public abstract boolean load();

    public abstract boolean save();
}
