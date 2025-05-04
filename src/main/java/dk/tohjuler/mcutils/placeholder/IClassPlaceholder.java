package dk.tohjuler.mcutils.placeholder;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Nullable;

public interface IClassPlaceholder<T> {

    /**
     * Apply the placeholder to the input string.
     * <br/>
     *
     * @param instance The instance of the class to apply the placeholder from.
     * @param input    The input string to apply the placeholder to.
     * @param player   The player to apply the placeholder to, can be null.
     * @return The input string with the placeholder applied.
     */
    String apply(T instance, String input, @Nullable OfflinePlayer player);

    /**
     * A wrapper for the apply method, that takes an object instead of a class.
     * If the object is not an instance of the class, it will return the input string.
     * <br/>
     *
     * @param instance The instance of the class to apply the placeholder from.
     * @param input    The input string to apply the placeholder to.
     * @param player   The player to apply the placeholder to, can be null.
     * @return The input string with the placeholder applied.
     */
    default String applyFromObj(Object instance, String input, @Nullable OfflinePlayer player) {
        if (instance == null) return input;

        try {
            //noinspection unchecked
            return apply((T) instance, input, player);
        } catch (Exception e) {
            return input;
        }
    }
}
