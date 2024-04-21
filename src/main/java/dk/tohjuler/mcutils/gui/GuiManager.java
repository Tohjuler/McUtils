package dk.tohjuler.mcutils.gui;

import dk.tohjuler.mcutils.gui.utils.Storage;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class GuiManager {
    private final Map<String, ConfigBasedGuiBase<?>> guis = new HashMap<>();
    private final File folder;

    public GuiManager(JavaPlugin plugin, ConfigBasedGuiBase<?>... guis) {
        folder = new File(plugin.getDataFolder(), "guis");
        for (ConfigBasedGuiBase<?> gui : guis) {
            this.guis.put(gui.getId(), gui);
            gui.load(folder);
        }
    }

    public GuiManager(JavaPlugin plugin, File folder, ConfigBasedGuiBase<?>... guis) {
        this.folder = folder;
        for (ConfigBasedGuiBase<?> gui : guis) {
            gui.load(folder);
            this.guis.put(gui.getId(), gui);
        }
    }

    /**
     * Get a gui by id
     * <p>
     * @param p The player to get the gui for
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
     * @param p The player to open the gui for
     * @param gui The class of the gui to open
     * @since 1.5.0
     */
    public void open(Player p, Class<?> gui) {
        for (ConfigBasedGuiBase<?> g : guis.values())
            if (g.getClass().equals(gui)) {
                g.open(p);
                return;
            }
    }

    /**
     * Get a gui by id
     * <p>
     * @param p The player to get the gui for
     * @param id The id of the gui
     * @param initStorage A callback to initialize the storage
     * @since 1.11.0
     */
    public void open(Player p, String id, Consumer<Storage> initStorage) {
        if (!guis.containsKey(id)) {
            return;
        }
        guis.get(id).open(p, initStorage);
    }

    /**
     * Open a gui, by class
     * <p>
     * @param p The player to open the gui for
     * @param gui The class of the gui to open
     * @param initStorage A callback to initialize the storage
     * @since 1.11.0
     */
    public void open(Player p, Class<?> gui, Consumer<Storage> initStorage) {
        for (ConfigBasedGuiBase<?> g : guis.values())
            if (g.getClass().equals(gui)) {
                g.open(p, initStorage);
                return;
            }
    }

    /**
     * Reload all guis
     * @since 1.5.0
     */
    public void reload() {
        guis.values().forEach(gui -> gui.load(folder));
    }
}
