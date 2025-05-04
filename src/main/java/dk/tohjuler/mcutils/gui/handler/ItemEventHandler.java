package dk.tohjuler.mcutils.gui.handler;

import dev.triumphteam.gui.guis.BaseGui;
import dk.tohjuler.mcutils.gui.items.Item;
import dk.tohjuler.mcutils.gui.utils.IStorage;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Abstract class for handling events for a gui item.
 * <br/>
 * Warning: Use of these events can override the item, meaning that it can bypas the config.
 * If there is an event handler in use, then the click action on the item will not be called.
 *
 * @param <T> The type of the gui.
 * @param <S> The type of the storage.
 * @since 1.17.0
 */
public abstract class ItemEventHandler<T extends BaseGui, S extends IStorage> {

    /**
     * Called when the item is created.
     * If you want to modify the item, pls use {@see dk.tohjuler.mcutils.gui.utils.Replacer#modifyItem(Function)}
     * but remember that it bypasses the config.
     * <br/>
     *
     * @param player The player that is opening the gui.
     * @param gui    The gui that is being opened.
     * @param item   The item that is being created.
     * @since 1.17.0
     */
    public abstract void onCreate(Player player, T gui, ItemStack item);

    /**
     * Called when a player clicks on the item.
     * <br/>
     *
     * @param player       The player that clicked.
     * @param wrappedEvent The event that was triggered.
     * @since 1.17.0
     */
    public abstract void onClick(Player player, Item.WrappedInventoryClickEvent<T, S> wrappedEvent);
}
