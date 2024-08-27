package dk.tohjuler.mcutils.gui.utils;

import dk.tohjuler.mcutils.items.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Replacer<S extends IStorage> {
    private @Nullable ItemBuilder item;
    private @Nullable String str;

    @Getter
    @Setter
    private @Nullable Player player;

    /**
     * Replace a string in the lore and display name
     * <p>
     *
     * @param regex The string to replace
     * @param func  The function to replace the string with
     */
    protected void replace(String regex, Function<String, String> func) {
        if (item != null)
            item = item.replaceAllFromGui(regex, func);
        else if (str != null)
            str = replaceInString(str, regex, func);
    }

    /**
     * Modify the item, out of the normal methods.
     * If an "adv:" is used in the material of the item, it can override this method.
     * WARNING: This method is not recommended to use, as it goes out of the config system
     * and allows static modifications (Modifications that can't be changed by config).
     * <p>
     *
     * @param func The function to modify the item with
     * @since 1.5.2
     */
    protected void modifyItem(Function<ItemBuilder, ItemBuilder> func) {
        if (item != null)
            item = func.apply(item);
    }

    public abstract void replace(Player p, S localStorage);

    /**
     * This method is for internal use.
     * <p>
     *
     * @param storage The storage to use
     * @param item    The item to replace in
     * @param p       The player to replace for
     * @return The item with the replaced strings
     * @since 1.5.0
     */
    public ItemBuilder replaceCall(S storage, ItemBuilder item, Player p) {
        this.item = item;
        this.str = null;
        replace(p, storage);
        return this.item;
    }

    /**
     * This method is for internal use.
     * <p>
     *
     * @param storage The storage to use
     * @param str     The string to replace in
     * @param p       The player to replace for
     * @return The string with the replaced strings
     * @since 1.5.0
     */
    public String replaceCall(S storage, String str, Player p) {
        this.str = str;
        this.item = null;
        replace(p, storage);
        return this.str;
    }

    /**
     * Replace a regex in a string.
     * <p>
     *
     * @param str     The string to replace in
     * @param regex   The regex to replace
     * @param func    The function to replace the string with
     * @return The string with the replaced regex
     * @since 1.5.0
     */
    public static String replaceInString(String str, String regex, Function<String, String> func) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);

        if (!matcher.find()) return str;
        String matchedPart = matcher.group();
        String res = func.apply(matchedPart);
        if (res == null) res = "REPLACE_ERROR";

        return str.replace(matchedPart, res);
    }
}
