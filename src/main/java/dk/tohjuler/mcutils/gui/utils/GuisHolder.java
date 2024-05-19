package dk.tohjuler.mcutils.gui.utils;

import dk.tohjuler.mcutils.gui.ConfigBasedGuiBase;
import dk.tohjuler.mcutils.gui.GuiManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * Use this to create a class that holds all the guis.
 * This is only necessary if you want custom storage's for the guis,
 * else use {@link GuiManager}.
 *
 * @since 1.18.0
 */
public abstract class GuisHolder {
    public GuisHolder(JavaPlugin plugin) {
        load(new File(plugin.getDataFolder(), "guis"));
    }

    public GuisHolder(File folder) {
        load(folder);
    }

    /**
     * Load all the guis, defined in the class as fields.
     * <p>
     *
     * @param folder The folder for the gui's config files
     * @since 1.18.0
     */
    public void load(File folder) {
        Arrays.stream(getClass().getDeclaredFields())
                .filter(field -> field.getType().isAssignableFrom(ConfigBasedGuiBase.class))
                .forEach(field -> {
                    try {
                        field.setAccessible(true);
                        ConfigBasedGuiBase<?, ?> gui = (ConfigBasedGuiBase<?, ?>) field.get(this);
                        gui.load(folder);
                    } catch (IllegalAccessException e) {
                        new RuntimeException("Could not load gui", e).printStackTrace();
                    }
                });
    }

    /**
     * Open a gui by class.
     * <p>
     *
     * @param p     The player
     * @param clazz The class of the gui
     * @since 1.18.0
     */
    public void open(Player p, Class<? extends ConfigBasedGuiBase<?, ?>> clazz) {
        Field field = Arrays.stream(getClass().getDeclaredFields())
                .filter(field1 -> clazz.isAssignableFrom(field1.getType()))
                .findFirst()
                .orElse(null);
        if (field == null) {
            new ClassNotFoundException("Could not find gui: " + clazz.getName()).printStackTrace();
            return;
        }
        try {
            field.setAccessible(true);
            ConfigBasedGuiBase<?, ?> gui = (ConfigBasedGuiBase<?, ?>) field.get(this);
            gui.open(p);
        } catch (IllegalAccessException e) {
            new RuntimeException("Could not open gui", e).printStackTrace();
        }
    }
}
