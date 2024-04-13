package dk.tohjuler.mcutils.gui.items;

import dk.tohjuler.mcutils.gui.Gui;
import dk.tohjuler.mcutils.items.ItemBuilder;
import org.bukkit.entity.Player;

import java.util.function.Function;

/**
 * A static item is an item that doesn't get saved to the config.
 * @since 1.5
 */
public class StaticItem extends Item{
    private final Function<Player, ItemBuilder> func;

    public StaticItem(Gui gui, int slot, Function<Player, ItemBuilder> func) {
        super(gui, null, slot, null);
        this.func = func;
    }

    public ItemBuilder getItem(Player p) {
        return func.apply(p);
    }
}
