package dk.tohjuler.mcutils.gui.utils;

import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is used to make a single gui item into many.
 * Example: Show all online players.
 * <p>
 * @param <T> The type of the list
 */
public abstract class AsList<T> {
    private final List<T> list;

    public AsList(List<T> list) {
        this.list = list;
    }

    /**
     * This is an internal method, don't use it.
     * <p>
     * @param p The player to call the method on
     * @return The list with the method called on each value
     * @since 1.5.0
     */
    public List<Replacer> call(Player p) {
        return list.stream().map(value -> handle(value, p)).collect(Collectors.toList());
    }

    /**
     * Get calls for every value in the list
     * <p>
     * @param value The value to get the calls for
     * @param p The player to get the calls for
     * @return The replacer for the value
     * @since 1.5.0
     */
    public abstract Replacer handle(T value, Player p);
}
