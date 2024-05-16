package dk.tohjuler.mcutils.gui.utils;

import dev.triumphteam.gui.guis.BaseGui;
import dk.tohjuler.mcutils.gui.items.Item;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * This class is used to make a single gui item into many.
 * Example: Show all online players.
 * <p>
 *
 * @param <T>   The type of the list.
 * @param <GUI> The type of the gui.
 * @since 1.5.0
 */
@Getter
public abstract class AsList<T, GUI extends BaseGui, S extends IStorage> {
    private List<T> list;

    /**
     * This is an internal method, don't use it.
     * <p>
     *
     * @param p The player to call the method on
     * @param localStorage The local storage
     * @return The list with the method called on each value
     * @since 1.5.0
     */
    public List<Holder<GUI, S>> call(Player p, S localStorage) {
        list = getList(p, localStorage);
        if (list == null || list.isEmpty()) return Collections.emptyList();

        return list.stream().map(value -> {
            Replacer<S> replacer = handle(value, p);
            if (value instanceof Player) replacer.setPlayer((Player) value);
            return new Holder<GUI, S>(() -> handle(value, p), (player, event) -> clickAction(player, event, value));
        }).collect(Collectors.toList());
    }

    /**
     * Get calls for every value in the list
     * If the {@link T} is a player, that players given in {@link #getList(Player, S)} will be used in the {@link Replacer#replace(Player, S)}
     * <p>
     *
     * @param value The value to get the calls for
     * @param p     The player to get the calls for
     * @return The replacer for the value
     * @since 1.5.0
     */
    public abstract Replacer<S> handle(T value, Player p);

    /**
     * Get the list of values
     * <p>
     *
     * @param p The player to get the list for
     * @param localStorage The local storage
     * @return The list of values
     * @since 1.5.1
     */
    public abstract List<T> getList(Player p, S localStorage);

    /**
     * The action to run when the item is clicked
     * <p>
     *
     * @param player       The player that clicked the item
     * @param wrappedEvent The wrapped event
     * @param value        The value of the asList
     * @since 1.12.0
     */
    public abstract void clickAction(Player player, Item.WrappedInventoryClickEvent<GUI, S> wrappedEvent, T value);

    public static class Holder<GUI extends BaseGui, S extends IStorage> {
        private final Supplier<Replacer<S>> getReplacer;
        @Getter
        private final BiConsumer<Player, Item.WrappedInventoryClickEvent<GUI, S>> callback;

        public Holder(Supplier<Replacer<S>> getReplacer, BiConsumer<Player, Item.WrappedInventoryClickEvent<GUI, S>> callback) {
            this.callback = callback;
            this.getReplacer = getReplacer;
        }

        public Replacer<S> getReplacer() {
            return getReplacer.get();
        }
    }
}
