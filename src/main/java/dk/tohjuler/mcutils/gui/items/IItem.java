package dk.tohjuler.mcutils.gui.items;

import dev.triumphteam.gui.guis.BaseGui;
import dk.tohjuler.mcutils.config.ConfigurationFile;
import dk.tohjuler.mcutils.gui.utils.IStorage;
import org.bukkit.entity.Player;

/**
 * An item interface, for items in the gui.
 * <br/>
 *
 * @param <T> The type of the gui
 * @param <S> The type of the storage
 */
public interface IItem<T extends BaseGui, S extends IStorage> {
    /**
     * Set up the gui, with the item.
     * <br/>
     *
     * @param gui          The gui
     * @param p            The player
     * @param localStorage The local storage
     * @since 1.18.0
     */
    void setupGui(T gui, Player p, S localStorage);

    /**
     * Save the item to the config
     * <br/>
     *
     * @param cf The config file
     * @since 1.18.0
     */
    void save(ConfigurationFile cf);

    /**
     * Load extra data from the config
     * <br/>
     *
     * @param cf       The config file
     * @param basePath The base path
     * @since 1.18.0
     */
    void loadExtra(ConfigurationFile cf, String basePath);
}
