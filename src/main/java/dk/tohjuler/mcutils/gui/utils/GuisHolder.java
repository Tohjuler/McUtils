package dk.tohjuler.mcutils.gui.utils;

import com.cryptomorin.xseries.XSound;
import dev.triumphteam.gui.guis.BaseGui;
import dk.tohjuler.mcutils.gui.ConfigBasedGuiBase;
import dk.tohjuler.mcutils.gui.GuiManager;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

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
    protected @Nullable XSound clickSound = XSound.UI_BUTTON_CLICK;

    public GuisHolder(JavaPlugin plugin) {
        load(new File(plugin.getDataFolder(), "guis"));
    }

    public GuisHolder(File folder) {
        load(folder);
    }

    /**
     * Load all the guis, defined in the class as fields.
     * <br/>
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
                        gui.getGuiEventHandler().addOnCreate(this::onCreate);
                        gui.getGuiEventHandler().addOnClose(this::onClose);
                        gui.getGuiEventHandler().addDefaultClick(this::defaultClick);

                        if (clickSound != null)
                            gui.getGuiEventHandler().addDefaultClick((player, gui1, event, storage) -> {
                                if (event.isCancelled()) return;
                                if (clickSound == null) return;
                                clickSound.play(player);
                            });

                        gui.load(folder);
                    } catch (IllegalAccessException e) {
                        new RuntimeException("Could not load gui", e).printStackTrace();
                    }
                });
    }

    /**
     * Open a gui by class.
     * <br/>
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

    /**
     * Called right before opening the gui.
     * Remember the onCreate event on the gui is call before this.
     * This is a global event for all guis.
     * <br/>
     *
     * @param player       The player that is opening the gui.
     * @param gui          The gui that is being opened.
     * @param localStorage The storage that is being used.
     * @since 1.20.0
     */
    public void onCreate(Player player, BaseGui gui, IStorage localStorage) {
    }

    /**
     * Called when the gui is closed.
     * Remember the onClose event on the gui is call before this.
     * This is a global event for all guis.
     * <br/>
     *
     * @param player       The player that is closing the gui.
     * @param gui          The gui that is being closed.
     * @param localStorage The storage that is being used.
     * @since 1.20.0
     */
    public void onClose(Player player, BaseGui gui, IStorage localStorage) {
    }

    /**
     * Called when a player clicks in the gui.
     * Remember if there is a click action on the item, then it will also be called.
     * Remember the default click event on the gui is call before this.
     * This is a global event for all guis.
     * <br/>
     *
     * @param player       The player that clicked.
     * @param gui          The gui that was clicked in.
     * @param event        The event that was triggered.
     * @param localStorage The storage that is being used.
     * @since 1.20.0
     */
    public void defaultClick(Player player, BaseGui gui, InventoryClickEvent event, IStorage localStorage) {
        event.setCancelled(true);
    }
}
