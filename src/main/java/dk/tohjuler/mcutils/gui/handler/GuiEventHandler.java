package dk.tohjuler.mcutils.gui.handler;

import dev.triumphteam.gui.guis.BaseGui;
import dk.tohjuler.mcutils.QuadConsumer;
import dk.tohjuler.mcutils.TriConsumer;
import dk.tohjuler.mcutils.gui.utils.IStorage;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.List;

@Getter
public class GuiEventHandler<T extends BaseGui, S extends IStorage>{
    private final List<TriConsumer<Player, T, S>> onCreate = new ArrayList<>();
    private final List<TriConsumer<Player, T, S>> onClose = new ArrayList<>();
    private final List<QuadConsumer<Player, T, InventoryClickEvent, S>> defaultClick = new ArrayList<>();

    public void callOnCreate(Player player, T gui, S storage) {
        onCreate.forEach(consumer -> consumer.accept(player, gui, storage));
    }

    public void callOnClose(Player player, T gui, S storage) {
        onClose.forEach(consumer -> consumer.accept(player, gui, storage));
    }

    public void callDefaultClick(Player player, T gui, InventoryClickEvent event, S storage) {
        defaultClick.forEach(consumer -> consumer.accept(player, gui, event, storage));
    }

    public GuiEventHandler<T, S> addOnCreate(TriConsumer<Player, T, S> consumer) {
        onCreate.add(consumer);
        return this;
    }

    public GuiEventHandler<T, S> addOnClose(TriConsumer<Player, T, S> consumer) {
        onClose.add(consumer);
        return this;
    }

    public GuiEventHandler<T, S> addDefaultClick(QuadConsumer<Player, T, InventoryClickEvent, S> consumer) {
        defaultClick.add(consumer);
        return this;
    }
}
