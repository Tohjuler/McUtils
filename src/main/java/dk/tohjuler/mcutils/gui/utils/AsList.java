package dk.tohjuler.mcutils.gui.utils;

import dev.triumphteam.gui.guis.BaseGui;
import dk.tohjuler.mcutils.gui.items.Item;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * This class is used to make a single gui item into many.
 * Example: Show all online players.
 * <br/>
 *
 * @param <T>   The type of the list.
 * @param <GUI> The type of the gui.
 * @param <S>   The type of the storage.
 * @since 1.5.0
 */
@Getter
public abstract class AsList<T, GUI extends BaseGui, S extends IStorage> {
    private @Nullable List<T> list;

    /**
     * This is an internal method, don't use it.
     * <br/>
     *
     * @param p            The player to call the method on
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
                    return new Holder<GUI, S>(() -> handle(value, p), (player, event) -> clickAction(player, event, value), show(p, localStorage, value));
                })
                .collect(Collectors.toList());
    }

    /**
     * Get calls for every value in the list
     * If the {@link T} is a player, that player will be used in {@link #getList(Player, IStorage)} and {@link Replacer#replace(Player, IStorage)}
     * <br/>
     *
     * @param value The value to get the calls for
     * @param p     The player to get the calls for
     * @return The replacer for the value
     * @since 1.5.0
     */
    public abstract Replacer<S> handle(T value, Player p);

    /**
     * Get the list of values
     * <br/>
     *
     * @param p            The player to get the list for
     * @param localStorage The local storage
     * @return The list of values
     * @since 1.5.1
     */
    public abstract @Nullable List<T> getList(Player p, S localStorage);

    /**
     * The action to run when the item is clicked
     * <br/>
     *
     * @param player       The player that clicked the item
     * @param wrappedEvent The wrapped event
     * @param value        The value of the asList
     * @since 1.12.0
     */
    public abstract void clickAction(Player player, Item.WrappedInventoryClickEvent<GUI, S> wrappedEvent, T value);

    /**
     * Check if the item should be shown.
     * <br/>
     *
     * @param player       The player to check for
     * @param localStorage The local storage
     * @param value        The value to check for
     * @return If the item should be shown
     * @since 1.19.0
     */
    public boolean show(Player player, S localStorage, T value) {
        return true;
    }

    /**
     * A holder for the replacer and the click action.
     * <br/>
     *
     * @param <GUI> The type of the gui.
     * @param <S>   The type of the storage.
     */
    public static class Holder<GUI extends BaseGui, S extends IStorage> {
        private final Supplier<Replacer<S>> getReplacer;
        @Getter
        private final BiConsumer<Player, Item.WrappedInventoryClickEvent<GUI, S>> callback;

        @Getter
        private final boolean show;

        /**
         * Create a new holder
         * <br/>
         *
         * @param getReplacer The replacer
         * @param callback    The click action
         * @param show        If the item should be shown
         */
        public Holder(Supplier<Replacer<S>> getReplacer, BiConsumer<Player, Item.WrappedInventoryClickEvent<GUI, S>> callback, boolean show) {
            this.callback = callback;
            this.getReplacer = getReplacer;
            this.show = show;
        }

        public Replacer<S> getReplacer() {
            return getReplacer.get();
        }
    }
}
