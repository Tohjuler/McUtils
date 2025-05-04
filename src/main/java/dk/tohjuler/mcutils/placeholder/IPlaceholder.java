package dk.tohjuler.mcutils.placeholder;

import org.bukkit.OfflinePlayer;

import javax.annotation.Nullable;

public interface IPlaceholder {

    /**
     * Apply the placeholder to the input string.
     * <br/>
     *
     * @param input  The input string to apply the placeholder to.
     * @param player The player to apply the placeholder to, can be null.
     * @return The input string with the placeholder applied.
     */
    String apply(String input, @Nullable OfflinePlayer player);

}
