package dk.tohjuler.mcutils.gui.items;

import dev.triumphteam.gui.guis.BaseGui;
import dk.tohjuler.mcutils.gui.ConfigBasedGuiBase;
import dk.tohjuler.mcutils.items.ItemBuilder;
import org.bukkit.entity.Player;

import java.util.function.Function;

/**
 * A static item is an item that doesn't get saved to the config.
 *
 * @since 1.5
 */
public class StaticItem<T extends BaseGui> extends Item<T> {
    private final Function<Player, ItemBuilder> func;

    public StaticItem(ConfigBasedGuiBase<T> gui, int slot, Function<Player, ItemBuilder> func) {
        super(gui, null, slot, null);
        this.func = func;
    }

    public ItemBuilder getItem(Player p) {
        return func.apply(p);
    }
}
