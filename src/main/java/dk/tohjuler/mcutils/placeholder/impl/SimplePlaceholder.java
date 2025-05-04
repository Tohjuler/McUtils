package dk.tohjuler.mcutils.placeholder.impl;

import dk.tohjuler.mcutils.placeholder.IPlaceholder;
import org.bukkit.OfflinePlayer;

import javax.annotation.Nullable;

/**
 * A simple placeholder implementation that replaces a placeholder in a string with a value.
 * Allows the use of regex for the placeholder.
 */
public class SimplePlaceholder implements IPlaceholder {
    private final String placeholder;
    private final String value;

    /**
     * Create a new SimplePlaceholder.
     * <br/>
     *
     * @param placeholder The placeholder to replace.
     * @param value       The value to replace the placeholder with.
     */
    public SimplePlaceholder(String placeholder, String value) {
        this.placeholder = placeholder;
        this.value = value;
    }

    @Override
    public String apply(String input, @Nullable OfflinePlayer player) {
        return input.replaceAll(placeholder, value);
    }
}
