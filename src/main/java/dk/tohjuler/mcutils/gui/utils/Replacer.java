package dk.tohjuler.mcutils.gui.utils;

import dk.tohjuler.mcutils.items.ItemBuilder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public abstract class Replacer {
    private @Nullable ItemBuilder item;
    private @Nullable String str;

    /**
     * Replace a string in the lore and display name
     * <p>
     * @param regex The string to replace
     * @param replacement The string to replace it with
     */
    protected void replace(String regex, String replacement) {
        if (item != null)
            item.replaceLoreAndDisplayName(regex, replacement);
        else if (str != null)
            str = str.replaceAll(regex, replacement);
    }

    public abstract void replace(Player p);

    /**
     * DON'T USE THIS METHOD
     * It is only used internally
     * <p>
     * @since 1.5.0
     */
    public void replaceCall(ItemBuilder item, Player p) {
        this.item = item;
        replace(p);
    }

    /**
     * DON'T USE THIS METHOD
     * It is only used internally
     * <p>
     * @since 1.5.0
     */
    public void replaceCall(String str, Player p) {
        this.str = str;
        replace(p);
    }
}
