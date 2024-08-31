package dk.tohjuler.mcutils.gui.items;

import dev.triumphteam.gui.guis.BaseGui;
import dk.tohjuler.mcutils.config.ConfigurationFile;
import dk.tohjuler.mcutils.gui.ConfigBasedGuiBase;
import dk.tohjuler.mcutils.gui.utils.IStorage;
import dk.tohjuler.mcutils.items.ItemBuilder;
import org.bukkit.entity.Player;

import java.util.function.Function;

/**
 * A static item is an item that doesn't get saved to the config.
 *
 * @param <T> The type of the gui.
 * @param <S> The type of the storage.
 * @since 1.5
 */
public class StaticItem<T extends BaseGui, S extends IStorage> extends Item<T, S> {
    private final Function<Player, ItemBuilder> func;

    public StaticItem(ConfigBasedGuiBase<T, S> gui, int slot, Function<Player, ItemBuilder> func) {
        super(gui, null, slot, null);
        this.func = func;
    }

    public ItemBuilder getItem(Player p) {
        return func.apply(p);
    }

    @Override
    public void setupGui(T gui, Player p, S localStorage) {
        if (checkShow(p, localStorage)) {
            if (parseSlotFirst() == -1)
                gui.addItem(getItem(p).buildAsGuiItem(
                        e -> call(p, gui, e, localStorage)
                ));
            else
                for (int slot : parseSlot())
                    gui.setItem(slot, getItem(p).buildAsGuiItem(
                            e -> call(p, gui, e, localStorage)
                    ));
        } else if (getFallbackItem() != null) // Fallback items
            if (parseSlotFirst() == -1)
                gui.addItem(buildFallback(localStorage, p,
                        e -> call(p, gui, e, localStorage),
                        gui
                ));
            else
                for (int slot : parseSlot())
                    gui.setItem(slot, buildFallback(localStorage, p,
                            e -> call(p, gui, e, localStorage),
                            gui
                    ));
    }

    @Override
    public void save(ConfigurationFile cf) {
    }

    @Override
    public void loadExtra(ConfigurationFile cf, String basePath) {
    }
}
