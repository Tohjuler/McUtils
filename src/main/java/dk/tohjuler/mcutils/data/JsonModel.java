package dk.tohjuler.mcutils.data;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.logging.Level;

public class JsonModel<T> extends DataModel<T, T> {

    private final JavaPlugin plugin;

    private T data;
    private Function<Object, T> serializer = null;
    private Type type;

    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .serializeNulls()
            .create();

    public JsonModel(String id, int timeToSave, TimeUnit timeUnit, JavaPlugin plugin) {
        super(id, timeToSave, timeUnit, plugin);
        this.plugin = plugin;
        load();
    }

    public JsonModel(String id, int timeToSave, TimeUnit timeUnit, Type type, JavaPlugin plugin) {
        super(id, timeToSave, timeUnit, plugin);
        this.plugin = plugin;
        this.type = type;
        load();
    }

    public JsonModel(String id, int timeToSave, TimeUnit timeUnit, Function<Object, T> serializer, JavaPlugin plugin) {
        super(id, timeToSave, timeUnit, plugin);
        this.serializer = serializer;
        this.plugin = plugin;
        load();
    }

    @Override
    public T get() {
        return data;
    }

    @Override
    public void set(T data) {
        this.data = data;
    }

    @Override
    public boolean loadFrom(File file) {
        try {
            if (!file.exists()) return false;
            BufferedReader reader = Files.newBufferedReader(file.toPath());
            data = gson.fromJson(
                    reader,
                    type != null
                            ? type
                            : new TypeToken<T>(getClass()) {
                    }.getType()
            );

            if (serializer != null) data = serializer.apply(data);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Loads the data from `data/{id}.json`
     * <br/>
     *
     * @return true if the data was loaded successfully
     * @since 1.0.0
     */
    @Override
    public boolean load() {
        File dataFolder = new File(plugin.getDataFolder(), "data");
        if (!dataFolder.exists()) return false;

        File file = new File(dataFolder, getId() + ".json");

        return loadFrom(file);
    }

    /**
     * Saves the data to a file
     * <br/>
     *
     * @param file The file to save to
     * @return true if the data was saved successfully
     * @since 1.0.0
     */
    @Override
    public boolean saveTo(File file) {
        try {
            file.createNewFile();
            Writer writer = new FileWriter(file, false);
            gson.toJson(data, writer);
            writer.flush();
            writer.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Saves the data to `data/{id}.json`
     * <br/>
     *
     * @return true if the data was saved successfully
     * @since 1.0.0
     */
    @Override
    public boolean save() {
        File dataFolder = new File(plugin.getDataFolder(), "data");
        dataFolder.mkdirs();

        if (data == null) {
            plugin.getLogger().log(Level.WARNING, "Failed to save data model " + getId() + " because data is null");
            return false;
        }

        File file = new File(dataFolder, getId() + ".json");

        return saveTo(file);
    }

    /**
     * Deletes the data file `data/{id}.json` and sets the data to null
     * <br/>
     *
     * @return true if the data was deleted successfully
     */
    @Override
    public boolean delete() {
        File dataFolder = new File(plugin.getDataFolder(), "data");
        if (!dataFolder.exists()) return false;

        File file = new File(dataFolder, getId() + ".json");

        set(null);
        return file.delete();
    }
}
