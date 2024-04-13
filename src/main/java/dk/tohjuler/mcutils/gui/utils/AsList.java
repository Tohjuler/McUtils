package dk.tohjuler.mcutils.gui.utils;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is used to make a single gui item into many.
 * Example: Show all online players.
 * <p>
 * @param <T> The type of the list
 * @since 1.5.0
 */
@Getter
public abstract class AsList<T> {
    private List<T> list;

    /**
     * This is an internal method, don't use it.
     * <p>
     * @param p The player to call the method on
     * @return The list with the method called on each value
     * @since 1.5.0
     */
    public List<Replacer> call(Player p) {
        list = getList(p);
        if (list == null || list.isEmpty()) return Collections.emptyList();

        return list.stream().map(value -> {
            Replacer replacer = handle(value, p);
            if (value instanceof Player) replacer.setPlayer((Player) value);
            return replacer;
        }).collect(Collectors.toList());
    }

    /**
     * Get calls for every value in the list
     * If the {@link T} is a player, that players given in {@link #getList(Player)} will be used in the {@link Replacer#replace(Player)}
     * <p>
     * @param value The value to get the calls for
     * @param p The player to get the calls for
     * @return The replacer for the value
     * @since 1.5.0
     */
    public abstract Replacer handle(T value, Player p);

    /**
     * Get the list of values
     * <p>
     * @param p The player to get the list for
     * @return The list of values
     * @since 1.5.1
     */
    public abstract List<T> getList(Player p);
}
