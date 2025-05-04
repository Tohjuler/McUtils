package dk.tohjuler.mcutils.placeholder.impl;

import dk.tohjuler.mcutils.placeholder.IPlaceholder;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.OfflinePlayer;

import javax.annotation.Nullable;

/**
 * A placeholder implementation that uses PlaceholderAPI to apply placeholders.
 */
public class PlaceholderAPIHelper implements IPlaceholder {
    @Override
    public String apply(String input, @Nullable OfflinePlayer player) {
        return PlaceholderAPI.setPlaceholders(player, input);
    }
}
