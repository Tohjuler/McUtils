package dk.tohjuler.mcutils.gui;

import dk.tohjuler.mcutils.gui.utils.IStorage;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class GuiManager {
    private final Map<String, ConfigBasedGuiBase<?, ?>> guis = new HashMap<>();
    private final File folder;

    private final JavaPlugin plugin;

    public GuiManager(JavaPlugin plugin, ConfigBasedGuiBase<?, ?>... guis) {
        this.plugin = plugin;
        folder = new File(plugin.getDataFolder(), "guis");
        for (ConfigBasedGuiBase<?, ?> gui : guis) {
            this.guis.put(gui.getId(), gui);
            gui.load(folder);
        }
    }

    public GuiManager(JavaPlugin plugin, File folder, ConfigBasedGuiBase<?, ?>... guis) {
        this.plugin = plugin;
        this.folder = folder;
        for (ConfigBasedGuiBase<?, ?> gui : guis) {
            gui.load(folder);
            this.guis.put(gui.getId(), gui);
        }
    }

    /**
     * Get a gui by id
     * <p>
     *
     * @param p  The player to get the gui for
     * @param id The id of the gui
     * @since 1.5.0
     */
    public void open(Player p, String id) {
        if (!guis.containsKey(id)) {
            return;
        }
        guis.get(id).open(p);
    }

    /**
     * Open a gui, by class
     * <p>
     *
     * @param p   The player to open the gui for
     * @param gui The class of the gui to open
     * @since 1.5.0
     */
    public void open(Player p, Class<? extends ConfigBasedGuiBase<?, ?>> gui) {
        for (ConfigBasedGuiBase<?, ?> g : guis.values())
            if (g.getClass().equals(gui)) {
                g.open(p);
                return;
            }
    }

    /**
     * Get a gui by id
     * <p>
     *
     * @param <S>         The type of the storage
     * @param p           The player to get the gui for
     * @param id          The id of the gui
     * @param initStorage A callback to initialize the storage
     * @since 1.11.0
     */
    public <S extends IStorage> void open(Player p, String id, Consumer<S> initStorage) {
        if (!guis.containsKey(id)) {
            plugin.getLogger().warning("No gui with id " + id + " found");
            return;
        }
        @SuppressWarnings("unchecked")
        Consumer<IStorage> i = (Consumer<IStorage>) initStorage;
        guis.get(id).openByIStorage(p, i);
    }

    /**
     * Open a gui, by class
     * <p>
     *
     * @param <S>         The type of the storage
     * @param p           The player to open the gui for
     * @param gui         The class of the gui to open
     * @param initStorage A callback to initialize the storage
     * @since 1.11.0
     */
    public <S extends IStorage> void open(Player p, Class<? extends ConfigBasedGuiBase<?, S>> gui, Consumer<S> initStorage) {
        for (ConfigBasedGuiBase<?, ? extends IStorage> g : guis.values())
            if (g.getClass().equals(gui)) {
                @SuppressWarnings("unchecked")
                Consumer<IStorage> i = (Consumer<IStorage>) initStorage;
                g.openByIStorage(p, i);
                return;
            }
        plugin.getLogger().warning("No gui with class " + gui.getName() + " found");
    }

    /**
     * Reload all guis
     *
     * @since 1.5.0
     */
    public void reload() {
        guis.values().forEach(gui -> gui.load(folder));
    }
}
